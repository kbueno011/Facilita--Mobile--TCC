package com.exemple.facilita.network

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import java.net.URISyntaxException

class WebSocketManager {
    private var socket: Socket? = null

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _locationUpdate = MutableStateFlow<LocationUpdate?>(null)
    val locationUpdate: StateFlow<LocationUpdate?> = _locationUpdate

    // Chat - Mensagens
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    companion object {
        private const val TAG = "WebSocketManager"
        // Socket.IO gerencia protocolo automaticamente
        private const val SERVER_URL = "https://facilita-c6hhb9csgygudrdz.canadacentral-01.azurewebsites.net"

        @Volatile
        private var instance: WebSocketManager? = null

        fun getInstance(): WebSocketManager {
            return instance ?: synchronized(this) {
                instance ?: WebSocketManager().also { instance = it }
            }
        }
    }

    // 🔥 NOVA PROPRIEDADE: Armazena dados de conexão para reenviar após reconexão
    private var connectionData: Triple<Int, String, String>? = null
    private var pendingJoinServico: String? = null

    fun connect(userId: Int, userType: String, userName: String) {
        try {
            // Armazena dados de conexão
            connectionData = Triple(userId, userType, userName)

            val options = IO.Options().apply {
                reconnection = true
                reconnectionAttempts = Integer.MAX_VALUE
                reconnectionDelay = 1000
                reconnectionDelayMax = 5000
                timeout = 20000
                transports = arrayOf("websocket", "polling")
            }

            socket = IO.socket(SERVER_URL, options)

            Log.d(TAG, "")
            Log.d(TAG, "╔════════════════════════════════════════════════╗")
            Log.d(TAG, "║  📡 REGISTRANDO LISTENERS DO WEBSOCKET        ║")
            Log.d(TAG, "╚════════════════════════════════════════════════╝")

            socket?.on(Socket.EVENT_CONNECT, onConnect)
            Log.d(TAG, "✅ Listener: EVENT_CONNECT")

            socket?.on(Socket.EVENT_DISCONNECT, onDisconnect)
            Log.d(TAG, "✅ Listener: EVENT_DISCONNECT")

            socket?.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            Log.d(TAG, "✅ Listener: EVENT_CONNECT_ERROR")

            socket?.on("location_updated", onLocationUpdated)
            Log.d(TAG, "✅ Listener: location_updated (LOCALIZAÇÃO DO PRESTADOR)")

            socket?.on("connect_response", onConnectResponse)
            Log.d(TAG, "✅ Listener: connect_response")

            socket?.on("servico_joined", onServicoJoined)
            Log.d(TAG, "✅ Listener: servico_joined (CONFIRMAÇÃO DE ENTRADA NA SALA)")

            socket?.on("receive_message", onReceiveMessage)
            Log.d(TAG, "✅ Listener: receive_message (MENSAGENS DE CHAT)")

            Log.d(TAG, "")
            Log.d(TAG, "📊 TOTAL DE LISTENERS: 7 específicos + diagnóstico")
            Log.d(TAG, "")
            Log.d(TAG, "🔍 MODO DIAGNÓSTICO ATIVADO:")
            Log.d(TAG, "   Todos os eventos recebidos serão logados em tempo real")
            Log.d(TAG, "")

            // 🔥 DIAGNÓSTICO: Intercepta TODAS as emissões/recepções
            setupEventLogger()

            socket?.connect()


        } catch (e: URISyntaxException) {
            Log.e(TAG, "Erro ao conectar WebSocket", e)
            e.printStackTrace()
        }
    }

