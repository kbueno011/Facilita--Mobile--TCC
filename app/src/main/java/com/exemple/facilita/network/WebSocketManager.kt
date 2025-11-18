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
        private const val SERVER_URL = "https://servidor-facilita.onrender.com"

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
            socket?.emit("join_servico", servicoId)
            Log.d(TAG, "join_servico emitido: $servicoId")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao entrar no serviço", e)
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
            val data = args[0] as JSONObject
            val servicoId = data.optInt("servicoId", 0)
            val latitude = data.optDouble("latitude", 0.0)
            val longitude = data.optDouble("longitude", 0.0)
            val prestadorName = data.optString("prestadorName", "")
            val timestamp = data.optString("timestamp", "")

            val update = LocationUpdate(
                servicoId = servicoId,
                latitude = latitude,
                longitude = longitude,
                prestadorName = prestadorName,
                timestamp = timestamp
            )

            _locationUpdate.value = update
            Log.d(TAG, "Localização atualizada: lat=$latitude, lng=$longitude")

        } catch (e: Exception) {
            Log.e(TAG, "Erro ao processar location_updated", e)
        }
    }

    fun disconnect() {
        try {
            socket?.off(Socket.EVENT_CONNECT)
            socket?.off(Socket.EVENT_DISCONNECT)
            socket?.off(Socket.EVENT_CONNECT_ERROR)
            socket?.off("location_updated")
            socket?.off("connect_response")
            socket?.disconnect()
            socket = null
            _isConnected.value = false
            Log.d(TAG, "Socket desconectado manualmente")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao desconectar", e)
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

