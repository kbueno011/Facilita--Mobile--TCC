package com.exemple.facilita.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exemple.facilita.service.WebSocketManager
import com.exemple.facilita.utils.TokenManager
import com.exemple.facilita.viewmodel.ServicoViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCorridaEmAndamento(
    navController: NavController,
    servicoId: String
) {
    val context = LocalContext.current
    val viewModel: ServicoViewModel = viewModel()

    val servico by viewModel.servico.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Localização em tempo real via WebSocket
    val localizacaoWebSocket by WebSocketManager.localizacaoAtual.collectAsState()

    var mostrarDetalhes by remember { mutableStateOf(false) }

    val token = TokenManager.obterToken(context) ?: ""

    // Inicia o monitoramento via API (polling de 10 em 10 segundos)
    LaunchedEffect(servicoId) {
        if (token.isNotEmpty()) {
            viewModel.iniciarMonitoramento(token, servicoId)
        }
    }

    // Conecta ao WebSocket quando o serviço está ativo
    LaunchedEffect(servico?.id, servico?.status) {
        servico?.let { servicoAtual ->
            if (servicoAtual.status == "EM_ANDAMENTO" && servicoAtual.prestador != null) {
                Log.d("TelaCorreda", "🔌 Conectando ao WebSocket...")

                // Conecta como contratante
                WebSocketManager.conectar(
                    userId = servicoAtual.idContratante,
                    userType = "contratante",
                    userName = "Contratante"
                )

                // Entra na sala do serviço
                WebSocketManager.entrarNaSala(servicoId)
            }
        }
    }

    // Para o monitoramento e desconecta WebSocket quando sai
    DisposableEffect(Unit) {
        onDispose {
            viewModel.pararMonitoramento()
            WebSocketManager.desconectar()
            Log.d("TelaCorreda", "🔌 WebSocket desconectado")
        }
    }

    // Mostrar erro
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.limparErro()
        }
    }

    // Redireciona quando concluído
    LaunchedEffect(servico?.status) {
        if (servico?.status == "CONCLUIDO") {
            Toast.makeText(context, "Serviço concluído!", Toast.LENGTH_SHORT).show()
            // TODO: Navegar para tela de avaliação
            navController.navigate("tela_home") {
                popUpTo("tela_home") { inclusive = false }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa em tela cheia
        servico?.let { servicoAtual ->
            MapaCorridaEmAndamento(
                servico = servicoAtual,
                localizacaoWebSocket = localizacaoWebSocket,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Header flutuante
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            HeaderCorridaFlutuante(
                servicoId = servicoId,
                status = servico?.status
            )
        }

        // Card de informações na parte inferior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            servico?.let { servicoAtual ->
                CardInformacoesCorridaFlutuante(
                    servico = servicoAtual,
                    tempoEstimado = viewModel.calcularTempoEstimado(),
                    mostrarDetalhes = mostrarDetalhes,
                    onToggleDetalhes = { mostrarDetalhes = !mostrarDetalhes }
                )
            }
        }

        // Loading
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
}

@Composable
private fun MapaCorridaEmAndamento(
    servico: com.exemple.facilita.data.models.Servico,
    localizacaoWebSocket: com.exemple.facilita.service.LocalizacaoWebSocket?,
    modifier: Modifier = Modifier
) {
    val prestador = servico.prestador
    val localizacao = servico.localizacao

    // Usa localização do WebSocket se disponível (tempo real), senão usa da API
    val latPrestador = localizacaoWebSocket?.latitude
        ?: prestador?.latitudeAtual
        ?: localizacao?.latitude
        ?: -23.550520
    val lonPrestador = localizacaoWebSocket?.longitude
        ?: prestador?.longitudeAtual
        ?: localizacao?.longitude
        ?: -46.633308

    val posicaoPrestador = remember(latPrestador, lonPrestador) {
        LatLng(latPrestador, lonPrestador)
    }

    // Por enquanto usa um ponto próximo como destino
    // TODO: Quando tiver destino na API, usar os valores corretos
    val posicaoDestino = LatLng(latPrestador + 0.01, lonPrestador + 0.01)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(posicaoPrestador, 16f)
    }

    // Atualiza câmera seguindo o prestador em tempo real
    LaunchedEffect(latPrestador, lonPrestador) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(posicaoPrestador, 16f),
            1000
        )
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = false
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            compassEnabled = true,
            rotationGesturesEnabled = true,
            scrollGesturesEnabled = true,
            tiltGesturesEnabled = false,
            zoomGesturesEnabled = true
        )
    ) {
        // Marcador do prestador (carro em movimento) - atualizado em tempo real
        Marker(
            state = rememberMarkerState(position = posicaoPrestador),
            title = localizacaoWebSocket?.prestadorName
                ?: prestador?.usuario?.nome
                ?: prestador?.nome
                ?: "Prestador",
            snippet = "Em movimento",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        )

        // Marcador do destino
        Marker(
            state = rememberMarkerState(position = posicaoDestino),
            title = "Destino",
            snippet = localizacao?.endereco ?: "Local de destino",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        )
    }
}

@Composable
private fun HeaderCorridaFlutuante(
    servicoId: String,
    status: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Pedido #${servicoId.take(8)}",
                    fontSize = 14.sp,
                    color = Color(0xFF6D6D6D)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF00B14F), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (status) {
                            "EM_ANDAMENTO" -> "Em andamento"
                            "ACEITO" -> "A caminho do destino"
                            else -> "Aguarde..."
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = Color(0xFF00B14F),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun CardInformacoesCorridaFlutuante(
    servico: com.exemple.facilita.data.models.Servico,
    tempoEstimado: Int,
    mostrarDetalhes: Boolean,
    onToggleDetalhes: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Handle para arrastar
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
                    .clickable { onToggleDetalhes() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tempo estimado destacado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tempo estimado",
                        fontSize = 14.sp,
                        color = Color(0xFF6D6D6D)
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$tempoEstimado",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00B14F)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "min",
                            fontSize = 16.sp,
                            color = Color(0xFF6D6D6D),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                // Animação de carro em movimento
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            Color(0xFF00B14F).copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = Color(0xFF00B14F),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            if (mostrarDetalhes) {
                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(color = Color(0xFFE0E0E0))

                Spacer(modifier = Modifier.height(20.dp))

                // Informações do prestador
                servico.prestador?.let { prestador ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF3C604B), Color(0xFF00B14F))
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = prestador.nome.take(2).uppercase(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = prestador.nome,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D2D2D)
                            )
                            prestador.veiculo?.let { veiculo ->
                                Text(
                                    text = "${veiculo.marca} ${veiculo.modelo} • ${veiculo.placa}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6D6D6D)
                                )
                            }
                        }

                        // Botões de contato
                        IconButton(
                            onClick = { /* Ligar */ },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF00B14F), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = "Ligar",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { /* Chat */ },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF00B14F), CircleShape)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Message,
                                contentDescription = "Mensagem",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(color = Color(0xFFE0E0E0))

                Spacer(modifier = Modifier.height(20.dp))

                // Destino
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFFFF6B6B), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Destino",
                            fontSize = 12.sp,
                            color = Color(0xFF6D6D6D)
                        )
                        Text(
                            text = servico.localizacao?.endereco ?: servico.categoria?.nome ?: "Destino",
                            fontSize = 14.sp,
                            color = Color(0xFF2D2D2D),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

