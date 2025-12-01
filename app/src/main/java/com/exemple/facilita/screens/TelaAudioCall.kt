package com.exemple.facilita.screens

import android.Manifest
import android.util.Log
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
import androidx.navigation.NavController
import com.exemple.facilita.utils.TokenManager
import com.exemple.facilita.viewmodel.CallViewModel
import com.exemple.facilita.webrtc.CallState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay

/**
 * 🎤 TELA DE CHAMADA DE ÁUDIO (Versão Simplificada)
 * Interface para chamadas de áudio WebRTC
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TelaAudioCall(
    navController: NavController,
    servicoId: String,
    prestadorId: String,
    prestadorNome: String,
    viewModel: CallViewModel = viewModel()
) {
    val context = LocalContext.current
    val greenColor = Color(0xFF019D31)

    // Estados
    val callState by viewModel.callState.collectAsState()
    val localAudioEnabled by viewModel.localAudioEnabled.collectAsState()
    val callDuration by viewModel.callDuration.collectAsState()

    // Dados do usuário
    val userId = TokenManager.obterUserId(context)?.toString() ?: "0"
    val userName = TokenManager.obterNomeUsuario(context) ?: "Usuário"

    // Permissão de áudio
    val audioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    // Inicialização
    LaunchedEffect(Unit) {
        // Solicitar permissão primeiro
        when {
            audioPermissionState.status is com.google.accompanist.permissions.PermissionStatus.Granted -> {
                Log.d("TelaAudioCall", "✅ Permissão já concedida, inicializando...")
                viewModel.initializeWebRTC()
                delay(500)
                viewModel.startAudioCall(
                    servicoId = servicoId,
                    targetUserId = prestadorId,
                    callerId = userId,
                    callerName = userName
                )
            }
            else -> {
                Log.d("TelaAudioCall", "⚠️ Solicitando permissão...")
                audioPermissionState.launchPermissionRequest()
            }
        }
    }

    // Observa mudanças no estado da permissão
    LaunchedEffect(audioPermissionState.status) {
        if (audioPermissionState.status is com.google.accompanist.permissions.PermissionStatus.Granted) {
            Log.d("TelaAudioCall", "✅ Permissão concedida, inicializando...")
            viewModel.initializeWebRTC()

            // Aguarda inicialização
            delay(500)

            // Iniciar chamada de áudio
            viewModel.startAudioCall(
                servicoId = servicoId,
                targetUserId = prestadorId,
                callerId = userId,
                callerName = userName
            )
        }
    }

    // Observa mudanças no estado da chamada
    LaunchedEffect(callState) {
        when (callState) {
            is CallState.Ended, is CallState.Rejected, is CallState.Cancelled, is CallState.Failed -> {
                // Volta para tela anterior
                delay(1000)
                navController.popBackStack()
            }
            else -> {}
        }
    }

    // Animação de pulso para quando está chamando
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // UI
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
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Avatar grande
            Surface(
                modifier = Modifier
                    .size(180.dp)
                    .scale(if (callState is CallState.Calling || callState is CallState.OutgoingCall) scale else 1f),
                shape = CircleShape,
                color = greenColor.copy(alpha = 0.2f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(
                        androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                greenColor.copy(alpha = 0.3f),
                                greenColor.copy(alpha = 0.1f)
                            )
                        )
                    )
                ) {
                    Text(
                        text = prestadorNome.firstOrNull()?.uppercase() ?: "P",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Nome do prestador
            Text(
                text = prestadorNome,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Estado da chamada
            Text(
                text = when (callState) {
                    is CallState.Calling -> "Chamando..."
                    is CallState.OutgoingCall -> "Aguardando resposta..."
                    is CallState.ActiveCall -> formatDuration(callDuration)
                    is CallState.IncomingCall -> "Chamada recebida"
                    is CallState.Ended -> "Chamada encerrada"
                    is CallState.Rejected -> "Chamada rejeitada"
                    is CallState.Cancelled -> "Chamada cancelada"
                    is CallState.Failed -> "Falha na chamada"
                    else -> "Conectando..."
                },
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Indicador de áudio
            if (callState is CallState.ActiveCall) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 40.dp)
                ) {
                    Icon(
                        imageVector = if (localAudioEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = null,
                        tint = if (localAudioEnabled) greenColor else Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (localAudioEnabled) "Microfone ligado" else "Microfone desligado",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }
            }

            // Controles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Alternar áudio
                CallControlButton(
                    icon = if (localAudioEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                    label = if (localAudioEnabled) "Desligar Mic" else "Ligar Mic",
                    backgroundColor = if (localAudioEnabled) Color.White.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.8f),
                    onClick = { viewModel.toggleAudio() }
                )

                // Encerrar chamada
                FloatingActionButton(
                    onClick = {
                        viewModel.endCall()
                        navController.popBackStack()
                    },
                    containerColor = Color.Red,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "Encerrar",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Alto-falante (placeholder)
                CallControlButton(
                    icon = Icons.Default.VolumeUp,
                    label = "Alto-falante",
                    backgroundColor = Color.White.copy(alpha = 0.15f),
                    onClick = {
                        // TODO: Implementar toggle de alto-falante
                    }
                )
            }
        }
    }
}

