package com.exemple.facilita.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.exemple.facilita.components.BottomNavBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Movimentacao(
    val titulo: String,
    val subtitulo: String,
    val valor: String,
    val data: String,
    val tipo: TipoMovimentacao
)

enum class TipoMovimentacao {
    DEPOSITO, SAQUE, CORRIDA
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCarteira(navController: NavController) {
    var mostrarSaldo by remember { mutableStateOf(true) }
    var mostrarDialogDepositar by remember { mutableStateOf(false) }
    var mostrarDialogSacar by remember { mutableStateOf(false) }

    val saldo = "R$ 1.250,00"
    val nomeUsuario = "Adriana"

    // Animação de entrada
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    val movimentacoes = listOf(
        Movimentacao(
            titulo = "Corrida - Centro ao Bairro Sul",
            subtitulo = "Entregador: João Silva",
            valor = "- R$ 25,50",
            data = "Hoje, 14:30",
            tipo = TipoMovimentacao.CORRIDA
        ),
        Movimentacao(
            titulo = "Depósito via PIX",
            subtitulo = "Recarga de saldo",
            valor = "+ R$ 500,00",
            data = "Hoje, 10:15",
            tipo = TipoMovimentacao.DEPOSITO
        ),
        Movimentacao(
            titulo = "Corrida - Shopping ao Centro",
            subtitulo = "Entregador: Maria Santos",
            valor = "- R$ 18,00",
            data = "Ontem, 16:45",
            tipo = TipoMovimentacao.CORRIDA
        ),
        Movimentacao(
            titulo = "Saque",
            subtitulo = "Transferência para conta bancária",
            valor = "- R$ 200,00",
            data = "18 Nov, 09:00",
            tipo = TipoMovimentacao.SAQUE
        ),
        Movimentacao(
            titulo = "Corrida - Farmácia Express",
            subtitulo = "Entregador: Pedro Lima",
            valor = "- R$ 15,00",
            data = "17 Nov, 20:30",
            tipo = TipoMovimentacao.CORRIDA
        ),
        Movimentacao(
            titulo = "Depósito via Boleto",
            subtitulo = "Recarga de saldo",
            valor = "+ R$ 300,00",
            data = "15 Nov, 11:20",
            tipo = TipoMovimentacao.DEPOSITO
        )
    )

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header com gradiente e animação
                item {
                    AnimatedHeader(
                        nomeUsuario = nomeUsuario,
                        saldo = saldo,
                        mostrarSaldo = mostrarSaldo,
                        onToggleSaldo = { mostrarSaldo = !mostrarSaldo },
                        visible = visible
                    )
                }

                // Botões de ação - apenas Depositar e Sacar
                item {
                    AnimatedActionButtons(
                        visible = visible,
                        onDepositarClick = { mostrarDialogDepositar = true },
                        onSacarClick = { mostrarDialogSacar = true }
                    )
                }

                // Título Movimentações
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF4F4F4))
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            text = "Histórico de Movimentações",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFF2D2D2D)
                        )
                    }
                }

                // Lista de movimentações com animação
                itemsIndexed(movimentacoes) { index, mov ->
                    AnimatedMovimentacaoItem(
                        mov = mov,
                        index = index,
                        visible = visible
                    )
                }

                // Espaço no final
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Diálogos
    if (mostrarDialogDepositar) {
        DialogDepositar(
            onDismiss = { mostrarDialogDepositar = false },
            onConfirm = { valor ->
                // TODO: Implementar lógica de depósito
                mostrarDialogDepositar = false
            }
        )
    }

    if (mostrarDialogSacar) {
        DialogSacar(
            onDismiss = { mostrarDialogSacar = false },
            onConfirm = { valor ->
                // TODO: Implementar lógica de saque
                mostrarDialogSacar = false
            },
            saldoDisponivel = saldo
        )
    }
}

