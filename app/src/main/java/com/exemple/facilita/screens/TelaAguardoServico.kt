package com.exemple.facilita.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.exemple.facilita.utils.sdp
import com.exemple.facilita.utils.ssp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

enum class StatusServico {
    PROCURANDO,
    PRESTADOR_ENCONTRADO,
    A_CAMINHO,
    CHEGOU
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAguardoServico(
    navController: NavController,
    pedidoId: String? = "12345",
    origem: String? = "Rua Elton Silva, 509",
    destino: String? = "Av. Paulista, 1000"
) {
    var statusAtual by remember { mutableStateOf(StatusServico.PROCURANDO) }
    var tempoEstimado by remember { mutableStateOf(5) }
    var mostrarDialogoCancelar by remember { mutableStateOf(false) }
    var prestadorNome by remember { mutableStateOf("") }
    var prestadorAvaliacao by remember { mutableStateOf(0f) }

    // Simulação de progresso do status
    LaunchedEffect(Unit) {
        delay(3000)
        statusAtual = StatusServico.PRESTADOR_ENCONTRADO
        prestadorNome = "Carlos Silva"
        prestadorAvaliacao = 4.8f
        tempoEstimado = 8

        delay(5000)
        statusAtual = StatusServico.A_CAMINHO
        tempoEstimado = 5

        delay(8000)
        statusAtual = StatusServico.CHEGOU
        tempoEstimado = 0
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F7))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.sdp()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { mostrarDialogoCancelar = true },
                    modifier = Modifier
                        .size(44.sdp())
                        .background(Color(0xFF00B14F).copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Voltar",
                        tint = Color(0xFF00B14F),
                        modifier = Modifier.size(24.sdp())
                    )
                }

                Text(
                    text = "Pedido #$pedidoId",
                    fontSize = 16.ssp(),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )

                IconButton(
                    onClick = { /* Ajuda */ },
                    modifier = Modifier
                        .size(44.sdp())
                        .background(Color(0xFF00B14F).copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Help,
                        contentDescription = "Ajuda",
                        tint = Color(0xFF00B14F),
                        modifier = Modifier.size(24.sdp())
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.sdp()))

            // Animação central futurista
            AnimacaoLoadingFuturista(statusAtual)

            Spacer(modifier = Modifier.height(32.sdp()))

            // Texto do status
            StatusTexto(statusAtual, tempoEstimado)

            Spacer(modifier = Modifier.height(24.sdp()))

            // Card de informações do prestador (quando encontrado)
            if (statusAtual != StatusServico.PROCURANDO) {
                CardPrestador(
                    nome = prestadorNome,
                    avaliacao = prestadorAvaliacao,
                    statusAtual = statusAtual
                )
                Spacer(modifier = Modifier.height(16.sdp()))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Informações do percurso
            CardPercurso(origem ?: "", destino ?: "")

            Spacer(modifier = Modifier.height(16.sdp()))

            // Botão cancelar
            BotaoCancelar(
                onClick = { mostrarDialogoCancelar = true },
                enabled = statusAtual != StatusServico.CHEGOU
            )
        }

        // Diálogo de cancelamento
        if (mostrarDialogoCancelar) {
            DialogoCancelamento(
                onDismiss = { mostrarDialogoCancelar = false },
                onConfirmar = {
                    mostrarDialogoCancelar = false
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun FundoAnimado() {
    val infiniteTransition = rememberInfiniteTransition(label = "fundo")

    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset1"
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset2"
    )

    Canvas(modifier = Modifier.fillMaxSize().blur(150.sdp())) {
        // Círculos flutuantes animados
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFF019D31).copy(alpha = 0.3f), Color.Transparent)
            ),
            radius = 300f,
            center = Offset(
                x = size.width * 0.2f + cos(offset1 * PI / 180).toFloat() * 100f,
                y = size.height * 0.3f + sin(offset1 * PI / 180).toFloat() * 100f
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFF06C755).copy(alpha = 0.2f), Color.Transparent)
            ),
            radius = 400f,
            center = Offset(
                x = size.width * 0.8f + cos(offset2 * PI / 180).toFloat() * 80f,
                y = size.height * 0.7f + sin(offset2 * PI / 180).toFloat() * 80f
            )
        )
    }
}

