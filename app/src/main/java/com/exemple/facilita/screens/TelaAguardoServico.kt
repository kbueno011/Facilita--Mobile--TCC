package com.exemple.facilita.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exemple.facilita.utils.TokenManager
import com.exemple.facilita.viewmodel.ServicoViewModel
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAguardoServico(
    navController: NavController,
    servicoId: String,
    origemEndereco: String? = null,
    destinoEndereco: String? = null
) {
    val context = LocalContext.current
    val viewModel: ServicoViewModel = viewModel()

    val servico by viewModel.servico.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val token = TokenManager.obterToken(context) ?: ""

    var mostrarDialogoCancelar by remember { mutableStateOf(false) }
    var tempoEstimado by remember { mutableStateOf(0) }

    // Inicia o monitoramento do serviço
    LaunchedEffect(servicoId) {
        if (token.isNotEmpty()) {
            viewModel.iniciarMonitoramento(token, servicoId)
            Log.d("TelaAguardo", "✅ Monitoramento iniciado para serviço #$servicoId")
        }
    }

    // Atualiza tempo estimado
    LaunchedEffect(servico) {
        tempoEstimado = viewModel.calcularTempoEstimado()
    }

    // Navega para tela de rastreamento quando aceito
    LaunchedEffect(servico?.status) {
        when (servico?.status) {
            "ACEITO" -> {
                Log.d("TelaAguardo", "✅ Prestador aceitou o serviço! Navegando para rastreamento...")
                delay(1500) // Aguarda 1.5s para o usuário ver a confirmação
                navController.navigate("tela_rastreamento_servico/$servicoId") {
                    popUpTo("tela_aguardo_servico/$servicoId") { inclusive = true }
                }
            }
            "EM_ANDAMENTO" -> {
                Log.d("TelaAguardo", "🚀 Serviço em andamento! Navegando para rastreamento...")
                delay(500)
                navController.navigate("tela_rastreamento_servico/$servicoId") {
                    popUpTo("tela_aguardo_servico/$servicoId") { inclusive = true }
                }
            }
            "CONCLUIDO" -> {
                Toast.makeText(context, "✅ Serviço concluído!", Toast.LENGTH_SHORT).show()
                navController.navigate("tela_home") {
                    popUpTo("tela_home") { inclusive = true }
                }
            }
            "CANCELADO" -> {
                Toast.makeText(context, "❌ Serviço cancelado", Toast.LENGTH_SHORT).show()
                navController.navigate("tela_home") {
                    popUpTo("tela_home") { inclusive = true }
                }
            }
        }
    }

    // Determina o status visual
    val statusVisual = when (servico?.status) {
        "AGUARDANDO" -> "Procurando prestador..."
        "ACEITO" -> "Prestador encontrado!"
        "EM_ANDAMENTO" -> "Iniciando serviço..."
        else -> "Aguardando..."
    }

    val prestadorNome = servico?.prestador?.usuario?.nome ?: servico?.prestador?.nome ?: ""
    val prestadorAvaliacao = servico?.prestador?.avaliacao ?: 0f
    val categoria = servico?.categoria?.nome ?: ""
    val mostrarPrestador = servico?.status == "ACEITO" || servico?.status == "EM_ANDAMENTO"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF00B14F).copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Voltar",
                        tint = Color(0xFF00B14F),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Serviço #$servicoId",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )

                IconButton(
                    onClick = { /* Ajuda */ },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF00B14F).copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Ajuda",
                        tint = Color(0xFF00B14F),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Animação central
            AnimacaoLoadingFuturista(servico?.status ?: "AGUARDANDO")

            Spacer(modifier = Modifier.height(32.dp))

            // Texto do status
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = statusVisual,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2D2D2D),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (tempoEstimado > 0 && mostrarPrestador) {
                    Text(
                        text = "Chegará em aproximadamente $tempoEstimado min",
                        fontSize = 15.sp,
                        color = Color(0xFF6D6D6D),
                        textAlign = TextAlign.Center
                    )
                } else if (!mostrarPrestador) {
                    Text(
                        text = "Isso pode levar alguns segundos",
                        fontSize = 15.sp,
                        color = Color(0xFF6D6D6D),
                        textAlign = TextAlign.Center
                    )
                }

                if (categoria.isNotEmpty()) {
                    Text(
                        text = "Categoria: $categoria",
                        fontSize = 13.sp,
                        color = Color(0xFF00B14F),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Card do prestador (quando encontrado)
            if (mostrarPrestador && prestadorNome.isNotEmpty()) {
                CardPrestador(
                    nome = prestadorNome,
                    avaliacao = prestadorAvaliacao
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Informações do percurso
            if (origemEndereco != null && destinoEndereco != null) {
                CardPercurso(origemEndereco, destinoEndereco)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botão cancelar
            OutlinedButton(
                onClick = { mostrarDialogoCancelar = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFF6B6B)
                )
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Cancelar Serviço",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Loading overlay
        if (isLoading && servico == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF00B14F))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Carregando...",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Diálogo de cancelamento
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
}

@Composable
private fun AnimacaoLoadingFuturista(status: String) {
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
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Anel externo girando
        Canvas(
            modifier = Modifier
                .size(240.dp)
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
                .size(180.dp)
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
                .size(140.dp)
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
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF00B14F), Color(0xFF3C604B))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (status) {
                    "AGUARDANDO" -> Icons.Default.Search
                    "ACEITO" -> Icons.Default.CheckCircle
                    "EM_ANDAMENTO" -> Icons.Default.DirectionsCar
                    "CONCLUIDO" -> Icons.Default.Done
                    else -> Icons.Default.HourglassEmpty
                },
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }

        // Ondas expandindo (quando encontrado)
        if (status == "ACEITO" || status == "EM_ANDAMENTO") {
            repeat(3) { index ->
                val delay = index * 700
                var scale by remember { mutableStateOf(0f) }
                var alphaOnda by remember { mutableStateOf(1f) }

                LaunchedEffect(Unit) {
                    while (true) {
                        scale = 0f
                        alphaOnda = 1f
                        animate(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = tween(2000, easing = LinearEasing)
                        ) { value, _ ->
                            scale = value
                            alphaOnda = 1f - value
                        }
                        delay(delay.toLong())
                    }
                }

                Canvas(modifier = Modifier.size(240.dp).scale(1f + scale * 0.5f)) {
                    drawCircle(
                        color = Color(0xFF06C755).copy(alpha = alphaOnda * 0.5f),
                        radius = size.minDimension / 2,
                        style = Stroke(width = 4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CardPrestador(nome: String, avaliacao: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
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
                    .size(64.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF3C604B), Color(0xFF00B14F))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nome,
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
                        text = avaliacao.toString(),
                        fontSize = 14.sp,
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
                        Icons.Default.Message,
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
private fun CardPercurso(origem: String, destino: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Percurso",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D2D2D)
                    )
                }
            }

            // Linha conectora
            Box(
                modifier = Modifier
                    .padding(start = 5.dp, top = 4.dp, bottom = 4.dp)
                    .width(2.dp)
                    .height(30.dp)
                    .background(Color(0xFFE0E0E0))
            )

            // Destino
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(12.dp)
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
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D2D2D)
                    )
                }
            }
        }
    }
}

