package com.exemple.facilita.screens

import android.widget.Toast
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exemple.facilita.data.models.StatusServicoApi
import com.exemple.facilita.utils.TokenManager
import com.exemple.facilita.viewmodel.ServicoViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAguardoServicoAtualizada(
    navController: NavController,
    servicoId: String
) {
    val context = LocalContext.current
    val viewModel: ServicoViewModel = viewModel()
    val scope = rememberCoroutineScope()

    val servico by viewModel.servico.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var mostrarDialogoCancelar by remember { mutableStateOf(false) }
    var mostrarMapa by remember { mutableStateOf(false) }
    var mostrarDialogoPrestadorChegou by remember { mutableStateOf(false) }
    var prestadorChegouMostrado by remember { mutableStateOf(false) }

    val token = TokenManager.obterToken(context) ?: ""

    // Inicia o monitoramento quando a tela é criada
    LaunchedEffect(servicoId) {
        if (token.isNotEmpty()) {
            viewModel.iniciarMonitoramento(token, servicoId)
        }
    }

    // Para o monitoramento quando sai da tela
    DisposableEffect(Unit) {
        onDispose {
            viewModel.pararMonitoramento()
        }
    }

    // Mostrar erro se houver
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.limparErro()
        }
    }

    // Mostrar mapa quando prestador aceitar
    LaunchedEffect(servico?.status) {
        mostrarMapa = servico?.status in listOf("ACEITO", "EM_ANDAMENTO")

        // Mostra notificação quando prestador está chegando
        if (servico?.status == "ACEITO" && !prestadorChegouMostrado) {
            val tempoEstimado = viewModel.calcularTempoEstimado()
            // Se tempo estimado é menor que 2 minutos, mostra que está chegando
            if (tempoEstimado <= 2) {
                mostrarDialogoPrestadorChegou = true
                prestadorChegouMostrado = true
            }
        }

        // Redireciona para tela de corrida quando serviço iniciar
        if (servico?.status == "EM_ANDAMENTO") {
            navController.navigate("tela_corrida_andamento/$servicoId") {
                popUpTo("tela_aguardo_servico/$servicoId") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            HeaderAguardo(
                servicoId = servicoId,
                onVoltarClick = { mostrarDialogoCancelar = true }
            )

            if (mostrarMapa && servico != null) {
                // Mapa com localização do prestador
                MapaRastreamento(
                    servico = servico!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                // Animação de aguardando
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    AnimacaoAguardando(servico?.status)
                }
            }

            // Informações na parte inferior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                // Status
                StatusTextoAtualizado(
                    status = servico?.status,
                    tempoEstimado = viewModel.calcularTempoEstimado()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Card do Prestador (se já foi aceito)
                servico?.prestador?.let { prestador ->
                    CardPrestadorAtualizado(prestador = prestador)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Informações do percurso
                servico?.let {
                    CardPercursoAtualizado(
                        origem = it.localizacao?.endereco ?: it.localizacao?.bairro ?: "Origem",
                        destino = it.categoria?.nome ?: "Destino"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Botão cancelar (só se ainda não iniciou)
                if (servico?.status != "EM_ANDAMENTO" && servico?.status != "CONCLUIDO") {
                    BotaoCancelarAtualizado(
                        onClick = { mostrarDialogoCancelar = true }
                    )
                }
            }
        }

        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF00B14F))
            }
        }
    }

    // Dialog de cancelamento
    if (mostrarDialogoCancelar) {
        DialogCancelamentoAtualizado(
            onDismiss = { mostrarDialogoCancelar = false },
            onConfirmar = {
                scope.launch {
                    viewModel.cancelarServico(
                        token = token,
                        servicoId = servicoId,
                        onSuccess = {
                            Toast.makeText(context, "Serviço cancelado", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onError = { erro ->
                            Toast.makeText(context, erro, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                mostrarDialogoCancelar = false
            }
        )
    }

    // Dialog quando prestador está chegando
    if (mostrarDialogoPrestadorChegou) {
        DialogPrestadorChegando(
            prestadorNome = servico?.prestador?.nome ?: "Prestador",
            onDismiss = { mostrarDialogoPrestadorChegou = false }
        )
    }
}
@Composable
private fun HeaderAguardo(
    servicoId: String,
    onVoltarClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF3C604B), Color(0xFF00B14F))
                ),
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onVoltarClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Voltar",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Pedido #${servicoId.take(8)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun MapaRastreamento(
    servico: com.exemple.facilita.data.models.Servico,
    modifier: Modifier = Modifier
) {
    val prestador = servico.prestador
    val localizacao = servico.localizacao

    // Usa localização do prestador se disponível, senão usa localização do serviço
    val latPrestador = prestador?.latitudeAtual ?: localizacao?.latitude ?: -23.550520
    val lonPrestador = prestador?.longitudeAtual ?: localizacao?.longitude ?: -46.633308

    val posicaoPrestador = LatLng(latPrestador, lonPrestador)

    // Por enquanto, usamos a mesma localização como origem e um ponto próximo como destino
    // TODO: Quando tiver origem/destino na API, usar os valores corretos
    val posicaoOrigem = LatLng(latPrestador, lonPrestador)
    val posicaoDestino = LatLng(latPrestador + 0.01, lonPrestador + 0.01)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(posicaoPrestador, 15f)
    }

    // Atualiza câmera quando prestador se move
    LaunchedEffect(latPrestador, lonPrestador) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(posicaoPrestador, 15f),
            1000
        )
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = false),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false
        )
    ) {
        // Marcador do prestador
        Marker(
            state = MarkerState(position = posicaoPrestador),
            title = prestador?.usuario?.nome ?: prestador?.nome ?: "Prestador",
            snippet = "A caminho",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        )

        // Marcador da origem
        Marker(
            state = MarkerState(position = posicaoOrigem),
            title = "Origem",
            snippet = localizacao?.endereco ?: "Local de origem",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        )

        // Marcador do destino
        Marker(
            state = MarkerState(position = posicaoDestino),
            title = "Destino",
            snippet = "Local de destino",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        )
    }
}