@Composable
private fun AnimacaoLoadingFuturista(status: StatusServico) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

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

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(240.sdp())
            .scale(if (status == StatusServico.CHEGOU) 1.2f else 1f),
        contentAlignment = Alignment.Center
    ) {
        // Anel externo girando
        Canvas(
            modifier = Modifier
                .size(240.sdp())
                .rotate(rotacao)
        ) {
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        Color(0xFF00B14F).copy(alpha = alpha),
                        Color(0xFF3C604B).copy(alpha = 0.5f),
                        Color(0xFF00B14F).copy(alpha = alpha)
                    )
                ),
                startAngle = 0f,
                sweepAngle = 300f,
                useCenter = false,
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )
        }

        // Anel do meio pulsando
        Canvas(
            modifier = Modifier
                .size(180.sdp())
                .scale(pulso)
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        Color.Transparent,
                        Color(0xFF00B14F).copy(alpha = 0.1f),
                        Color(0xFF00B14F).copy(alpha = 0.3f)
                    )
                ),
                radius = size.minDimension / 2
            )
        }

        // Círculo central com ícone
        Box(
            modifier = Modifier
                .size(140.sdp())
                .background(
                    brush = Brush.radialGradient(
                        listOf(
                            Color(0xFF00B14F),
                            Color(0xFF3C604B)
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 3.sdp(),
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF00B14F), Color(0xFF3C604B))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (status) {
                    StatusServico.PROCURANDO -> Icons.Default.Search
                    StatusServico.PRESTADOR_ENCONTRADO -> Icons.Default.Person
                    StatusServico.A_CAMINHO -> Icons.Default.DirectionsCar
                    StatusServico.CHEGOU -> Icons.Default.CheckCircle
                },
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(60.sdp())
            )
        }

        // Ondas expandindo (quando encontrado ou chegou)
        if (status != StatusServico.PROCURANDO) {
            repeat(3) { index ->
                val delay = index * 700
                var scale by remember { mutableStateOf(0f) }
                var alpha2 by remember { mutableStateOf(1f) }

                LaunchedEffect(Unit) {
                    while (true) {
                        scale = 0f
                        alpha2 = 1f
                        animate(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = tween(2000, easing = LinearEasing)
                        ) { value, _ ->
                            scale = value
                            alpha2 = 1f - value
                        }
                        delay(delay.toLong())
                    }
                }

                Canvas(modifier = Modifier.size(240.sdp()).scale(1f + scale * 0.5f)) {
                    drawCircle(
                        color = Color(0xFF06C755).copy(alpha = alpha2 * 0.5f),
                        radius = size.minDimension / 2,
                        style = Stroke(width = 4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusTexto(status: StatusServico, tempoEstimado: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = when (status) {
                StatusServico.PROCURANDO -> "Procurando prestador..."
                StatusServico.PRESTADOR_ENCONTRADO -> "Prestador encontrado!"
                StatusServico.A_CAMINHO -> "Prestador a caminho"
                StatusServico.CHEGOU -> "Prestador chegou!"
            },
            fontSize = 26.ssp(),
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF2D2D2D),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.sdp()))

        if (tempoEstimado > 0) {
            Text(
                text = when (status) {
                    StatusServico.PROCURANDO -> "Isso pode levar alguns segundos"
                    StatusServico.PRESTADOR_ENCONTRADO -> "Chegará em aproximadamente $tempoEstimado min"
                    StatusServico.A_CAMINHO -> "Tempo estimado: $tempoEstimado min"
                    StatusServico.CHEGOU -> ""
                },
                fontSize = 15.ssp(),
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center
            )
        } else if (status == StatusServico.CHEGOU) {
            Text(
                text = "O prestador está no local",
                fontSize = 15.ssp(),
                color = Color(0xFF00B14F),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CardPrestador(nome: String, avaliacao: Float, statusAtual: StatusServico) {
    val scale by animateFloatAsState(
        targetValue = if (statusAtual == StatusServico.PRESTADOR_ENCONTRADO) 1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(20.sdp()),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.sdp()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar do prestador
            Box(
                modifier = Modifier
                    .size(64.sdp())
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF3C604B), Color(0xFF00B14F))
                        ),
                        shape = CircleShape
                    )
                    .border(3.sdp(), Color(0xFF00B14F).copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.sdp())
                )
            }

            Spacer(modifier = Modifier.width(16.sdp()))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nome,
                    fontSize = 18.ssp(),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )
                Spacer(modifier = Modifier.height(4.sdp()))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.sdp())
                    )
                    Spacer(modifier = Modifier.width(4.sdp()))
                    Text(
                        text = avaliacao.toString(),
                        fontSize = 14.ssp(),
                        color = Color(0xFF6D6D6D)
                    )
                }
            }

            // Botões de ação
            Row {
                IconButton(
                    onClick = { /* Ligar */ },
                    modifier = Modifier
                        .size(44.sdp())
                        .background(Color(0xFF00B14F), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "Ligar",
                        tint = Color.White,
                        modifier = Modifier.size(20.sdp())
                    )
                }

                Spacer(modifier = Modifier.width(8.sdp()))

                IconButton(
                    onClick = { /* Chat */ },
                    modifier = Modifier
                        .size(44.sdp())
                        .background(Color(0xFF00B14F), CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Message,
                        contentDescription = "Mensagem",
                        tint = Color.White,
                        modifier = Modifier.size(20.sdp())
                    )
                }
            }
        }
    }
}

