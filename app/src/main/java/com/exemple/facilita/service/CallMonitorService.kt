package com.exemple.facilita.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.exemple.facilita.network.WebSocketManager
import com.exemple.facilita.screens.IncomingCallActivity
import org.json.JSONObject

/**
 * 📞 SERVIÇO DE MONITORAMENTO DE CHAMADAS
 * Fica em background ouvindo chamadas recebidas
 */
class CallMonitorService : Service() {

    private val webSocketManager = WebSocketManager.getInstance()

    companion object {
        private const val TAG = "CallMonitorService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "")
        Log.d(TAG, "╔════════════════════════════════════════════════╗")
        Log.d(TAG, "║  📞 CallMonitorService INICIADO               ║")
        Log.d(TAG, "╚════════════════════════════════════════════════╝")
        Log.d(TAG, "")
        setupCallListener()
    }

    private fun setupCallListener() {
        val socket = webSocketManager.getSocket()

        if (socket == null) {
            Log.e(TAG, "❌❌❌ SOCKET É NULL! WebSocket não está conectado!")
            Log.e(TAG, "   O serviço não vai funcionar sem conexão WebSocket")
            return
        }

        Log.d(TAG, "✅ Socket obtido: ${socket.connected()}")
        Log.d(TAG, "🔌 Registrando listener para 'call:incoming'...")

        socket.on("call:incoming") { data ->
            try {
                Log.d(TAG, "")
                Log.d(TAG, "╔════════════════════════════════════════════════╗")
                Log.d(TAG, "║  🔥🔥🔥 CALL:INCOMING EVENTO RECEBIDO! 🔥🔥🔥 ║")
                Log.d(TAG, "╚════════════════════════════════════════════════╝")

                if (data.isEmpty()) {
                    Log.e(TAG, "❌ Dados vazios no evento call:incoming!")
                    return@on
                }

                Log.d(TAG, "📦 Total de argumentos: ${data.size}")
                Log.d(TAG, "📦 Tipo do primeiro arg: ${data[0]?.javaClass?.simpleName}")

                val callData = data[0] as JSONObject

                Log.d(TAG, "📄 JSON completo recebido:")
                Log.d(TAG, callData.toString(2))

                val callerName = callData.optString("callerName", "Desconhecido")
                val callType = callData.optString("callType", "audio")
                val servicoId = callData.optString("servicoId", "0")
                val callerId = callData.optString("callerId", "0")
                val callId = callData.optString("callId", "")

                Log.d(TAG, "")
                Log.d(TAG, "📋 DADOS EXTRAÍDOS:")
                Log.d(TAG, "   👤 De: $callerName")
                Log.d(TAG, "   📱 Tipo: $callType")
                Log.d(TAG, "   🆔 CallId: $callId")
                Log.d(TAG, "   🏠 ServiceId: $servicoId")
                Log.d(TAG, "   👥 CallerId: $callerId")

                Log.d(TAG, "")
                Log.d(TAG, "🚀 Criando Intent para abrir IncomingCallActivity...")

                // Abrir activity de chamada recebida em tela cheia
                val intent = Intent(applicationContext, IncomingCallActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("callerName", callerName)
                    putExtra("callType", callType)
                    putExtra("servicoId", servicoId)
                    putExtra("callerId", callerId)
                    putExtra("callId", callId)
                }

                Log.d(TAG, "🎬 Chamando startActivity...")
                startActivity(intent)

                Log.d(TAG, "")
                Log.d(TAG, "✅✅✅ IncomingCallActivity INICIADA COM SUCESSO! ✅✅✅")
                Log.d(TAG, "")

            } catch (e: Exception) {
                Log.e(TAG, "")
                Log.e(TAG, "❌❌❌ ERRO CRÍTICO ao processar chamada! ❌❌❌")
                Log.e(TAG, "Mensagem: ${e.message}")
                Log.e(TAG, "Stack trace:")
                e.printStackTrace()
                Log.e(TAG, "")
            }
        }

        Log.d(TAG, "")
        Log.d(TAG, "✅ Listener 'call:incoming' REGISTRADO com sucesso!")
        Log.d(TAG, "")
        Log.d(TAG, "📊 STATUS FINAL:")
        Log.d(TAG, "   Socket conectado: ${socket.connected()}")
        Log.d(TAG, "   Socket ID: ${socket.id()}")
        Log.d(TAG, "   Listeners ativos: ${socket.hasListeners("call:incoming")}")
        Log.d(TAG, "")
        Log.d(TAG, "⏳ Aguardando chamadas...")
        Log.d(TAG, "")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "📴 CallMonitorService destruído")
    }
}