    /**
     * 🔍 DIAGNÓSTICO: Registra todos os eventos recebidos do servidor
     * Útil para descobrir se o servidor está enviando eventos com nomes diferentes
     */
    private fun setupEventLogger() {
        // Lista de eventos conhecidos do Socket.IO
        val knownEvents = listOf(
            Socket.EVENT_CONNECT,
            Socket.EVENT_DISCONNECT,
            Socket.EVENT_CONNECT_ERROR,
            "location_updated",
            "connect_response",
            "servico_joined",
            "receive_message"
        )

        // Registra listener para cada evento conhecido
        knownEvents.forEach { eventName ->
            socket?.on(eventName) { args ->
                Log.d(TAG, "")
                Log.d(TAG, "╔════════════════════════════════════════════════╗")
                Log.d(TAG, "║  🔔 EVENTO RECEBIDO: $eventName")
                Log.d(TAG, "╚════════════════════════════════════════════════╝")
                Log.d(TAG, "📊 Total de args: ${args.size}")
                args.forEachIndexed { index, arg ->
                    when (arg) {
                        is JSONObject -> {
                            Log.d(TAG, "📦 Arg[$index] (JSONObject):")
                            Log.d(TAG, arg.toString(2))
                        }
                        else -> {
                            Log.d(TAG, "📦 Arg[$index]: $arg (${arg?.javaClass?.simpleName})")
                        }
                    }
                }
                Log.d(TAG, "")
            }
        }

        // Tenta capturar eventos desconhecidos (se o Socket.IO suportar)
        try {
            // Registra listener para eventos comuns que podem ter nomes diferentes
            val possibleEventNames = listOf(
                "location_update",
                "locationUpdate",
                "prestador_location",
                "prestadorLocation",
                "position_update",
                "positionUpdate",
                "message",
                "chat_message",
                "chatMessage",
                "new_message",
                "newMessage"
            )

            possibleEventNames.forEach { eventName ->
                socket?.on(eventName) { args ->
                    Log.d(TAG, "")
                    Log.d(TAG, "🚨🚨🚨 EVENTO ALTERNATIVO DETECTADO: $eventName 🚨🚨🚨")
                    Log.d(TAG, "📊 Args: ${args.size}")
                    args.forEachIndexed { index, arg ->
                        Log.d(TAG, "📦 Arg[$index]: $arg")
                    }
                    Log.d(TAG, "")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Não foi possível registrar listeners alternativos", e)
        }
    }

    private fun emitUserConnected(userId: Int, userType: String, userName: String) {
        try {
            Log.d(TAG, "")
            Log.d(TAG, "╔════════════════════════════════════════════════╗")
            Log.d(TAG, "║  👤 ENVIANDO IDENTIFICAÇÃO DO USUÁRIO         ║")
            Log.d(TAG, "╚════════════════════════════════════════════════╝")

            val data = JSONObject().apply {
                put("userId", userId)
                put("userType", userType)
                put("userName", userName)
            }

            Log.d(TAG, "📤 Dados enviados:")
            Log.d(TAG, data.toString(2))
            Log.d(TAG, "🔌 Socket conectado? ${socket?.connected()}")
            Log.d(TAG, "📡 Emitindo evento: user_connected")

            socket?.emit("user_connected", data)

            Log.d(TAG, "✅ Evento user_connected emitido com sucesso!")
            Log.d(TAG, "⏳ Aguardando resposta do servidor (connect_response)...")
            Log.d(TAG, "")
        } catch (e: Exception) {
            Log.e(TAG, "❌ ERRO ao emitir user_connected", e)
            e.printStackTrace()
        }
    }

    fun joinServico(servicoId: String) {
        try {
            Log.d(TAG, "")
            Log.d(TAG, "╔════════════════════════════════════════════════╗")
            Log.d(TAG, "║  🚪 ENTRANDO NA SALA DO SERVIÇO               ║")
            Log.d(TAG, "╚════════════════════════════════════════════════╝")
            Log.d(TAG, "🆔 ServicoId: $servicoId")
            Log.d(TAG, "🔌 Socket conectado? ${socket?.connected()}")

            if (socket?.connected() != true) {
                Log.w(TAG, "⚠️ Socket ainda não está conectado!")
                Log.w(TAG, "   Armazenando servicoId para entrar na sala após conexão...")
                pendingJoinServico = servicoId
                Log.d(TAG, "✅ Join pendente armazenado. Será processado ao conectar.")
                Log.d(TAG, "")
                return
            }

            Log.d(TAG, "📡 Emitindo evento: join_servico")
            socket?.emit("join_servico", servicoId)
            Log.d(TAG, "✅ Evento join_servico emitido com sucesso!")
            Log.d(TAG, "⏳ Aguardando confirmação do servidor (servico_joined)...")
            Log.d(TAG, "")
        } catch (e: Exception) {
            Log.e(TAG, "❌ ERRO CRÍTICO ao entrar no serviço $servicoId", e)
            e.printStackTrace()
        }
    }

    private val onServicoJoined = Emitter.Listener { args ->
        try {
            Log.d(TAG, "")
            Log.d(TAG, "╔════════════════════════════════════════════════╗")
            Log.d(TAG, "║  🎉 CONFIRMAÇÃO: ENTROU NA SALA!              ║")
            Log.d(TAG, "╚════════════════════════════════════════════════╝")

            if (args.isEmpty()) {
                Log.w(TAG, "⚠️ Resposta sem dados do servidor")
                return@Listener
            }

            val data = args[0] as? JSONObject
            Log.d(TAG, "📦 Dados da resposta:")
            Log.d(TAG, data?.toString(2) ?: "null")

            val servicoId = data?.optString("servicoId", "")
            val message = data?.optString("message", "")

            Log.d(TAG, "")
            Log.d(TAG, "✅ SUCESSO!")
            Log.d(TAG, "   🆔 ServicoId: $servicoId")
            Log.d(TAG, "   💬 Mensagem: $message")
            Log.d(TAG, "")
            Log.d(TAG, "🎯 AGORA VOCÊ IRÁ RECEBER:")
            Log.d(TAG, "   📍 Atualizações de localização do prestador")
            Log.d(TAG, "   💬 Mensagens de chat do prestador")
            Log.d(TAG, "")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao processar resposta de servico_joined", e)
            e.printStackTrace()
        }
    }

    fun updateLocation(servicoId: Int, latitude: Double, longitude: Double, userId: Int) {
        try {
            val data = JSONObject().apply {
                put("servicoId", servicoId)
                put("latitude", latitude)
                put("longitude", longitude)
                put("userId", userId)
            }
            socket?.emit("update_location", data)
            Log.d(TAG, "update_location emitido: lat=$latitude, lng=$longitude")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar localização", e)
        }
    }


    private val onConnect = Emitter.Listener {
        Log.d(TAG, "")
        Log.d(TAG, "╔════════════════════════════════════════════════╗")
        Log.d(TAG, "║  ✅ WEBSOCKET CONECTADO COM SUCESSO!          ║")
        Log.d(TAG, "╚════════════════════════════════════════════════╝")
        Log.d(TAG, "📡 URL: $SERVER_URL")
        Log.d(TAG, "🔌 Estado da conexão: CONECTADO")
        Log.d(TAG, "⏰ Timestamp: ${System.currentTimeMillis()}")
        _isConnected.value = true
        Log.d(TAG, "✅ _isConnected atualizado para: ${_isConnected.value}")
        Log.d(TAG, "")

        // 🔥 ENVIA IDENTIFICAÇÃO DO USUÁRIO IMEDIATAMENTE APÓS CONECTAR
        connectionData?.let { (userId, userType, userName) ->
            Log.d(TAG, "🚀 Enviando identificação do usuário automaticamente...")
            emitUserConnected(userId, userType, userName)
        }

        // 🔥 PROCESSA joinServico PENDENTE (se houver)
        pendingJoinServico?.let { servicoId ->
            Log.d(TAG, "🚀 Processando join_servico pendente: $servicoId")
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                joinServico(servicoId)
                pendingJoinServico = null
            }, 500) // Aguarda 500ms para garantir que user_connected foi processado
        }

        Log.d(TAG, "🎯 AGUARDANDO:")
        Log.d(TAG, "   1️⃣ Entrada na sala do serviço (join_servico)")
        Log.d(TAG, "   2️⃣ Atualizações de localização (location_updated)")
        Log.d(TAG, "   3️⃣ Mensagens de chat (receive_message)")
        Log.d(TAG, "")
    }

