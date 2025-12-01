package com.exemple.facilita.screens

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.exemple.facilita.viewmodel.CallViewModel
import org.json.JSONObject

/**
 * 📞 ACTIVITY DE CHAMADA RECEBIDA
 * Tela em tela cheia que aparece quando recebe uma chamada
 */
class IncomingCallActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callerName = intent.getStringExtra("callerName") ?: "Desconhecido"
        val callType = intent.getStringExtra("callType") ?: "audio"
        val servicoId = intent.getStringExtra("servicoId") ?: "0"
        val callerId = intent.getStringExtra("callerId") ?: "0"
        val callId = intent.getStringExtra("callId") ?: ""

        setContent {
            IncomingCallScreen(
                callerName = callerName,
                callType = callType,
                servicoId = servicoId,
                callerId = callerId,
                callId = callId,
                onAccept = {
                    // Aceitar chamada e ir para tela de chamada ativa
                    finish()
                },
                onReject = {
                    // Rejeitar e fechar
                    finish()
                }
            )
        }
    }
}

/**
 * 📞 TELA DE CHAMADA RECEBIDA
 */
@Composable
fun IncomingCallScreen(
    callerName: String,
    callType: String,
    servicoId: String,
    callerId: String,
    callId: String,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    viewModel: CallViewModel = viewModel()
) {
    val greenColor = Color(0xFF019D31)
    val context = LocalContext.current

    // Animação de pulso
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF0D0D0D)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Parte superior com info da chamada
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar com animação
                Surface(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(scale),
                    shape = CircleShape,
                    color = greenColor.copy(alpha = 0.2f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.background(
                            androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(
                                    greenColor.copy(alpha = 0.4f),
                                    greenColor.copy(alpha = 0.1f)
                                )
                            )
                        )
                    ) {
                        Text(
                            text = callerName.firstOrNull()?.uppercase() ?: "?",
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Nome do chamador
                Text(
                    text = callerName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tipo de chamada
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (callType == "video") Icons.Default.Videocam else Icons.Default.Call,
                        contentDescription = null,
                        tint = greenColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (callType == "video") "Chamada de vídeo" else "Chamada de áudio",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Texto "Chamando..."
                Text(
                    text = "Chamando...",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            // Botões de ação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botão Rejeitar
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FloatingActionButton(
                        onClick = {
                            // Rejeitar chamada
                            val callData = JSONObject().apply {
                                put("servicoId", servicoId)
                                put("callId", callId)
                                put("callerId", callerId)
                                put("reason", "user_declined")
                            }
                            viewModel.rejectCall("user_declined")
                            onReject()
                        },
                        containerColor = Color.Red,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Rejeitar",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Rejeitar",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Botão Aceitar
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FloatingActionButton(
                        onClick = {
                            // Aceitar chamada
                            val callData = JSONObject().apply {
                                put("servicoId", servicoId)
                                put("callId", callId)
                                put("callerId", callerId)
                                put("callType", callType)
                            }

                            // Inicializar WebRTC
                            viewModel.initializeWebRTC()

                            // Aceitar chamada
                            viewModel.acceptCall(callData)

                            // Ir para tela de chamada ativa
                            val intent = if (callType == "video") {
                                android.content.Intent(context, context::class.java).apply {
                                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                    // Navegar para TelaVideoCall
                                }
                            } else {
                                android.content.Intent(context, context::class.java).apply {
                                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                    // Navegar para TelaAudioCall
                                }
                            }

                            onAccept()
                        },
                        containerColor = greenColor,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Aceitar",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aceitar",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

