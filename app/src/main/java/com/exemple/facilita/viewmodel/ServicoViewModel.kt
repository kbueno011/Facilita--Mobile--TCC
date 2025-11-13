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

        // Converte servicoId para Int com validação
        val idServico = servicoId.toIntOrNull()
        if (idServico == null || idServico == 0) {
            _error.value = "ID do serviço inválido: $servicoId"
            Log.e("ServicoViewModel", "❌ ID inválido recebido: '$servicoId'")
            return
        }

        Log.d("ServicoViewModel", "🚀 Iniciando monitoramento do serviço ID: $idServico")

        pollingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    buscarServicoPorId(token, idServico)

                    // Intervalo de 10 segundos entre requisições (conforme API)
                    delay(10000)

                    // Para o polling se o serviço foi concluído ou cancelado
                    val status = _servico.value?.status
                    if (status == "CONCLUIDO" || status == "CANCELADO") {
                        Log.d("ServicoViewModel", "⏹️ Parando monitoramento - Status final: $status")
                        break
                    }
                } catch (e: Exception) {
                    Log.e("ServicoViewModel", "Erro no polling: ${e.message}", e)
                    delay(10000) // Espera 10 segundos antes de tentar novamente
                }
            }
        }
    }

    // Para o monitoramento
    fun pararMonitoramento() {
        pollingJob?.cancel()
        pollingJob = null
    }

    // Busca serviço específico por ID usando busca por status
    private suspend fun buscarServicoPorId(token: String, servicoId: Int) {
        try {
            Log.d("ServicoViewModel", "🔄 Buscando serviço ID: $servicoId")

            // Lista de status possíveis para buscar (em ordem de prioridade)
            val statusPossiveis = listOf("EM_ANDAMENTO", "ACEITO", "PENDENTE", "AGUARDANDO")
            var servicoEncontrado: com.exemple.facilita.data.models.ServicoPedido? = null

            // Tenta buscar em cada status até encontrar o serviço
            for (status in statusPossiveis) {
                try {
                    val response = apiService.buscarServicosPorStatus("Bearer $token", status)

                    if (response.isSuccessful && response.body()?.statusCode == 200) {
                        val pedidos = response.body()?.data?.pedidos

                        // Procura o serviço específico pelo ID
                        servicoEncontrado = pedidos?.find { it.id == servicoId }

                        if (servicoEncontrado != null) {
                            Log.d("ServicoViewModel", "✅ Serviço encontrado com status: $status")
                            break
                        }
                    }
                } catch (e: Exception) {
                    Log.w("ServicoViewModel", "⚠️ Erro ao buscar status $status: ${e.message}")
                    continue
                }
            }

            if (servicoEncontrado != null) {
                // Converte ServicoPedido para Servico
                _servico.value = converterParaServico(servicoEncontrado)
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
                Log.e("ServicoViewModel", "❌ Serviço ID $servicoId não encontrado em nenhum status")
            }
        } catch (e: Exception) {
            _error.value = "Erro de conexão: ${e.message}"
            Log.e("ServicoViewModel", "❌ Exceção ao buscar serviço", e)
        }
    }

    // Converte ServicoPedido para Servico
    private fun converterParaServico(pedido: com.exemple.facilita.data.models.ServicoPedido): com.exemple.facilita.data.models.Servico {
        return com.exemple.facilita.data.models.Servico(
            id = pedido.id,
            idContratante = 0, // Não disponível no ServicoPedido
            idPrestador = pedido.prestador?.id,
            idCategoria = pedido.categoria?.id ?: 0,
            descricao = pedido.descricao,
            status = pedido.status,
            dataSolicitacao = pedido.dataSolicitacao,
            dataConclusao = pedido.dataConclusao,
            dataConfirmacao = null,
            valor = pedido.valor.toString(),
            tempoEstimado = null,
            dataInicio = null,
            contratante = null,
            prestador = pedido.prestador,
            categoria = pedido.categoria,
            localizacao = pedido.localizacao
        )
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