    private val onDisconnect = Emitter.Listener {
        Log.w(TAG, "")
        Log.w(TAG, "╔════════════════════════════════════════════════╗")
        Log.w(TAG, "║  ⚠️ WEBSOCKET DESCONECTADO!                   ║")
        Log.w(TAG, "╚════════════════════════════════════════════════╝")
        Log.w(TAG, "🔌 Estado da conexão: DESCONECTADO")
        Log.w(TAG, "⏰ Timestamp: ${System.currentTimeMillis()}")
        _isConnected.value = false
        Log.w(TAG, "❌ _isConnected atualizado para: ${_isConnected.value}")
        Log.w(TAG, "🔄 Tentando reconectar automaticamente...")
        Log.w(TAG, "")
    }

    private val onConnectError = Emitter.Listener { args ->
        Log.e(TAG, "Erro de conexão: ${args.joinToString()}")
        _isConnected.value = false
    }

    private val onConnectResponse = Emitter.Listener { args ->
        try {
            val data = args[0] as? JSONObject
            Log.d(TAG, "Resposta de conexão: $data")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao processar resposta de conexão", e)
        }
    }

    private val onLocationUpdated = Emitter.Listener { args ->
        try {
            Log.d(TAG, "═══════════════════════════════════════════════")
            Log.d(TAG, "🎯 LOCALIZAÇÃO RECEBIDA DO PRESTADOR!")
            Log.d(TAG, "═══════════════════════════════════════════════")
            Log.d(TAG, "📊 Total de args: ${args.size}")

            if (args.isEmpty()) {
                Log.e(TAG, "❌ ERRO: Args vazio! Nenhum dado de localização recebido")
                return@Listener
            }

            val data = args[0] as JSONObject
            Log.d(TAG, "📦 Dados RAW completos:")
            Log.d(TAG, data.toString(2)) // Pretty print JSON

            val servicoId = data.optInt("servicoId", 0)
            val latitude = data.optDouble("latitude", 0.0)
            val longitude = data.optDouble("longitude", 0.0)
            val prestadorName = data.optString("prestadorName", "")
            val userId = data.optInt("userId", 0)
            val timestamp = data.optString("timestamp", "")

            Log.d(TAG, "")
            Log.d(TAG, "═══════════════════════════════════════════════")
            Log.d(TAG, "📍 LOCALIZAÇÃO DO PRESTADOR RECEBIDA:")
            Log.d(TAG, "═══════════════════════════════════════════════")
            Log.d(TAG, "   🆔 ServicoId: $servicoId")
            Log.d(TAG, "   👤 Prestador: $prestadorName")
            Log.d(TAG, "   👤 UserId: $userId")
            Log.d(TAG, "   🌍 Latitude: $latitude")
            Log.d(TAG, "   🌍 Longitude: $longitude")
            Log.d(TAG, "   ⏰ Timestamp: $timestamp")
            Log.d(TAG, "")

            // Validação de coordenadas
            if (latitude == 0.0 && longitude == 0.0) {
                Log.w(TAG, "⚠️ AVISO: Coordenadas zeradas!")
                Log.w(TAG, "   Possíveis causas:")
                Log.w(TAG, "   • Prestador não ativou GPS")
                Log.w(TAG, "   • Permissões de localização negadas")
                Log.w(TAG, "   • Prestador ainda não iniciou rastreamento")
            } else {
                Log.d(TAG, "✅ ✅ ✅ COORDENADAS VÁLIDAS RECEBIDAS! ✅ ✅ ✅")
                Log.d(TAG, "")
                Log.d(TAG, "🎯 O PRESTADOR ESTÁ CONECTADO E ENVIANDO LOCALIZAÇÃO!")
                Log.d(TAG, "")
                Log.d(TAG, "📍 Posição atual:")
                Log.d(TAG, "   Lat: $latitude")
                Log.d(TAG, "   Lng: $longitude")
            }

            val update = LocationUpdate(
                servicoId = servicoId,
                latitude = latitude,
                longitude = longitude,
                prestadorName = prestadorName,
                timestamp = timestamp
            )

            _locationUpdate.value = update
            Log.d(TAG, "")
            Log.d(TAG, "✅ LocationUpdate atualizado no StateFlow!")
            Log.d(TAG, "📊 Valor atualizado: Lat=$latitude, Lng=$longitude")
            Log.d(TAG, "🔔 Telas observando este StateFlow serão notificadas!")
            Log.d(TAG, "═══════════════════════════════════════════════")
            Log.d(TAG, "")

        } catch (e: Exception) {
            Log.e(TAG, "❌ ERRO CRÍTICO ao processar location_updated", e)
            e.printStackTrace()
        }
    }

