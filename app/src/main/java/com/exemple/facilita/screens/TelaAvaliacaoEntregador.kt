package com.exemple.facilita.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun TelaAvaliacaoCliente(
    navController: NavController,
    servicoId: Int = 0,
    clienteNome: String = "Cliente",
    valorServico: String = "0.00"
) {
    var showSuccessAnimation by remember { mutableStateOf(true) }
    var showRatingScreen by remember { mutableStateOf(false) }

    // Modo Claro - Cores suaves
    val primaryGreen = Color(0xFF2E7D32)
    val darkGreen = Color(0xFF1B5E20)
    val accentCyan = Color(0xFF0097A7)
    val lightBg = Color(0xFFF5F5F5)
    val cardBg = Color.White
    val textPrimary = Color(0xFF212121)
    val textSecondary = Color(0xFF757575)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFAFAFA),
                        lightBg,
                        Color(0xFFEEEEEE)
                    )
                )
            )
    ) {
        // Animação de sucesso aparece primeiro
        if (showSuccessAnimation) {
            ServicoFinalizadoAnimation(
                onAnimationComplete = {
                    showSuccessAnimation = false
                    showRatingScreen = true
                }
            )
        }

        // Tela de avaliação aparece depois
        if (showRatingScreen) {
            AvaliacaoClienteContent(
                navController = navController,
                servicoId = servicoId,
                clienteNome = clienteNome,
                valorServico = valorServico
            )
        }
    }
}

