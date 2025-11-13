package com.exemple.facilita.service

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import java.net.URISyntaxException

/**
 * Gerenciador de WebSocket para rastreamento em tempo real
 * Baseado na documentação da API Facilita
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object WebSocketManager {

    private const val TAG = "WebSocketManager"
    private const val SOCKET_URL = "ws://localhost:3030" // Altere para o servidor real

    private var socket: Socket? = null
    private var isConnected = false

    // Estado da localização em tempo real
    private val _localizacaoAtual = MutableStateFlow<LocalizacaoWebSocket?>(null)
    val localizacaoAtual: StateFlow<LocalizacaoWebSocket?> = _localizacaoAtual

    /**
     * Conecta ao WebSocket
     */
    fun conectar(userId: Int, userType: String, userName: String) {
        try {
            if (socket?.connected() == true) {
                Log.d(TAG, "⚠️ WebSocket já conectado")
                return
            }

            val options = IO.Options().apply {
                reconnection = true
                reconnectionAttempts = 5
                reconnectionDelay = 1000
                timeout = 10000
            }

            socket = IO.socket(SOCKET_URL, options)

            socket?.apply {
                on(Socket.EVENT_CONNECT, onConnect(userId, userType, userName))
                on(Socket.EVENT_DISCONNECT, onDisconnect)
                on(Socket.EVENT_CONNECT_ERROR, onConnectError)
                on("location_updated", onLocationUpdated)

                connect()
                Log.d(TAG, "🔌 Iniciando conexão WebSocket...")
            }

        } catch (e: URISyntaxException) {
            Log.e(TAG, "❌ Erro na URI do WebSocket", e)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao conectar WebSocket", e)
        }
    }

    /**
     * Entra na sala do serviço específico
     */
    fun entrarNaSala(servicoId: String) {
        socket?.emit("join_servico", servicoId)
        Log.d(TAG, "🚪 Entrando na sala do serviço #$servicoId")
    }

    /**
     * Envia atualização de localização
     */
    fun enviarLocalizacao(servicoId: Int, latitude: Double, longitude: Double, userId: Int) {
        try {
            val data = JSONObject().apply {
                put("servicoId", servicoId)
                put("latitude", latitude)
                put("longitude", longitude)
                put("userId", userId)
            }

            socket?.emit("update_location", data)
            Log.d(TAG, "📍 Localização enviada: lat=$latitude, lng=$longitude")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao enviar localização", e)
        }
    }

    /**
     * Desconecta do WebSocket
     */
    fun desconectar() {
        socket?.apply {
            disconnect()
            off()
        }
        socket = null
        isConnected = false
        _localizacaoAtual.value = null
        Log.d(TAG, "🔌 WebSocket desconectado")
    }

    // Listeners de eventos

    private fun onConnect(userId: Int, userType: String, userName: String): Emitter.Listener {
        return Emitter.Listener {
            isConnected = true
            Log.d(TAG, "✅ WebSocket conectado!")

            // Envia dados de autenticação
            val authData = JSONObject().apply {
                put("userId", userId)
                put("userType", userType)
                put("userName", userName)
            }
            socket?.emit("user_connected", authData)
            Log.d(TAG, "👤 Autenticação enviada: $userName ($userType)")
        }
    }

    private val onDisconnect = Emitter.Listener {
        isConnected = false
        Log.d(TAG, "❌ WebSocket desconectado")
    }

    private val onConnectError = Emitter.Listener { args ->
        Log.e(TAG, "❌ Erro de conexão WebSocket: ${args.joinToString()}")
    }

    private val onLocationUpdated = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val servicoId = data.getInt("servicoId")
            val latitude = data.getDouble("latitude")
            val longitude = data.getDouble("longitude")
            val prestadorName = data.optString("prestadorName", "")
            val timestamp = data.optString("timestamp", "")

            val localizacao = LocalizacaoWebSocket(
                servicoId = servicoId,
                latitude = latitude,
                longitude = longitude,
                prestadorName = prestadorName,
                timestamp = timestamp
            )

            _localizacaoAtual.value = localizacao
            Log.d(TAG, "📍 Localização atualizada: $latitude, $longitude - $prestadorName")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao processar location_updated", e)
        }
    }

    fun isConectado(): Boolean = isConnected
}

/**
 * Modelo de dados para localização via WebSocket
 */
data class LocalizacaoWebSocket(
    val servicoId: Int,
    val latitude: Double,
    val longitude: Double,
    val prestadorName: String,
    val timestamp: String
)