    /**
     * Envia mensagem de chat
     */
    fun sendChatMessage(
        servicoId: Int,
        mensagem: String,
        sender: String,
        targetUserId: Int
    ) {
        try {
            Log.d(TAG, "💬 Enviando mensagem de chat:")
            Log.d(TAG, "   ServicoId: $servicoId")
            Log.d(TAG, "   Mensagem: $mensagem")
            Log.d(TAG, "   Sender: $sender")
            Log.d(TAG, "   TargetUserId: $targetUserId")

            if (socket?.connected() != true) {
                Log.e(TAG, "❌ Socket não está conectado! Não pode enviar mensagem")
                return
            }

            val data = JSONObject().apply {
                put("servicoId", servicoId)
                put("mensagem", mensagem)
                put("sender", sender)
                put("targetUserId", targetUserId)
            }

            socket?.emit("send_message", data, object : io.socket.client.Ack {
                override fun call(vararg args: Any?) {
                    Log.d(TAG, "📨 ACK recebido do servidor! Args: ${args.size}")
                    args.forEachIndexed { index, arg ->
                        Log.d(TAG, "   ACK arg[$index]: $arg")
                    }
                }
            })
            Log.d(TAG, "✅ Mensagem de chat enviada via WebSocket")

            // Adiciona mensagem própria na lista local
            val currentMessages = _chatMessages.value.toMutableList()
            currentMessages.add(
                ChatMessage(
                    servicoId = servicoId,
                    mensagem = mensagem,
                    sender = sender,
                    userName = "Você",
                    timestamp = System.currentTimeMillis(),
                    isOwn = true
                )
            )
            _chatMessages.value = currentMessages

        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao enviar mensagem de chat", e)
            e.printStackTrace()
        }
    }