// Componente de Header Animado
@Composable
private fun AnimatedHeader(
    nomeUsuario: String,
    saldo: String,
    mostrarSaldo: Boolean,
    onToggleSaldo: () -> Unit,
    visible: Boolean
) {
    // Animações
    val slideOffset by animateIntAsState(
        targetValue = if (visible) 0 else -100,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 800)
    )

    // Animação do card de saldo
    val cardScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF3C604B), Color(0xFF00B14F))
                )
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .alpha(alpha)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar com avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar com animação de rotação
                var rotation by remember { mutableStateOf(0f) }
                LaunchedEffect(Unit) {
                    rotation = 360f
                }
                val animatedRotation by animateFloatAsState(
                    targetValue = rotation,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    )
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .rotate(animatedRotation)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = nomeUsuario.take(2).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.rotate(-animatedRotation)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Olá,",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = nomeUsuario,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Ícone de notificação com badge animado
                var badgeVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(500)
                    badgeVisible = true
                }

                Box {
                    IconButton(onClick = { /* notificações */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificações",
                            tint = Color.White
                        )
                    }
                    // Badge animado
                    if (badgeVisible) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Card de saldo com animação
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .scale(cardScale)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column {
                            Text(
                                text = "Saldo Disponível",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            AnimatedVisibility(
                                visible = mostrarSaldo,
                                enter = fadeIn() + slideInVertically(),
                                exit = fadeOut() + slideOutVertically()
                            ) {
                                Text(
                                    text = saldo,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }
                            AnimatedVisibility(
                                visible = !mostrarSaldo,
                                enter = fadeIn() + slideInVertically(),
                                exit = fadeOut() + slideOutVertically()
                            ) {
                                Text(
                                    text = "R$ ••••••",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }
                        }

                        // Botão de toggle com rotação
                        var iconRotation by remember { mutableStateOf(0f) }
                        val animatedIconRotation by animateFloatAsState(
                            targetValue = iconRotation,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        )

                        IconButton(
                            onClick = {
                                iconRotation += 180f
                                onToggleSaldo()
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (mostrarSaldo)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = "Mostrar/ocultar",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(animatedIconRotation)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Texto informativo com animação
            var textVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(600)
                textVisible = true
            }

            if (textVisible) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "💳 Use seu saldo para pagar corridas",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// Componente de Botões de Ação Animados
@Composable
private fun AnimatedActionButtons(
    visible: Boolean,
    onDepositarClick: () -> Unit,
    onSacarClick: () -> Unit
) {
    var buttonsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(visible) {
        if (visible) {
            delay(400)
            buttonsVisible = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF4F4F4))
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botão Depositar
            AnimatedVisibility(
                visible = buttonsVisible,
                enter = fadeIn(animationSpec = tween(600)) +
                        slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        ),
                modifier = Modifier.weight(1f)
            ) {
                ActionButton(
                    text = "Depositar",
                    icon = Icons.Default.Add,
                    backgroundColor = Color(0xFF00B14F),
                    onClick = onDepositarClick
                )
            }

            // Botão Sacar
            AnimatedVisibility(
                visible = buttonsVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 150)) +
                        slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        ),
                modifier = Modifier.weight(1f)
            ) {
                ActionButton(
                    text = "Sacar",
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    backgroundColor = Color(0xFF3C604B),
                    onClick = onSacarClick
                )
            }
        }
    }
}

// Botão de Ação Individual
@Composable
private fun ActionButton(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    // Animação de pulse no ícone
    val infiniteTransition = rememberInfiniteTransition()
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (pressed) 2.dp else 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed = false
                        }
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(iconScale)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
        }
    }
}

// Item de Movimentação Animado
@Composable
private fun AnimatedMovimentacaoItem(
    mov: Movimentacao,
    index: Int,
    visible: Boolean
) {
    var itemVisible by remember { mutableStateOf(false) }
    LaunchedEffect(visible) {
        if (visible) {
            delay((index * 100).toLong())
            itemVisible = true
        }
    }

    AnimatedVisibility(
        visible = itemVisible,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
    ) {
        MovimentacaoItem(mov)
    }
}

