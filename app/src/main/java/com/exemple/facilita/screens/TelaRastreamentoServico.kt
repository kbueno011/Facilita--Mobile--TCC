package com.exemple.facilita.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exemple.facilita.utils.TokenManager
import com.exemple.facilita.viewmodel.ServicoViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRastreamentoServico(
    navController: NavController,
    servicoId: String
) {
    val context = LocalContext.current
    val viewModel: ServicoViewModel = viewModel()

    val servico by viewModel.servico.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val token = TokenManager.obterToken(context) ?: ""

    var mostrarDialogoCancelar by remember { mutableStateOf(false) }

    // Inicia o monitoramento
    LaunchedEffect(servicoId) {
        if (token.isNotEmpty()) {
            viewModel.iniciarMonitoramento(token, servicoId)
        }
    }

    // Monitora mudanças de status
    LaunchedEffect(servico?.status) {
        when (servico?.status) {
            "CONCLUIDO" -> {
                Toast.makeText(context, "Serviço concluído com sucesso!", Toast.LENGTH_LONG).show()
                delay(2000)
                navController.navigate("tela_home") {
                    popUpTo("tela_home") { inclusive = true }
                }
            }
            "CANCELADO" -> {
                Toast.makeText(context, "Serviço cancelado", Toast.LENGTH_SHORT).show()
                navController.navigate("tela_home") {
                    popUpTo("tela_home") { inclusive = true }
                }
            }
        }
    }

    // Posições no mapa
    val prestadorLat = servico?.prestador?.latitudeAtual ?: -23.550520
    val prestadorLng = servico?.prestador?.longitudeAtual ?: -46.633308
    val prestadorPos = LatLng(prestadorLat, prestadorLng)

    val destinoLat = servico?.localizacao?.latitude ?: -23.561414
    val destinoLng = servico?.localizacao?.longitude ?: -46.656139
    val destinoPos = LatLng(destinoLat, destinoLng)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(prestadorPos, 15f)
    }

    // Atualiza câmera quando prestador se move
    LaunchedEffect(prestadorLat, prestadorLng) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLng(prestadorPos),
            durationMs = 1000
        )
    }

    val prestadorNome = servico?.prestador?.usuario?.nome ?: "Prestador"
    val tempoEstimado = viewModel.calcularTempoEstimado()

    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        ) {
            // Marcador do prestador
            Marker(
                state = MarkerState(position = prestadorPos),
                title = prestadorNome,
                snippet = "A caminho"
            )

            // Marcador do destino
            Marker(
                state = MarkerState(position = destinoPos),
                title = "Destino",
                snippet = servico?.localizacao?.endereco ?: ""
            )
        }

        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding()
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF00B14F).copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Voltar",
                        tint = Color(0xFF00B14F)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Serviço em andamento",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D)
                    )
                    if (tempoEstimado > 0) {
                        Text(
                            text = "Chega em ~$tempoEstimado min",
                            fontSize = 12.sp,
                            color = Color(0xFF00B14F)
                        )
                    }
                }

                IconButton(
                    onClick = { /* Ajuda */ },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF00B14F).copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Ajuda",
                        tint = Color(0xFF00B14F)
                    )
                }
            }
        }

        // Card inferior com informações
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Informações do prestador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFF00B14F), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = prestadorNome,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D2D)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = servico?.prestador?.avaliacao?.toString() ?: "5.0",
                                fontSize = 14.sp,
                                color = Color(0xFF6D6D6D)
                            )
                        }

                        // Veículo se disponível
                        servico?.prestador?.veiculo?.let { veiculo ->
                            Text(
                                text = "${veiculo.marca} ${veiculo.modelo} - ${veiculo.placa}",
                                fontSize = 12.sp,
                                color = Color(0xFF6D6D6D)
                            )
                        }
                    }

                    // Botões de ação
                    Row {
                        IconButton(
                            onClick = { /* Ligar */ },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFF00B14F), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = "Ligar",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { /* Chat */ },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFF00B14F), CircleShape)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Message,
                                contentDescription = "Mensagem",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(color = Color(0xFFE0E0E0))

                Spacer(modifier = Modifier.height(20.dp))

                // Status
                Text(
                    text = "Status do Serviço",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF00B14F).copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Color(0xFF00B14F)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = when (servico?.status) {
                                "EM_ANDAMENTO" -> "Prestador a caminho"
                                "CONCLUIDO" -> "Serviço concluído"
                                else -> "Em andamento"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2D2D2D)
                        )
                        Text(
                            text = servico?.categoria?.nome ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFF6D6D6D)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botão cancelar
                OutlinedButton(
                    onClick = { mostrarDialogoCancelar = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF6B6B)
                    )
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancelar Serviço")
                }
            }
        }

        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF00B14F))
            }
        }
    }

    // Dialog de cancelamento
    if (mostrarDialogoCancelar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCancelar = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text("Cancelar Serviço?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Tem certeza que deseja cancelar este serviço? Esta ação não pode ser desfeita.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelarServico(
                            token = token,
                            servicoId = servicoId,
                            onSuccess = {
                                mostrarDialogoCancelar = false
                                Toast.makeText(context, "Serviço cancelado", Toast.LENGTH_SHORT).show()
                                navController.navigate("tela_home") {
                                    popUpTo("tela_home") { inclusive = true }
                                }
                            },
                            onError = { erro ->
                                Toast.makeText(context, erro, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B6B)
                    )
                ) {
                    Text("Sim, Cancelar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCancelar = false }) {
                    Text("Não")
                }
            }
        )
    }
}

