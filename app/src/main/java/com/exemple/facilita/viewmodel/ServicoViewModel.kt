package com.exemple.facilita.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exemple.facilita.data.api.ServicoApiService
import com.exemple.facilita.data.models.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServicoViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: ServicoApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://servidor-facilita.onrender.com/v1/facilita/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ServicoApiService::class.java)
    }

    private val _servico = MutableStateFlow<Servico?>(null)
    val servico: StateFlow<Servico?> = _servico.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var pollingJob: Job? = null

    // Inicia o polling para verificar status do serviço
    fun iniciarMonitoramento(token: String, servicoId: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    buscarServicoPorId(token, servicoId.toIntOrNull() ?: 0)

                    // Intervalo de 5 segundos entre requisições
                    delay(5000)

                    // Para o polling se o serviço foi concluído ou cancelado
                    val status = _servico.value?.status
                    if (status == "CONCLUIDO" || status == "CANCELADO") {
                        break
                    }
                } catch (e: Exception) {
                    Log.e("ServicoViewModel", "Erro no polling: ${e.message}", e)
                    delay(5000) // Espera 5 segundos antes de tentar novamente
                }
            }
        }
    }

    // Para o monitoramento
    fun pararMonitoramento() {
        pollingJob?.cancel()
        pollingJob = null
    }

    // Busca serviço específico por ID
    private suspend fun buscarServicoPorId(token: String, servicoId: Int) {
        try {
            Log.d("ServicoViewModel", "🔄 Buscando serviço ID: $servicoId")

            // Busca todos os serviços do usuário
            val response = apiService.meusServicos("Bearer $token")

            if (response.isSuccessful && response.body()?.statusCode == 200) {
                val servicos = response.body()?.data

                // Encontra o serviço específico
                val servicoEncontrado = servicos?.find { it.id == servicoId }

                if (servicoEncontrado != null) {
                    _servico.value = servicoEncontrado
                    _error.value = null

                    Log.d("ServicoViewModel", "✅ Serviço atualizado: Status=${servicoEncontrado.status}")

                    // Log da localização do prestador se existir
                    servicoEncontrado.prestador?.let { prestador ->
                        if (prestador.latitudeAtual != null && prestador.longitudeAtual != null) {
                            Log.d("ServicoViewModel", "📍 Prestador em: ${prestador.latitudeAtual}, ${prestador.longitudeAtual}")
                        }
                    }
                } else {
                    _error.value = "Serviço não encontrado"
                    Log.e("ServicoViewModel", "❌ Serviço ID $servicoId não encontrado na lista")
                }
            } else {
                _error.value = "Erro ao buscar serviço: ${response.code()}"
                Log.e("ServicoViewModel", "❌ Erro na resposta: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            _error.value = "Erro de conexão: ${e.message}"
            Log.e("ServicoViewModel", "❌ Exceção ao buscar serviço", e)
        }
    }

    // Cancelar serviço
    fun cancelarServico(token: String, servicoId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.cancelarServico("Bearer $token", servicoId)

                if (response.isSuccessful && response.body()?.statusCode == 200) {
                    _servico.value = response.body()?.data
                    pararMonitoramento()
                    Log.d("ServicoViewModel", "✅ Serviço cancelado com sucesso")
                    onSuccess()
                } else {
                    val erro = response.body()?.message ?: "Erro ao cancelar serviço"
                    _error.value = erro
                    Log.e("ServicoViewModel", "❌ Erro ao cancelar: $erro")
                    onError(erro)
                }
            } catch (e: Exception) {
                val erro = "Erro de conexão: ${e.message}"
                _error.value = erro
                Log.e("ServicoViewModel", "❌ Exceção ao cancelar", e)
                onError(erro)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Limpar erro
    fun limparErro() {
        _error.value = null
    }

    // Calcular tempo estimado de chegada
    fun calcularTempoEstimado(): Int {
        val servico = _servico.value ?: return 0

        // Se tem tempo estimado da API, usa ele
        servico.tempoEstimado?.let {
            return it
        }

        val prestador = servico.prestador ?: return 0
        val localizacao = servico.localizacao

        // Se não tem localização, retorna estimativa padrão
        if (prestador.latitudeAtual == null || prestador.longitudeAtual == null ||
            localizacao?.latitude == null || localizacao.longitude == null) {
            return when (servico.status) {
                "ACEITO" -> 10
                "EM_ANDAMENTO" -> 5
                else -> 0
            }
        }

        // Calcular distância aproximada (fórmula simplificada)
        val latDiff = Math.abs(prestador.latitudeAtual - localizacao.latitude)
        val lonDiff = Math.abs(prestador.longitudeAtual - localizacao.longitude)
        val distancia = Math.sqrt(latDiff * latDiff + lonDiff * lonDiff)

        // Estimativa: 1 grau ≈ 111km, velocidade média 30km/h
        val tempoHoras = (distancia * 111) / 30
        val tempoMinutos = (tempoHoras * 60).toInt()

        return maxOf(1, tempoMinutos) // Mínimo 1 minuto
    }

    override fun onCleared() {
        super.onCleared()
        pararMonitoramento()
    }
}