@Composable
private fun MovimentacaoItem(mov: Movimentacao) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF4F4F4))
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            pressed = true
                            try {
                                awaitRelease()
                            } finally {
                                pressed = false
                            }
                        },
                        onTap = { /* detalhes */ }
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícone da movimentação com animação
                val iconColor = when (mov.tipo) {
                    TipoMovimentacao.DEPOSITO -> Color(0xFF00B14F)
                    TipoMovimentacao.SAQUE -> Color(0xFFFF6B6B)
                    TipoMovimentacao.CORRIDA -> Color(0xFF3C604B)
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (mov.tipo) {
                            TipoMovimentacao.DEPOSITO -> Icons.Default.Add
                            TipoMovimentacao.SAQUE -> Icons.AutoMirrored.Filled.TrendingDown
                            TipoMovimentacao.CORRIDA -> Icons.Default.DirectionsCar
                        },
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mov.titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF2D2D2D),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = mov.subtitulo,
                        fontSize = 11.sp,
                        color = Color(0xFF6D6D6D),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = mov.valor,
                        color = if (mov.valor.startsWith("+")) Color(0xFF00B14F) else Color(0xFFFF6B6B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = mov.data,
                        fontSize = 11.sp,
                        color = Color(0xFF6D6D6D)
                    )
                }
            }
        }
    }
}

// Diálogo de Depositar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogDepositar(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var valor by remember { mutableStateOf("") }
    var showAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showAnimation = true
    }

    val scale by animateFloatAsState(
        targetValue = if (showAnimation) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.scale(scale)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ícone animado
                val infiniteTransition = rememberInfiniteTransition()
                val iconScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .scale(iconScale)
                        .clip(CircleShape)
                        .background(Color(0xFF00B14F).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF00B14F),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Depositar Saldo",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Digite o valor que deseja adicionar à sua carteira",
                    fontSize = 13.sp,
                    color = Color(0xFF6D6D6D),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = valor,
                    onValueChange = {
                        // Aceita apenas números e ponto/vírgula
                        if (it.isEmpty() || it.matches(Regex("^\\d*[.,]?\\d{0,2}\$"))) {
                            valor = it
                        }
                    },
                    label = { Text("Valor") },
                    placeholder = { Text("R$ 0,00") },
                    leadingIcon = {
                        Text(
                            text = "R$",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00B14F)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00B14F),
                        focusedLabelColor = Color(0xFF00B14F)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF6D6D6D)
                        )
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (valor.isNotEmpty()) {
                                onConfirm(valor)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = valor.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00B14F)
                        )
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

// Diálogo de Sacar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogSacar(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    saldoDisponivel: String
) {
    var valor by remember { mutableStateOf("") }
    var showAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showAnimation = true
    }

    val scale by animateFloatAsState(
        targetValue = if (showAnimation) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.scale(scale)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ícone animado
                val infiniteTransition = rememberInfiniteTransition()
                val iconRotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 10f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(400, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .rotate(iconRotation)
                        .clip(CircleShape)
                        .background(Color(0xFF3C604B).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = Color(0xFF3C604B),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sacar Saldo",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Saldo disponível: $saldoDisponivel",
                    fontSize = 14.sp,
                    color = Color(0xFF00B14F),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "O valor será transferido para sua conta bancária",
                    fontSize = 12.sp,
                    color = Color(0xFF6D6D6D),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = valor,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*[.,]?\\d{0,2}\$"))) {
                            valor = it
                        }
                    },
                    label = { Text("Valor") },
                    placeholder = { Text("R$ 0,00") },
                    leadingIcon = {
                        Text(
                            text = "R$",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3C604B)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3C604B),
                        focusedLabelColor = Color(0xFF3C604B)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF6D6D6D)
                        )
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (valor.isNotEmpty()) {
                                onConfirm(valor)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = valor.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3C604B)
                        )
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaCarteiraPreview() {
    val navController = rememberNavController()
    TelaCarteira(navController)
}

