package com.exemple.facilita.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.exemple.facilita.model.PedidoApi
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalhesPedido(navController: NavController, pedidoJson: String?) {
    val pedido = remember {
        pedidoJson?.let { Gson().fromJson(it, PedidoApi::class.java) }
    }

    var animateContent by remember { mutableStateOf(false) }

    // Cores modo claro
    val primaryGreen = Color(0xFF019D31)
    val lightGreen = Color(0xFF06C755)
    val lightBg = Color(0xFFF8F9FA)
    val cardBg = Color.White
    val textPrimary = Color(0xFF212121)
    val textSecondary = Color(0xFF666666)

    // Animação de entrada
    LaunchedEffect(Unit) {
        delay(100)
        animateContent = true
    }

    val statusColor = when (pedido?.status) {
        "CONCLUIDO", "FINALIZADO" -> Color(0xFF4CAF50)
        "CANCELADO" -> Color(0xFFF44336)
        "EM_ANDAMENTO" -> primaryGreen
        "PENDENTE" -> Color(0xFFFFA726)
        else -> textSecondary
    }

    val statusText = when (pedido?.status) {
        "CONCLUIDO", "FINALIZADO" -> "Finalizado"
        "CANCELADO" -> "Cancelado"
        "EM_ANDAMENTO" -> "Em Andamento"
        "PENDENTE" -> "Pendente"
        else -> pedido?.status ?: "Desconhecido"
    }

    Scaffold(
        containerColor = lightBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalhes do Pedido",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Voltar",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryGreen,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (pedido == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        "Erro ao carregar pedido",
                        color = textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryGreen
                        )
                    ) {
                        Text("Voltar")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(
                    visible = animateContent,
                    enter = slideInVertically(
                        initialOffsetY = { -50 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) + fadeIn()
                ) {
                    // Card de Status Principal
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                spotColor = statusColor.copy(alpha = 0.25f)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Box {
                            // Barra lateral colorida
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .fillMaxHeight()
                                    .align(Alignment.CenterStart)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(statusColor, statusColor.copy(alpha = 0.7f))
                                        ),
                                        shape = RoundedCornerShape(
                                            topStart = 24.dp,
                                            bottomStart = 24.dp
                                        )
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 24.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "Pedido #${pedido.id}",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                        Text(
                                            formatarDataPedido(pedido.data_solicitacao),
                                            fontSize = 13.sp,
                                            color = textSecondary,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }

                                    // Badge de Status
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    listOf(statusColor, statusColor.copy(alpha = 0.8f))
                                                ),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .padding(horizontal = 16.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            statusText,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Valor do Serviço em Destaque
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                listOf(primaryGreen, lightGreen)
                                            ),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "Valor do Serviço",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "R$ ${String.format(Locale.getDefault(), "%.2f", pedido.valor)}",
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }



                // Card do Prestador (se houver)
                pedido.prestador?.let { prestador ->
                    AnimatedVisibility(
                        visible = animateContent,
                        enter = slideInVertically(
                            initialOffsetY = { 50 },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        ) + fadeIn()
                    ) {
                        InfoCardPedido(
                            title = "Informações do Prestador",
                            icon = Icons.Default.WorkOutline,
                            iconColor = primaryGreen,
                            cardBg = cardBg,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        ) {
                            prestador.usuario?.let { usuario ->
                                usuario.nome?.let { nome ->
                                    InfoRowPedido(
                                        label = "Nome",
                                        value = nome,
                                        icon = Icons.Default.Person,
                                        iconColor = primaryGreen,
                                        textPrimary = textPrimary,
                                        textSecondary = textSecondary
                                    )
                                }
                                usuario.email?.let { email ->
                                    InfoRowPedido(
                                        label = "Email",
                                        value = email,
                                        icon = Icons.Default.Email,
                                        iconColor = primaryGreen,
                                        textPrimary = textPrimary,
                                        textSecondary = textSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Card do Serviço
                AnimatedVisibility(
                    visible = animateContent,
                    enter = slideInVertically(
                        initialOffsetY = { 50 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) + fadeIn()
                ) {
                    InfoCardPedido(
                        title = "Detalhes do Serviço",
                        icon = Icons.Default.Work,
                        iconColor = primaryGreen,
                        cardBg = cardBg,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    ) {
                        pedido.categoria?.let { categoria ->
                            InfoRowPedido(
                                label = "Categoria",
                                value = categoria.nome,
                                icon = Icons.Default.Category,
                                iconColor = primaryGreen,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary
                            )
                        }
                        InfoRowPedido(
                            label = "Descrição",
                            value = pedido.descricao,
                            icon = Icons.Default.Description,
                            iconColor = primaryGreen,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                }

                // Card de Localização
                pedido.localizacao?.let { loc ->
                    AnimatedVisibility(
                        visible = animateContent,
                        enter = slideInVertically(
                            initialOffsetY = { 50 },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        ) + fadeIn()
                    ) {
                        InfoCardPedido(
                            title = "Localização",
                            icon = Icons.Default.LocationOn,
                            iconColor = primaryGreen,
                            cardBg = cardBg,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        ) {
                            loc.cidade?.let { cidade ->
                                InfoRowPedido(
                                    label = "Cidade",
                                    value = cidade,
                                    icon = Icons.Default.Place,
                                    iconColor = primaryGreen,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun InfoCardPedido(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = iconColor.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            iconColor.copy(alpha = 0.15f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            }

            content()
        }
    }
}

@Composable
fun InfoRowPedido(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor.copy(alpha = 0.7f),
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                fontSize = 12.sp,
                color = textSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                fontSize = 15.sp,
                color = textPrimary,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp
            )
        }
    }
}

private fun formatarDataPedido(dataISO: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'às' HH:mm", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dataISO)
        date?.let { outputFormat.format(it) } ?: dataISO
    } catch (e: Exception) {
        // Se falhar, tenta formato mais simples
        try {
            val inputFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat2.parse(dataISO)
            date?.let { outputFormat.format(it) } ?: dataISO
        } catch (e2: Exception) {
            dataISO
        }
    }
}
