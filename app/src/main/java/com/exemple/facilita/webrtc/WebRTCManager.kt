package com.exemple.facilita.webrtc

import android.content.Context
import android.util.Log
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

/**
 * 📞 WEBRTC MANAGER (Versão Simplificada)
 * Gerencia conexões WebRTC para chamadas de voz e vídeo
 *
 * NOTA: Esta é uma implementação simplificada que gerencia o estado
 * e a sinalização. A implementação completa do WebRTC será adicionada
 * quando o backend estiver pronto para testar.
 */
class WebRTCManager(
    private val context: Context,
    private val socket: Socket
) {
    // Estado da chamada
    private val _callState = MutableStateFlow<CallState>(CallState.Idle)
    val callState: StateFlow<CallState> = _callState

    private val _localVideoEnabled = MutableStateFlow(true)
    val localVideoEnabled: StateFlow<Boolean> = _localVideoEnabled

    private val _localAudioEnabled = MutableStateFlow(true)
    val localAudioEnabled: StateFlow<Boolean> = _localAudioEnabled

    private val _remoteVideoEnabled = MutableStateFlow(true)
    val remoteVideoEnabled: StateFlow<Boolean> = _remoteVideoEnabled

    private val _remoteAudioEnabled = MutableStateFlow(true)
    val remoteAudioEnabled: StateFlow<Boolean> = _remoteAudioEnabled

    // Dados da chamada atual
    private var currentCallId: String? = null
    private var currentServiceId: String? = null
    private var targetUserId: String? = null

    companion object {
        private const val TAG = "WebRTCManager"
    }

    init {
        setupSocketListeners()
        Log.d(TAG, "✅ WebRTCManager inicializado (modo simplificado)")
    }

    /**
     * Configura listeners do Socket.IO para chamadas
     */
    private fun setupSocketListeners() {
        Log.d(TAG, "")
        Log.d(TAG, "╔════════════════════════════════════════════════╗")
        Log.d(TAG, "║  🔌 CONFIGURANDO LISTENERS DE CHAMADA         ║")
        Log.d(TAG, "╚════════════════════════════════════════════════╝")

        // Chamada iniciada com sucesso
        socket.on("call:initiated") { data ->
            try {
                val callData = data[0] as JSONObject
                Log.d(TAG, "")
                Log.d(TAG, "✅ Chamada iniciada confirmada pelo servidor")
                Log.d(TAG, "   CallId: ${callData.optString("callId", "N/A")}")
                currentCallId = callData.optString("callId", "")
                _callState.value = CallState.OutgoingCall(callData)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao processar call:initiated", e)
                e.printStackTrace()
            }
        }

        // Chamada recebida - LISTENER PRINCIPAL
        socket.on("call:incoming") { data ->
            try {
                if (data.isEmpty()) {
                    Log.e(TAG, "❌ call:incoming sem dados!")
                    return@on
                }

                val callData = data[0] as JSONObject

                Log.d(TAG, "")
                Log.d(TAG, "╔════════════════════════════════════════════════╗")
                Log.d(TAG, "║  📞 CHAMADA RECEBIDA!                         ║")
                Log.d(TAG, "╚════════════════════════════════════════════════╝")
                Log.d(TAG, "   Raw Data: $callData")

                val callerName = callData.optString("callerName", "Desconhecido")
                val callType = callData.optString("callType", "audio")
                val servicoId = callData.optString("servicoId", "0")
                val callerId = callData.optString("callerId", "0")
                val callId = callData.optString("callId", "")

                Log.d(TAG, "   👤 De: $callerName")
                Log.d(TAG, "   📱 Tipo: $callType")
                Log.d(TAG, "   🆔 CallId: $callId")
                Log.d(TAG, "   🏠 ServiceId: $servicoId")
                Log.d(TAG, "   👥 CallerId: $callerId")

                currentCallId = callId
                currentServiceId = servicoId
                targetUserId = callerId

                _callState.value = CallState.IncomingCall(callData)

                Log.d(TAG, "✅ Estado atualizado para IncomingCall")
                Log.d(TAG, "")

            } catch (e: Exception) {
                Log.e(TAG, "❌ ERRO CRÍTICO ao processar call:incoming", e)
                e.printStackTrace()
            }
        }

        // Chamada aceita
        socket.on("call:accepted") { data ->
            try {
                val callData = data[0] as JSONObject
                Log.d(TAG, "✅ Chamada aceita por: ${callData.getString("answererName")}")
                _callState.value = CallState.ActiveCall
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao processar call:accepted", e)
            }
        }

        // ICE Candidate recebido
        socket.on("call:ice-candidate") { data ->
            try {
                Log.d(TAG, "✅ ICE Candidate recebido")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao processar ICE candidate", e)
            }
        }

        // Chamada finalizada
        socket.on("call:ended") { data ->
            try {
                val endData = data[0] as JSONObject
                Log.d(TAG, "📴 Chamada finalizada: ${endData.getString("reason")}")
                _callState.value = CallState.Ended(endData.getString("reason"))
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao processar call:ended", e)
            }
        }

        // Chamada rejeitada
        socket.on("call:rejected") { data ->
            try {
                val rejectData = data[0] as JSONObject
                Log.d(TAG, "❌ Chamada rejeitada por: ${rejectData.getString("rejectedByName")}")
                _callState.value = CallState.Rejected(rejectData.getString("reason"))
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao processar call:rejected", e)
            }
        }

        // Chamada cancelada
        socket.on("call:cancelled") { data ->
            try {
                Log.d(TAG, "🚫 Chamada cancelada")
                _callState.value = CallState.Cancelled
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao processar call:cancelled", e)
            }
        }

        // Chamada falhou
        socket.on("call:failed") { data ->
            try {
                val failData = data[0] as JSONObject
                Log.e(TAG, "💥 Chamada falhou: ${failData.getString("message")}")
                _callState.value = CallState.Failed(failData.getString("message"))
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao processar call:failed", e)
            }
        }

        // Mídia alternada
        socket.on("call:media-toggled") { data ->
            try {
                val toggleData = data[0] as JSONObject
                val mediaType = toggleData.getString("mediaType")
                val enabled = toggleData.getBoolean("enabled")

                when (mediaType) {
                    "video" -> _remoteVideoEnabled.value = enabled
                    "audio" -> _remoteAudioEnabled.value = enabled
                }

                Log.d(TAG, "🎛️ Mídia remota alternada: $mediaType = $enabled")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao processar media-toggled", e)
            }
        }

        Log.d(TAG, "✅ Listeners de chamada configurados")
    }


    /**
     * Inicia uma chamada
     */
    fun startCall(
        servicoId: String,
        targetUserId: String,
        callerId: String,
        callerName: String,
        callType: String // "video" ou "audio"
    ) {
        Log.d(TAG, "")
        Log.d(TAG, "╔════════════════════════════════════════════════╗")
        Log.d(TAG, "║  📞 INICIANDO CHAMADA                         ║")
        Log.d(TAG, "╚════════════════════════════════════════════════╝")
        Log.d(TAG, "   ServicoId: $servicoId")
        Log.d(TAG, "   TargetUserId: $targetUserId")
        Log.d(TAG, "   CallType: $callType")

        this.currentServiceId = servicoId
        this.targetUserId = targetUserId

        // Verificar se socket está conectado
        if (!socket.connected()) {
            Log.e(TAG, "❌❌❌ SOCKET NÃO ESTÁ CONECTADO!")
            Log.e(TAG, "   Não é possível iniciar chamada")
            _callState.value = CallState.Failed("WebSocket não conectado")
            return
        }

        Log.d(TAG, "✅ Socket conectado: ${socket.id()}")

        // Criar oferta SDP simples (placeholder para sinalização)
        val offerSdp = JSONObject().apply {
            put("type", "offer")
            put("sdp", "v=0\r\no=- ${System.currentTimeMillis()} 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n")
        }

        // Criar payload completo
        val payload = JSONObject().apply {
            put("servicoId", servicoId)
            put("callerId", callerId)
            put("callerName", callerName)
            put("targetUserId", targetUserId)
            put("callType", callType)
            put("offer", offerSdp)
        }

        Log.d(TAG, "")
        Log.d(TAG, "📤 EMITINDO EVENTO 'call:initiate'")
        Log.d(TAG, "📦 Payload:")
        Log.d(TAG, payload.toString(2))
        Log.d(TAG, "")

        // Emitir evento de início de chamada COM oferta SDP
        socket.emit("call:initiate", payload)

        _callState.value = CallState.Calling

        Log.d(TAG, "✅ Evento 'call:initiate' ENVIADO!")
        Log.d(TAG, "⏳ Aguardando resposta do destinatário...")
        Log.d(TAG, "⏳ O servidor deve enviar 'call:incoming' para o targetUserId: $targetUserId")
        Log.d(TAG, "")
    }

    /**
     * Aceita uma chamada recebida
     */
    fun acceptCall(callData: JSONObject) {
        Log.d(TAG, "")
        Log.d(TAG, "╔════════════════════════════════════════════════╗")
        Log.d(TAG, "║  ✅ ACEITANDO CHAMADA                         ║")
        Log.d(TAG, "╚════════════════════════════════════════════════╝")

        currentCallId = callData.getString("callId")
        currentServiceId = callData.getString("servicoId")
        targetUserId = callData.getString("callerId")

        val callType = callData.getString("callType")

        Log.d(TAG, "   CallId: $currentCallId")
        Log.d(TAG, "   ServiceId: $currentServiceId")
        Log.d(TAG, "   CallerId: $targetUserId")
        Log.d(TAG, "   CallType: $callType")

        // Criar resposta SDP (placeholder)
        val answerSdp = JSONObject().apply {
            put("type", "answer")
            put("sdp", "v=0\r\no=- ${System.currentTimeMillis()} 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n")
        }

        // Enviar aceitação para servidor COM resposta SDP
        socket.emit("call:accept", JSONObject().apply {
            put("servicoId", currentServiceId)
            put("callId", currentCallId)
            put("callerId", targetUserId)
            put("answer", answerSdp)
        })

        Log.d(TAG, "✅ Aceitação enviada ao servidor")
        Log.d(TAG, "🔗 Estabelecendo conexão...")

        _callState.value = CallState.ActiveCall
    }

    /**
     * Rejeita uma chamada
     */
    fun rejectCall(reason: String = "user_declined") {
        Log.d(TAG, "❌ Rejeitando chamada...")

        socket.emit("call:reject", JSONObject().apply {
            put("servicoId", currentServiceId)
            put("callId", currentCallId)
            put("reason", reason)
        })

        _callState.value = CallState.Idle
    }

    /**
     * Finaliza a chamada atual
     */
    fun endCall(reason: String = "user_ended") {
        Log.d(TAG, "📴 Finalizando chamada...")

        socket.emit("call:end", JSONObject().apply {
            put("servicoId", currentServiceId)
            put("callId", currentCallId)
            put("targetUserId", targetUserId)
            put("reason", reason)
        })

        _callState.value = CallState.Idle
    }

    /**
     * Alterna o estado do vídeo local
     */
    fun toggleVideo() {
        val newState = !_localVideoEnabled.value
        _localVideoEnabled.value = newState

        // Notifica o outro usuário
        socket.emit("call:toggle-media", JSONObject().apply {
            put("servicoId", currentServiceId)
            put("targetUserId", targetUserId)
            put("mediaType", "video")
            put("enabled", newState)
            put("callId", currentCallId)
        })

        Log.d(TAG, "📹 Vídeo local: ${if (newState) "Ligado" else "Desligado"}")
    }

    /**
     * Alterna o estado do áudio local
     */
    fun toggleAudio() {
        val newState = !_localAudioEnabled.value
        _localAudioEnabled.value = newState

        // Notifica o outro usuário
        socket.emit("call:toggle-media", JSONObject().apply {
            put("servicoId", currentServiceId)
            put("targetUserId", targetUserId)
            put("mediaType", "audio")
            put("enabled", newState)
            put("callId", currentCallId)
        })

        Log.d(TAG, "🎤 Áudio local: ${if (newState) "Ligado" else "Desligado"}")
    }

    /**
     * Troca câmera (frontal/traseira)
     */
    fun switchCamera() {
        Log.d(TAG, "🔄 Câmera trocada (não implementado ainda)")
    }

    /**
     * Libera todos os recursos
     */
    fun release() {
        currentCallId = null
        currentServiceId = null
        targetUserId = null

        Log.d(TAG, "✅ WebRTCManager liberado")
    }
}

/**
 * Estados possíveis de uma chamada
 */
sealed class CallState {
    object Idle : CallState()
    object Calling : CallState()
    data class IncomingCall(val callData: JSONObject) : CallState()
    data class OutgoingCall(val callData: JSONObject) : CallState()
    object ActiveCall : CallState()
    data class Ended(val reason: String) : CallState()
    data class Rejected(val reason: String) : CallState()
    object Cancelled : CallState()
    data class Failed(val message: String) : CallState()
}

