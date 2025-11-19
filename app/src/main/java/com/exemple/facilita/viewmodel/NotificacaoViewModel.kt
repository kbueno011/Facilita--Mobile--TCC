package com.exemple.facilita.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exemple.facilita.data.api.NotificacaoApiService
import com.exemple.facilita.data.models.Notificacao
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificacaoViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: NotificacaoApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://facilita-c6hhb9csgygudrdz.canadacentral-01.azurewebsites.net/v1/facilita/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(NotificacaoApiService::class.java)
    }

    private val _notificacoes = MutableStateFlow<List<Notificacao>>(emptyList())
    val notificacoes: StateFlow<List<Notificacao>> = _notificacoes.asStateFlow()

    private val _notificacoesNaoLidas = MutableStateFlow<List<Notificacao>>(emptyList())
    val notificacoesNaoLidas: StateFlow<List<Notificacao>> = _notificacoesNaoLidas.asStateFlow()

    private val _contadorNaoLidas = MutableStateFlow(0)
    val contadorNaoLidas: StateFlow<Int> = _contadorNaoLidas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var pollingJob: Job? = null

    // Inicia polling para verificar novas notificações
    fun iniciarMonitoramento(token: String, intervalo: Long = 30000) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    buscarNotificacoes(token)
                    delay(intervalo) // Padrão: 30 segundos
                } catch (e: Exception) {
                    Log.e("NotificacaoViewModel", "Erro no polling: ${e.message}", e)
                    delay(intervalo)
                }
            }
        }
    }

    // Para o monitoramento
    fun pararMonitoramento() {
        pollingJob?.cancel()
        pollingJob = null
    }

    // Busca todas as notificações
    fun buscarNotificacoes(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.obterNotificacoes("Bearer $token")

                if (response.isSuccessful && response.body()?.statusCode == 200) {
                    val novas = response.body()?.data ?: emptyList()
                    _notificacoes.value = novas

                    // Atualiza contador de não lidas
                    val naoLidas = novas.filter { !it.lida }
                    _notificacoesNaoLidas.value = naoLidas
                    _contadorNaoLidas.value = naoLidas.size

                    Log.d("NotificacaoViewModel", "✅ ${novas.size} notificações, ${naoLidas.size} não lidas")
                } else {
                    Log.e("NotificacaoViewModel", "❌ Erro ao buscar: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("NotificacaoViewModel", "❌ Exceção ao buscar notificações", e)
                _error.value = "Erro ao buscar notificações: ${e.message}"
            }
        }
    }

    // Busca apenas não lidas
    fun buscarNotificacoesNaoLidas(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.obterNotificacoesNaoLidas("Bearer $token")

                if (response.isSuccessful && response.body()?.statusCode == 200) {
                    val naoLidas = response.body()?.data ?: emptyList()
                    _notificacoesNaoLidas.value = naoLidas
                    _contadorNaoLidas.value = naoLidas.size

                    Log.d("NotificacaoViewModel", "✅ ${naoLidas.size} notificações não lidas")
                }
            } catch (e: Exception) {
                Log.e("NotificacaoViewModel", "❌ Erro ao buscar não lidas", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Marcar como lida
    fun marcarComoLida(token: String, notificacaoId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = apiService.marcarComoLida("Bearer $token", notificacaoId)

                if (response.isSuccessful && response.body()?.statusCode == 200) {
                    // Atualiza lista local
                    _notificacoes.value = _notificacoes.value.map {
                        if (it.id == notificacaoId) it.copy(lida = true) else it
                    }

                    // Atualiza não lidas
                    _notificacoesNaoLidas.value = _notificacoesNaoLidas.value.filter {
                        it.id != notificacaoId
                    }
                    _contadorNaoLidas.value = _notificacoesNaoLidas.value.size

                    Log.d("NotificacaoViewModel", "✅ Notificação $notificacaoId marcada como lida")
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("NotificacaoViewModel", "❌ Erro ao marcar como lida", e)
            }
        }
    }

    // Marcar todas como lidas
    fun marcarTodasComoLidas(token: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.marcarTodasComoLidas("Bearer $token")

                if (response.isSuccessful && response.body()?.statusCode == 200) {
                    // Atualiza todas localmente
                    _notificacoes.value = _notificacoes.value.map {
                        it.copy(lida = true)
                    }
                    _notificacoesNaoLidas.value = emptyList()
                    _contadorNaoLidas.value = 0

                    Log.d("NotificacaoViewModel", "✅ Todas marcadas como lidas")
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("NotificacaoViewModel", "❌ Erro ao marcar todas", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Deletar notificação
    fun deletarNotificacao(token: String, notificacaoId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = apiService.deletarNotificacao("Bearer $token", notificacaoId)

                if (response.isSuccessful && response.body()?.statusCode == 200) {
                    // Remove localmente
                    _notificacoes.value = _notificacoes.value.filter {
                        it.id != notificacaoId
                    }
                    _notificacoesNaoLidas.value = _notificacoesNaoLidas.value.filter {
                        it.id != notificacaoId
                    }
                    _contadorNaoLidas.value = _notificacoesNaoLidas.value.size

                    Log.d("NotificacaoViewModel", "✅ Notificação deletada")
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("NotificacaoViewModel", "❌ Erro ao deletar", e)
            }
        }
    }

    // Limpar erro
    fun limparErro() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        pararMonitoramento()
    }
}