    /**
     * Listener para mensagens de chat recebidas
     */
    private val onReceiveMessage = Emitter.Listener { args ->
        try {
            Log.d(TAG, "🎉🎉🎉 EVENTO RECEIVE_MESSAGE CHAMADO! 🎉🎉🎉")
            Log.d(TAG, "💬 Mensagem de chat recebida!")
            Log.d(TAG, "   Total de args: ${args.size}")

            if (args.isEmpty()) {
                Log.e(TAG, "❌ Args vazio! Nenhum dado recebido")
                return@Listener
            }

            val data = args[0] as JSONObject
            Log.d(TAG, "📦 Dados RAW: $data")
            Log.d(TAG, "📦 Dados toString: ${data.toString()}")

            val servicoId = data.optInt("servicoId", 0)
            val mensagem = data.optString("mensagem", "")
            val sender = data.optString("sender", "")
            val userName = data.optString("userName", "Desconhecido")
            val timestamp = data.optLong("timestamp", System.currentTimeMillis())

            Log.d(TAG, "   ✅ ServicoId: $servicoId")
            Log.d(TAG, "   ✅ Mensagem: $mensagem")
            Log.d(TAG, "   ✅ Sender: $sender")
            Log.d(TAG, "   ✅ UserName: $userName")
            Log.d(TAG, "   ✅ Timestamp: $timestamp")

            val chatMessage = ChatMessage(
                servicoId = servicoId,
                mensagem = mensagem,
                sender = sender,
                userName = userName,
                timestamp = timestamp,
                isOwn = false
            )

            val currentMessages = _chatMessages.value.toMutableList()
            currentMessages.add(chatMessage)
            _chatMessages.value = currentMessages

            Log.d(TAG, "✅ Mensagem adicionada. Total: ${currentMessages.size}")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao processar mensagem recebida", e)
            e.printStackTrace()
        }
    }

    /**
     * Limpa mensagens de chat
     */
    fun clearChatMessages() {
        _chatMessages.value = emptyList()
        Log.d(TAG, "🗑️ Mensagens de chat limpas")
    }

    fun disconnect() {
        try {
            Log.d(TAG, "🔌 Desconectando WebSocket...")
            socket?.off(Socket.EVENT_CONNECT)
            socket?.off(Socket.EVENT_DISCONNECT)
            socket?.off(Socket.EVENT_CONNECT_ERROR)
            socket?.off("location_updated")
            socket?.off("connect_response")
            socket?.off("servico_joined")
            socket?.off("receive_message")
            socket?.disconnect()
            socket = null
            _isConnected.value = false
            _locationUpdate.value = null
            _chatMessages.value = emptyList()
            Log.d(TAG, "✅ Socket desconectado com sucesso")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao desconectar", e)
            e.printStackTrace()
        }
    }

    fun isSocketConnected(): Boolean {
        return socket?.connected() ?: false
    }
}

data class LocationUpdate(
    val servicoId: Int,
    val latitude: Double,
    val longitude: Double,
    val prestadorName: String,
    val timestamp: String
)

data class ChatMessage(
    val servicoId: Int,
    val mensagem: String,
    val sender: String,
    val userName: String,
    val timestamp: Long,
    val isOwn: Boolean = false
)

