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

    companion object {
        private const val TAG = "WebSocketManager"
        private const val SERVER_URL = "wss://facilita-c6hhb9csgygudrdz.canadacentral-01.azurewebsites.net"

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

            socket?.on(Socket.EVENT_CONNECT, onConnect)
            socket?.on(Socket.EVENT_DISCONNECT, onDisconnect)
            socket?.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            socket?.on("location_updated", onLocationUpdated)
            socket?.on("connect_response", onConnectResponse)
            socket?.on("servico_joined", onServicoJoined)

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

    private val onConnect = Emitter.Listener {
        Log.d(TAG, "Socket conectado!")
        _isConnected.value = true
    }

    private val onDisconnect = Emitter.Listener {
        Log.d(TAG, "Socket desconectado")
        _isConnected.value = false
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

    fun disconnect() {
        try {
            Log.d(TAG, "🔌 Desconectando WebSocket...")
            socket?.off(Socket.EVENT_CONNECT)
            socket?.off(Socket.EVENT_DISCONNECT)
            socket?.off(Socket.EVENT_CONNECT_ERROR)
            socket?.off("location_updated")
            socket?.off("connect_response")
            socket?.off("servico_joined")
            socket?.disconnect()
            socket = null
            _isConnected.value = false
            _locationUpdate.value = null
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