@Composable
fun ServicoFinalizadoAnimation(
    onAnimationComplete: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var showCheckmark by remember { mutableStateOf(false) }
    var showParticles by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }

    val primaryGreen = Color(0xFF2E7D32)
    val accentCyan = Color(0xFF0097A7)

    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Controle da sequência de animação
    LaunchedEffect(Unit) {
        // Fase 1: Círculo cresce (0-30%)
        animate(0f, 0.3f, animationSpec = tween(500)) { value, _ ->
            progress = value
        }

        // Fase 2: Círculo completa (30-100%)
        animate(0.3f, 1f, animationSpec = tween(800, easing = FastOutSlowInEasing)) { value, _ ->
            progress = value
        }

        delay(200)
        showCheckmark = true

        delay(300)
        showParticles = true

        delay(200)
        showText = true

        // Aguarda mais um pouco antes de transitar
        delay(1500)
        onAnimationComplete()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Brilho de fundo
        Box(
            modifier = Modifier
                .size(400.dp)
                .alpha(glowAlpha * progress)
                .background(
                    primaryGreen.copy(alpha = 0.2f),
                    CircleShape
                )
                .blur(50.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Círculo de progresso com checkmark
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Círculo de progresso
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val sweepAngle = 360f * progress

                    // Círculo de fundo
                    drawCircle(
                        color = Color.Gray.copy(alpha = 0.2f),
                        style = Stroke(width = 12.dp.toPx())
                    )

                    // Círculo de progresso
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                primaryGreen,
                                accentCyan,
                                primaryGreen
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx())
                    )
                }

                // Checkmark animado
                if (showCheckmark) {
                    val checkScale by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "check"
                    )

                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .scale(checkScale),
                        tint = primaryGreen
                    )
                }

                // Partículas ao redor
                if (showParticles) {
                    repeat(12) { index ->
                        val angle = (index * 30f) * (Math.PI / 180f)
                        val distance = 120.dp

                        val particleAlpha by animateFloatAsState(
                            targetValue = 0f,
                            animationSpec = tween(1000),
                            label = "particle$index"
                        )

                        val particleScale by animateFloatAsState(
                            targetValue = 1.5f,
                            animationSpec = tween(1000),
                            label = "particleScale$index"
                        )

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = (distance.value * cos(angle)).dp,
                                    y = (distance.value * sin(angle)).dp
                                )
                                .size(8.dp)
                                .scale(particleScale)
                                .alpha(1f - particleAlpha)
                                .background(
                                    if (index % 2 == 0) primaryGreen else accentCyan,
                                    CircleShape
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Texto animado
            if (showText) {
                val textAlpha by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(500),
                    label = "text"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.alpha(textAlpha)
                ) {
                    Text(
                        "Serviço Finalizado!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Parabéns!",
                        fontSize = 20.sp,
                        color = primaryGreen,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun AvaliacaoClienteContent(
    navController: NavController,
    servicoId: Int,
    clienteNome: String,
    valorServico: String
) {
    var rating by remember { mutableFloatStateOf(0f) }
    var selectedQualities by remember { mutableStateOf(setOf<String>()) }
    var comentario by remember { mutableStateOf("") }
    var showThankYou by remember { mutableStateOf(false) }

    // Modo Claro
    val primaryGreen = Color(0xFF2E7D32)
    val darkGreen = Color(0xFF1B5E20)
    val accentCyan = Color(0xFF0097A7)
    val lightBg = Color(0xFFF5F5F5)
    val cardBg = Color.White
    val textPrimary = Color(0xFF212121)
    val textSecondary = Color(0xFF757575)

    if (showThankYou) {
        ThankYouDialog(
            onDismiss = {
                navController.navigate("tela_home") {
                    popUpTo("tela_home") { inclusive = true }
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Efeitos decorativos
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .background(
                    primaryGreen.copy(alpha = 0.1f),
                    CircleShape
                )
                .blur(60.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                    onClick = {
                        navController.navigate("tela_home") {
                            popUpTo("tela_home") { inclusive = true }
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = textPrimary
                    )
                }

                Text(
                    "Avalie o Cliente",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )

                // Espaço para simetria
                Box(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Avatar e nome do cliente
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBg
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(primaryGreen, darkGreen)
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            clienteNome.firstOrNull()?.uppercase() ?: "C",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        clienteNome,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = primaryGreen.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = primaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "R$ $valorServico",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sistema de avaliação por estrelas
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBg
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Como foi sua experiência?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Estrelas animadas
                    AnimatedStarRating(
                        rating = rating,
                        onRatingChange = { rating = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Texto da avaliação
                    Text(
                        when {
                            rating == 0f -> "Toque para avaliar"
                            rating <= 1f -> "Muito Insatisfeito"
                            rating <= 2f -> "Insatisfeito"
                            rating <= 3f -> "Regular"
                            rating <= 4f -> "Satisfeito"
                            else -> "Excelente!"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = when {
                            rating == 0f -> textSecondary
                            rating <= 2f -> Color(0xFFFF5252)
                            rating <= 3f -> Color(0xFFFFAB00)
                            else -> primaryGreen
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Qualidades do cliente (tags)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBg
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        "Qualidades do Cliente",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    QualityTagsGrid(
                        selectedQualities = selectedQualities,
                        onQualityToggle = { quality ->
                            selectedQualities = if (selectedQualities.contains(quality)) {
                                selectedQualities - quality
                            } else {
                                selectedQualities + quality
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de comentário
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBg
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        "Comentário (Opcional)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { if (it.length <= 500) comentario = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = {
                            Text(
                                "Descreva sua experiência com este cliente...",
                                color = textSecondary,
                                fontSize = 14.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary,
                            focusedBorderColor = primaryGreen,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            cursorColor = primaryGreen,
                            focusedContainerColor = Color(0xFFFAFAFA),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        ),
                        maxLines = 5,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "${comentario.length}/500",
                        fontSize = 12.sp,
                        color = textSecondary,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão de enviar
            Button(
                onClick = {
                    // TODO: Enviar avaliação para API
                    showThankYou = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = rating > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryGreen,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Enviar Avaliação",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AnimatedStarRating(
    rating: Float,
    onRatingChange: (Float) -> Unit
) {
    val primaryGreen = Color(0xFF2E7D32)
    val accentGold = Color(0xFFFFD700)

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            val starRating = index + 1
            val isSelected = starRating <= rating

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.2f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "star$index"
            )

            Icon(
                if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Star $starRating",
                modifier = Modifier
                    .size(48.dp)
                    .scale(scale)
                    .clickable { onRatingChange(starRating.toFloat()) },
                tint = if (isSelected) accentGold else Color(0xFFE0E0E0)
            )
        }
    }
}

@Composable
fun QualityTagsGrid(
    selectedQualities: Set<String>,
    onQualityToggle: (String) -> Unit
) {
    val qualities = listOf(
        "Educado" to Icons.Default.EmojiEmotions,
        "Pontual" to Icons.Default.Schedule,
        "Organizado" to Icons.Default.CheckCircle,
        "Comunicativo" to Icons.Default.Chat,
        "Respeitoso" to Icons.Default.Favorite,
        "Prestativo" to Icons.Default.ThumbUp,
        "Paciente" to Icons.Default.Psychology,
        "Confiável" to Icons.Default.VerifiedUser
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        qualities.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { (quality, icon) ->
                    QualityTag(
                        quality = quality,
                        icon = icon,
                        isSelected = selectedQualities.contains(quality),
                        onToggle = { onQualityToggle(quality) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun QualityTag(
    quality: String,
    icon: ImageVector,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Modo Claro
    val primaryGreen = Color(0xFF2E7D32)
    val cardBg = Color.White
    val textSecondary = Color(0xFF757575)

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "quality"
    )

    Card(
        onClick = onToggle,
        modifier = modifier.scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                primaryGreen.copy(alpha = 0.15f)
            else
                Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) primaryGreen else textSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                quality,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) primaryGreen else textSecondary
            )
        }
    }
}

@Composable
fun ThankYouDialog(onDismiss: () -> Unit) {
    val primaryGreen = Color(0xFF2E7D32)

    var particlesVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        particlesVisible = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Partículas de confete
                if (particlesVisible) {
                    repeat(20) { index ->
                        val delay = index * 50
                        ConfettiParticle(delay = delay)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Ícone de sucesso
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = primaryGreen
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Obrigado!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Sua avaliação foi enviada com sucesso!",
                        fontSize = 16.sp,
                        color = Color(0xFF757575),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryGreen
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Continuar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BoxScope.ConfettiParticle(delay: Int) {
    val primaryGreen = Color(0xFF2E7D32)
    val accentCyan = Color(0xFF0097A7)
    val accentGold = Color(0xFFFFD700)

    val colors = listOf(primaryGreen, accentCyan, accentGold)
    val color = colors.random()

    var offsetY by remember { mutableFloatStateOf(-50f) }
    var alpha by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        animate(
            initialValue = -50f,
            targetValue = 300f,
            animationSpec = tween(2000, easing = LinearEasing)
        ) { value, _ ->
            offsetY = value
            alpha = 1f - (value / 300f)
        }
    }

    Box(
        modifier = Modifier
            .offset(
                x = ((-100..100).random()).dp,
                y = offsetY.dp
            )
            .size(8.dp)
            .alpha(alpha)
            .background(color, CircleShape)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaAvaliacaoClientePreview() {
    val navController = rememberNavController()
    TelaAvaliacaoCliente(
        navController = navController,
        servicoId = 1,
        clienteNome = "João Silva",
        valorServico = "150.00"
    )
}
