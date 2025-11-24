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

    fun connect(userId: Int, userType: String, userName: String) {
        try {
            val options = IO.Options().apply {
                reconnection = true
                reconnectionAttempts = Integer.MAX_VALUE
                reconnectionDelay = 1000
                reconnectionDelayMax = 5000
                timeout = 20000
                transports = arrayOf("websocket", "polling")
            }

            socket = IO.socket(SERVER_URL, options)

            Log.d(TAG, "📡 Registrando listeners...")
            socket?.on(Socket.EVENT_CONNECT, onConnect)
            socket?.on(Socket.EVENT_DISCONNECT, onDisconnect)
            socket?.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            socket?.on("location_updated", onLocationUpdated)
            socket?.on("connect_response", onConnectResponse)
            socket?.on("servico_joined", onServicoJoined)
            socket?.on("receive_message", onReceiveMessage)

            // 🔍 Testar variações do nome do evento (caso o backend use nome diferente)
            socket?.on("message_received", onAnyEvent)  // Variação 1
            socket?.on("new_message", onAnyEvent)       // Variação 2
            socket?.on("chat_message", onAnyEvent)      // Variação 3
            socket?.on("message", onAnyEvent)           // Variação 4

            Log.d(TAG, "✅ Listener 'receive_message' REGISTRADO!")
            Log.d(TAG, "📊 Total de listeners registrados: 11 (+ 4 variações de teste)")

            socket?.connect()

            // Envia dados de conexão após conectar
            socket?.on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "Socket conectado, enviando user_connected")
                emitUserConnected(userId, userType, userName)
            }

        } catch (e: URISyntaxException) {
            Log.e(TAG, "Erro ao conectar WebSocket", e)
            e.printStackTrace()
        }
    }

    private fun emitUserConnected(userId: Int, userType: String, userName: String) {
        try {
            val data = JSONObject().apply {
                put("userId", userId)
                put("userType", userType)
                put("userName", userName)
            }
            socket?.emit("user_connected", data)
            Log.d(TAG, "user_connected emitido: $data")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao emitir user_connected", e)
        }
    }

    fun joinServico(servicoId: String) {
        try {
            Log.d(TAG, "🚪 Entrando na sala do serviço: $servicoId")
            socket?.emit("join_servico", servicoId)
            Log.d(TAG, "✅ Evento join_servico emitido com sucesso")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao entrar no serviço $servicoId", e)
            e.printStackTrace()
        }
    }

    private val onServicoJoined = Emitter.Listener { args ->
        try {
            val data = args[0] as? JSONObject
            Log.d(TAG, "🎉 Resposta de servico_joined: $data")
            val servicoId = data?.optString("servicoId", "")
            val message = data?.optString("message", "")
            Log.d(TAG, "✅ Entrou com sucesso no serviço $servicoId: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao processar resposta de servico_joined", e)
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

    /**
     * 🔍 CATCH-ALL: Captura QUALQUER evento que chegar do servidor
     * Usado para capturar mensagens que vêm sem nome de evento específico
     */
    private val onAnyEvent = Emitter.Listener { args ->
        try {
            if (args.isNotEmpty()) {
                val firstArg = args[0]

                // Verifica se é um JSONObject com dados de mensagem
                if (firstArg is JSONObject) {
                    Log.d(TAG, "🔥🔥🔥 EVENTO GENÉRICO CAPTURADO")
                    Log.d(TAG, "   Total de args: ${args.size}")
                    Log.d(TAG, "   Arg[0]: $firstArg")

                    // Verifica se tem os campos de mensagem
                    if (firstArg.has("mensagem") && firstArg.has("servicoId")) {
                        Log.d(TAG, "✅ É uma mensagem de chat! Processando...")

                        val servicoId = firstArg.optInt("servicoId", 0)
                        val mensagem = firstArg.optString("mensagem", "")
                        val sender = firstArg.optString("sender", "")
                        val timestamp = firstArg.optLong("timestamp", System.currentTimeMillis())

                        // Extrai informações do remetente
                        val senderInfo = firstArg.optJSONObject("senderInfo")
                        val userName = senderInfo?.optString("userName", "Usuário") ?: "Usuário"

                        Log.d(TAG, "   📨 Mensagem: $mensagem")
                        Log.d(TAG, "   👤 De: $userName ($sender)")
                        Log.d(TAG, "   🏠 ServicoId: $servicoId")

                        // Adiciona à lista de mensagens
                        val currentMessages = _chatMessages.value.toMutableList()
                        currentMessages.add(
                            ChatMessage(
                                servicoId = servicoId,
                                mensagem = mensagem,
                                sender = sender,
                                userName = userName,
                                timestamp = timestamp,
                                isOwn = sender == "contratante" // Se você é contratante
                            )
                        )
                        _chatMessages.value = currentMessages
                        Log.d(TAG, "✅ Mensagem adicionada! Total de mensagens: ${currentMessages.size}")
                    }
                } else {
                    val eventName = firstArg as? String ?: "unknown"
                    Log.d(TAG, "🔥 EVENTO GENÉRICO: $eventName")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao processar evento genérico", e)
            e.printStackTrace()
        }
    }

    private val onConnect = Emitter.Listener {
        Log.d(TAG, "✅ Socket conectado!")
        Log.d(TAG, "   Atualizando _isConnected para TRUE")
        _isConnected.value = true
        Log.d(TAG, "   Estado atual: isConnected = ${_isConnected.value}")
    }

    private val onDisconnect = Emitter.Listener {
        Log.w(TAG, "⚠️ Socket desconectado!")
        Log.w(TAG, "   Atualizando _isConnected para FALSE")
        _isConnected.value = false
        Log.w(TAG, "   Estado atual: isConnected = ${_isConnected.value}")
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
            Log.d(TAG, "🎯 Evento location_updated recebido! Args: ${args.size}")

            val data = args[0] as JSONObject
            Log.d(TAG, "📦 Dados recebidos: $data")

            val servicoId = data.optInt("servicoId", 0)
            val latitude = data.optDouble("latitude", 0.0)
            val longitude = data.optDouble("longitude", 0.0)
            val prestadorName = data.optString("prestadorName", "")
            val timestamp = data.optString("timestamp", "")

            Log.d(TAG, "📍 Localização processada:")
            Log.d(TAG, "   ServicoId: $servicoId")
            Log.d(TAG, "   Latitude: $latitude")
            Log.d(TAG, "   Longitude: $longitude")
            Log.d(TAG, "   Prestador: $prestadorName")
            Log.d(TAG, "   Timestamp: $timestamp")

            val update = LocationUpdate(
                servicoId = servicoId,
                latitude = latitude,
                longitude = longitude,
                prestadorName = prestadorName,
                timestamp = timestamp
            )

            _locationUpdate.value = update
            Log.d(TAG, "✅ LocationUpdate atualizado no StateFlow!")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao processar location_updated", e)
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

