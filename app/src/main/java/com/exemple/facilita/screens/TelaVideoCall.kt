package com.exemple.facilita.screens

import android.Manifest
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exemple.facilita.utils.TokenManager
import com.exemple.facilita.viewmodel.CallViewModel
import com.exemple.facilita.webrtc.CallState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay

/**
 * 📹 TELA DE CHAMADA DE VÍDEO (Versão Simplificada)
 * Interface para chamadas de vídeo WebRTC
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TelaVideoCall(
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
    val localVideoEnabled by viewModel.localVideoEnabled.collectAsState()
    val localAudioEnabled by viewModel.localAudioEnabled.collectAsState()
    val callDuration by viewModel.callDuration.collectAsState()

    // Dados do usuário
    val userId = TokenManager.obterUserId(context)?.toString() ?: "0"
    val userName = TokenManager.obterNomeUsuario(context) ?: "Usuário"

    // Permissões
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )

    // Inicialização
    LaunchedEffect(Unit) {
        if (permissionsState.allPermissionsGranted) {
            Log.d("TelaVideoCall", "✅ Permissões concedidas, inicializando...")
            viewModel.initializeWebRTC()

            // Aguarda inicialização
            delay(500)

            // Iniciar chamada
            viewModel.startVideoCall(
                servicoId = servicoId,
                targetUserId = prestadorId,
                callerId = userId,
                callerName = userName
            )
        } else {
            Log.d("TelaVideoCall", "⚠️ Solicitando permissões...")
            permissionsState.launchMultiplePermissionRequest()
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

    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) }

    // Inicializar CameraX
    DisposableEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Preview da câmera local (tela inteira)
        if (localVideoEnabled && cameraProvider != null) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { previewView ->
                val preview = Preview.Builder().build()
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

                try {
                    cameraProvider?.unbindAll()
                    cameraProvider?.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    Log.d("TelaVideoCall", "✅ Câmera iniciada com sucesso")
                } catch (e: Exception) {
                    Log.e("TelaVideoCall", "❌ Erro ao iniciar câmera", e)
                }
            }
        } else {
            // Tela preta com avatar quando vídeo está desligado
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        color = greenColor.copy(alpha = 0.3f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (localVideoEnabled) "Iniciando..." else "Vídeo desligado",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Área para vídeo remoto (seria onde aparece o vídeo do prestador)
        // Por enquanto mostra status da chamada
        if (callState !is CallState.ActiveCall) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = greenColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = when (callState) {
                                is CallState.Calling -> "Chamando ${prestadorNome}..."
                                is CallState.OutgoingCall -> "Aguardando resposta..."
                                else -> "Conectando..."
                            },
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            // Quando ativa, mostra o nome do prestador no topo
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp),
                shape = MaterialTheme.shapes.medium,
                color = Color.Black.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "$prestadorNome • ${formatDuration(callDuration)}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Controles da chamada
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Alternar áudio
            CallControlButton(
                icon = if (localAudioEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                label = if (localAudioEnabled) "Mic" else "Mudo",
                backgroundColor = if (localAudioEnabled) Color.White.copy(alpha = 0.2f) else Color.Red,
                onClick = { viewModel.toggleAudio() }
            )

            // Alternar vídeo
            CallControlButton(
                icon = if (localVideoEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                label = if (localVideoEnabled) "Vídeo" else "Sem vídeo",
                backgroundColor = if (localVideoEnabled) Color.White.copy(alpha = 0.2f) else Color.Red,
                onClick = { viewModel.toggleVideo() }
            )

            // Trocar câmera
            CallControlButton(
                icon = Icons.Default.FlipCameraAndroid,
                label = "Virar",
                backgroundColor = Color.White.copy(alpha = 0.2f),
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                        CameraSelector.LENS_FACING_BACK
                    } else {
                        CameraSelector.LENS_FACING_FRONT
                    }
                    Log.d("TelaVideoCall", "🔄 Câmera trocada para: ${if (lensFacing == CameraSelector.LENS_FACING_FRONT) "Frontal" else "Traseira"}")
                }
            )

            // Encerrar chamada
            CallControlButton(
                icon = Icons.Default.CallEnd,
                label = "Encerrar",
                backgroundColor = Color.Red,
                onClick = {
                    viewModel.endCall()
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * Botão de controle de chamada
 */
@Composable
fun CallControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = backgroundColor,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

/**
 * Formata duração em segundos para MM:SS
 */
fun formatDuration(seconds: Long): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}

