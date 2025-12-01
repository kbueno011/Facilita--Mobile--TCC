package com.exemple.facilita.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exemple.facilita.network.WebSocketManager
import com.exemple.facilita.webrtc.CallState
import com.exemple.facilita.webrtc.WebRTCManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * 📞 CALL VIEW MODEL
 * Gerencia o estado e a lógica de chamadas de voz/vídeo
 */
class CallViewModel(application: Application) : AndroidViewModel(application) {

    private val webSocketManager = WebSocketManager.getInstance()
    private var webRTCManager: WebRTCManager? = null

    // Estado da chamada
    private val _callState = MutableStateFlow<CallState>(CallState.Idle)
    val callState: StateFlow<CallState> = _callState

    // Estado de mídia
    private val _localVideoEnabled = MutableStateFlow(true)
    val localVideoEnabled: StateFlow<Boolean> = _localVideoEnabled

    private val _localAudioEnabled = MutableStateFlow(true)
    val localAudioEnabled: StateFlow<Boolean> = _localAudioEnabled

    private val _remoteVideoEnabled = MutableStateFlow(true)
    val remoteVideoEnabled: StateFlow<Boolean> = _remoteVideoEnabled

    private val _remoteAudioEnabled = MutableStateFlow(true)
    val remoteAudioEnabled: StateFlow<Boolean> = _remoteAudioEnabled

    // Duração da chamada
    private val _callDuration = MutableStateFlow(0L)
    val callDuration: StateFlow<Long> = _callDuration

    companion object {
        private const val TAG = "CallViewModel"
    }

    init {
        Log.d(TAG, "📞 CallViewModel inicializado")
        setupGlobalCallListeners()
    }

    /**
     * Configura listeners globais para chamadas recebidas
     */
    private fun setupGlobalCallListeners() {
        val socket = webSocketManager.getSocket()

        socket?.on("call:incoming") { data ->
            try {
                val callData = data[0] as JSONObject
                Log.d(TAG, "")
                Log.d(TAG, "╔════════════════════════════════════════════════╗")
                Log.d(TAG, "║  📞 CHAMADA RECEBIDA!                         ║")
                Log.d(TAG, "╚════════════════════════════════════════════════╝")
                Log.d(TAG, "   De: ${callData.optString("callerName", "Desconhecido")}")
                Log.d(TAG, "   Tipo: ${callData.optString("callType", "audio")}")
                Log.d(TAG, "   ServicoId: ${callData.optString("servicoId", "0")}")

                // Atualiza estado para chamada recebida
                _callState.value = CallState.IncomingCall(callData)

            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao processar chamada recebida", e)
            }
        }

        Log.d(TAG, "✅ Listeners globais de chamada configurados")
    }

    /**
     * Inicializa o WebRTC Manager
     */
    fun initializeWebRTC() {
        if (webRTCManager == null) {
            Log.d(TAG, "🔧 Inicializando WebRTCManager...")

            val socket = webSocketManager.getSocket()

            if (socket != null) {
                webRTCManager = WebRTCManager(
                    context = getApplication(),
                    socket = socket
                )

                // Observa estado da chamada do WebRTCManager
                viewModelScope.launch {
                    webRTCManager?.callState?.collect { state ->
                        _callState.value = state

                        when (state) {
                            is CallState.ActiveCall -> {
                                startCallDurationTimer()
                            }
                            is CallState.Ended, is CallState.Cancelled, is CallState.Rejected, is CallState.Failed -> {
                                resetCallState()
                            }
                            else -> {}
                        }
                    }
                }

                // Observa estado de mídia local
                viewModelScope.launch {
                    webRTCManager?.localVideoEnabled?.collect {
                        _localVideoEnabled.value = it
                    }
                }

                viewModelScope.launch {
                    webRTCManager?.localAudioEnabled?.collect {
                        _localAudioEnabled.value = it
                    }
                }

                // Observa estado de mídia remota
                viewModelScope.launch {
                    webRTCManager?.remoteVideoEnabled?.collect {
                        _remoteVideoEnabled.value = it
                    }
                }

                viewModelScope.launch {
                    webRTCManager?.remoteAudioEnabled?.collect {
                        _remoteAudioEnabled.value = it
                    }
                }

                Log.d(TAG, "✅ WebRTCManager inicializado")
            } else {
                Log.e(TAG, "❌ Socket não disponível!")
            }
        }
    }

    /**
     * Inicia uma chamada de vídeo
     */
    fun startVideoCall(
        servicoId: String,
        targetUserId: String,
        callerId: String,
        callerName: String
    ) {
        Log.d(TAG, "📹 Iniciando chamada de vídeo...")
        webRTCManager?.startCall(
            servicoId = servicoId,
            targetUserId = targetUserId,
            callerId = callerId,
            callerName = callerName,
            callType = "video"
        )
    }

    /**
     * Inicia uma chamada de áudio
     */
    fun startAudioCall(
        servicoId: String,
        targetUserId: String,
        callerId: String,
        callerName: String
    ) {
        Log.d(TAG, "🎤 Iniciando chamada de áudio...")
        webRTCManager?.startCall(
            servicoId = servicoId,
            targetUserId = targetUserId,
            callerId = callerId,
            callerName = callerName,
            callType = "audio"
        )
    }

    /**
     * Aceita uma chamada recebida
     */
    fun acceptCall(callData: JSONObject) {
        Log.d(TAG, "✅ Aceitando chamada...")
        webRTCManager?.acceptCall(callData)
    }

    /**
     * Rejeita uma chamada recebida
     */
    fun rejectCall(reason: String = "user_declined") {
        Log.d(TAG, "❌ Rejeitando chamada...")
        webRTCManager?.rejectCall(reason)
        resetCallState()
    }

    /**
     * Finaliza a chamada atual
     */
    fun endCall() {
        Log.d(TAG, "📴 Finalizando chamada...")
        webRTCManager?.endCall("user_ended")
        resetCallState()
    }

    /**
     * Alterna o estado do vídeo local
     */
    fun toggleVideo() {
        webRTCManager?.toggleVideo()
    }

    /**
     * Alterna o estado do áudio local
     */
    fun toggleAudio() {
        webRTCManager?.toggleAudio()
    }

    /**
     * Troca a câmera (frontal/traseira)
     */
    fun switchCamera() {
        webRTCManager?.switchCamera()
    }

    /**
     * Inicia o contador de duração da chamada
     */
    private fun startCallDurationTimer() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (_callState.value is CallState.ActiveCall) {
                _callDuration.value = (System.currentTimeMillis() - startTime) / 1000
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    /**
     * Reseta o estado da chamada
     */
    private fun resetCallState() {
        _callDuration.value = 0
        _localVideoEnabled.value = true
        _localAudioEnabled.value = true
        _remoteVideoEnabled.value = true
        _remoteAudioEnabled.value = true
    }

    /**
     * Limpa recursos ao destruir o ViewModel
     */
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "🧹 Limpando CallViewModel...")
        webRTCManager?.release()
        webRTCManager = null
    }
}