@Composable
private fun AnimacaoAguardando(status: String?) {
    val infiniteTransition = rememberInfiniteTransition(label = "aguardando")

    val rotacao by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotacao"
    )

    val pulso by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulso"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(40.dp)
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .scale(pulso),
            contentAlignment = Alignment.Center
        ) {
            // Círculo externo girando
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .rotate(rotacao)
                    .border(
                        width = 4.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFF00B14F),
                                Color(0xFF3C604B),
                                Color(0xFF00B14F)
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Círculo interno
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF00B14F),
                                Color(0xFF3C604B)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Procurando prestador...",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D2D)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Aguarde, estamos encontrando o melhor prestador para você",
            fontSize = 14.sp,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatusTextoAtualizado(
    status: String?,
    tempoEstimado: Int
) {
    val (titulo, subtitulo) = when (status) {
        "AGUARDANDO" -> Pair(
            "Procurando prestador...",
            "Aguarde, em breve encontraremos alguém"
        )
        "ACEITO" -> Pair(
            "Prestador encontrado!",
            "Chegará em aproximadamente $tempoEstimado min"
        )
        "EM_ANDAMENTO" -> Pair(
            "Serviço em andamento",
            "O prestador iniciou o serviço"
        )
        "CONCLUIDO" -> Pair(
            "Serviço concluído!",
            "Obrigado por usar o Facilita"
        )
        "CANCELADO" -> Pair(
            "Serviço cancelado",
            "Este serviço foi cancelado"
        )
        else -> Pair(
            "Carregando...",
            ""
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = titulo,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D2D)
        )
        if (subtitulo.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitulo,
                fontSize = 14.sp,
                color = Color(0xFF6D6D6D)
            )
        }
    }
}

@Composable
private fun CardPrestadorAtualizado(
    prestador: com.exemple.facilita.data.models.PrestadorInfo
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
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
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = prestador.nome,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = prestador.avaliacao.toString(),
                        fontSize = 14.sp,
                        color = Color(0xFF6D6D6D)
                    )
                }

                // Informações do veículo
                prestador.veiculo?.let { veiculo ->
                    Spacer(modifier = Modifier.height(4.dp))
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
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
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
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CardPercursoAtualizado(
    origem: String,
    destino: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Percurso",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Origem
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF00B14F), CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Origem",
                        fontSize = 12.sp,
                        color = Color(0xFF6D6D6D)
                    )
                    Text(
                        text = origem,
                        fontSize = 14.sp,
                        color = Color(0xFF2D2D2D),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Linha
            Box(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .width(2.dp)
                    .height(24.dp)
                    .background(Color(0xFFE0E0E0))
            )

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
                        text = destino,
                        fontSize = 14.sp,
                        color = Color(0xFF2D2D2D),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun BotaoCancelarAtualizado(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFFF6B6B)
        ),
        border = BorderStroke(2.dp, Color(0xFFFF6B6B))
    ) {
        Icon(
            Icons.Default.Cancel,
            contentDescription = null,
            tint = Color(0xFFFF6B6B),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Cancelar Pedido",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DialogCancelamentoAtualizado(
    onDismiss: () -> Unit,
    onConfirmar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Cancelar pedido?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )
        },
        text = {
            Column {
                Text(
                    text = "Tem certeza que deseja cancelar este pedido?",
                    fontSize = 15.sp,
                    color = Color(0xFF6D6D6D)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Esta ação não poderá ser desfeita.",
                    fontSize = 13.sp,
                    color = Color(0xFFFF6B6B)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B6B)
                )
            ) {
                Text("Sim, cancelar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Não", color = Color(0xFF6D6D6D))
            }
        }
    )
}

@Composable
private fun DialogPrestadorChegando(
    prestadorNome: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color(0xFF00B14F).copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF00B14F),
                    modifier = Modifier.size(48.dp)
                )
            }
        },
        title = {
            Text(
                text = "Prestador está chegando!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$prestadorNome está a menos de 2 minutos de distância.",
                    fontSize = 15.sp,
                    color = Color(0xFF6D6D6D),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Prepare-se! O serviço iniciará em breve.",
                    fontSize = 14.sp,
                    color = Color(0xFF00B14F),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00B14F)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entendi", fontWeight = FontWeight.Bold)
            }
        }
    )
}