@Composable
private fun CardPercurso(origem: String, destino: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.sdp()),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.sdp())
        ) {
            Text(
                text = "Percurso",
                fontSize = 16.ssp(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.height(16.sdp()))

            // Origem
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.sdp())
                        .background(Color(0xFF00B14F), CircleShape)
                )
                Spacer(modifier = Modifier.width(12.sdp()))
                Column {
                    Text(
                        text = "Origem",
                        fontSize = 12.ssp(),
                        color = Color(0xFF6D6D6D)
                    )
                    Text(
                        text = origem,
                        fontSize = 14.ssp(),
                        color = Color(0xFF2D2D2D),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Linha conectora
            Box(
                modifier = Modifier
                    .padding(start = 5.sdp())
                    .width(2.sdp())
                    .height(24.sdp())
                    .background(Color(0xFFE0E0E0))
            )

            // Destino
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.sdp())
                        .background(Color(0xFFFF6B6B), CircleShape)
                )
                Spacer(modifier = Modifier.width(12.sdp()))
                Column {
                    Text(
                        text = "Destino",
                        fontSize = 12.ssp(),
                        color = Color(0xFF6D6D6D)
                    )
                    Text(
                        text = destino,
                        fontSize = 14.ssp(),
                        color = Color(0xFF2D2D2D),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun BotaoCancelar(onClick: () -> Unit, enabled: Boolean) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.sdp()),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFFF6B6B),
            disabledContentColor = Color.Gray
        ),
        border = BorderStroke(2.sdp(), if (enabled) Color(0xFFFF6B6B) else Color.Gray)
    ) {
        Icon(
            Icons.Default.Cancel,
            contentDescription = null,
            tint = if (enabled) Color(0xFFFF6B6B) else Color.Gray,
            modifier = Modifier.size(20.sdp())
        )
        Spacer(modifier = Modifier.width(8.sdp()))
        Text(
            text = if (enabled) "Cancelar Pedido" else "Não é possível cancelar",
            fontSize = 16.ssp(),
            fontWeight = FontWeight.Bold,
            color = if (enabled) Color(0xFFFF6B6B) else Color.Gray
        )
    }
}

@Composable
private fun DialogoCancelamento(onDismiss: () -> Unit, onConfirmar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.sdp()),
        title = {
            Text(
                text = "Cancelar pedido?",
                fontSize = 20.ssp(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )
        },
        text = {
            Column {
                Text(
                    text = "Tem certeza que deseja cancelar este pedido?",
                    fontSize = 15.ssp(),
                    color = Color(0xFF6D6D6D)
                )
                Spacer(modifier = Modifier.height(8.sdp()))
                Text(
                    text = "Esta ação não poderá ser desfeita.",
                    fontSize = 13.ssp(),
                    color = Color(0xFFFF6B6B)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B6B)
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Sim, cancelar", fontSize = 14.ssp(), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Voltar",
                    fontSize = 14.ssp(),
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    )
}

