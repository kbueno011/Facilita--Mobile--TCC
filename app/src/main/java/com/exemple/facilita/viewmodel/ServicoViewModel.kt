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
            .baseUrl("https://facilita-c6hhb9csgygudrdz.canadacentral-01.azurewebsites.net/v1/facilita/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ServicoApiService::class.java)
    }

    private val _servico = MutableStateFlow<Servico?>(null)
    val servico: StateFlow<Servico?> = _servico.asStateFlow()

    private val _servicoPedido = MutableStateFlow<ServicoPedido?>(null)
    val servicoPedido: StateFlow<ServicoPedido?> = _servicoPedido.asStateFlow()

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

    // Busca serviço específico por ID - NOVA ESTRATÉGIA: busca TODOS os pedidos
    private suspend fun buscarServicoPorId(token: String, servicoId: Int) {
        try {
            Log.d("ServicoViewModel", "🔄 Buscando serviço ID: $servicoId em TODOS os pedidos")

            // Busca TODOS os pedidos do contratante (sem filtro de status)
            val response = apiService.buscarTodosPedidos("Bearer $token")

            if (response.isSuccessful && response.body()?.statusCode == 200) {
                val pedidos = response.body()?.data?.pedidos

                Log.d("ServicoViewModel", "📦 Total de pedidos retornados: ${pedidos?.size ?: 0}")

                // Procura o serviço específico pelo ID
                val servicoEncontrado = pedidos?.find { it.id == servicoId }

                if (servicoEncontrado != null) {
                    Log.d("ServicoViewModel", "✅ Serviço encontrado!")
                    Log.d("ServicoViewModel", "   ID: ${servicoEncontrado.id}")
                    Log.d("ServicoViewModel", "   Status: ${servicoEncontrado.status}")
                    Log.d("ServicoViewModel", "   Descrição: ${servicoEncontrado.descricao}")
                    Log.d("ServicoViewModel", "   Valor: R$ ${servicoEncontrado.valor}")

                    // Armazena o ServicoPedido completo (com paradas)
                    _servicoPedido.value = servicoEncontrado

                    // Converte ServicoPedido para Servico (retrocompatibilidade)
                    _servico.value = converterParaServico(servicoEncontrado)
                    _error.value = null

                    // Log das paradas se existirem
                    servicoEncontrado.paradas?.let { paradas ->
                        Log.d("ServicoViewModel", "🛣️ Serviço com ${paradas.size} paradas:")
                        paradas.sortedBy { it.ordem }.forEach { parada ->
                            Log.d("ServicoViewModel", "  ${parada.ordem}: ${parada.tipo} - ${parada.descricao}")
                            Log.d("ServicoViewModel", "     Coords: ${parada.lat}, ${parada.lng}")
                            Log.d("ServicoViewModel", "     Endereço: ${parada.enderecoCompleto}")
                        }
                    } ?: run {
                        Log.d("ServicoViewModel", "📍 Serviço SEM paradas definidas")
                    }

                    // Log do prestador se existir
                    servicoEncontrado.prestador?.let { prestador ->
                        Log.d("ServicoViewModel", "👤 Prestador: ${prestador.usuario?.nome}")
                        if (prestador.latitudeAtual != null && prestador.longitudeAtual != null) {
                            Log.d("ServicoViewModel", "   📍 Posição atual: ${prestador.latitudeAtual}, ${prestador.longitudeAtual}")
                        } else {
                            Log.w("ServicoViewModel", "   ⚠️ Prestador sem localização atual")
                        }
                    } ?: run {
                        Log.d("ServicoViewModel", "⚠️ Serviço ainda sem prestador atribuído")
                    }

                    // Log da localização de destino
                    servicoEncontrado.localizacao?.let { loc ->
                        Log.d("ServicoViewModel", "🎯 Localização de destino:")
                        Log.d("ServicoViewModel", "   Coords: ${loc.latitude}, ${loc.longitude}")
                        Log.d("ServicoViewModel", "   Endereço: ${loc.endereco}")
                    }

                } else {
                    _error.value = "Serviço não encontrado"
                    Log.e("ServicoViewModel", "❌ Serviço ID $servicoId não encontrado na lista de pedidos")
                    Log.e("ServicoViewModel", "   IDs disponíveis: ${pedidos?.map { it.id }?.joinToString()}")
                }
            } else {
                val errorMsg = "Erro na API: ${response.code()} - ${response.message()}"
                _error.value = errorMsg
                Log.e("ServicoViewModel", "❌ $errorMsg")
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

