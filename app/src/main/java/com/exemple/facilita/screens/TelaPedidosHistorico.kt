package com.exemple.facilita.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.exemple.facilita.components.BottomNavBar
import com.exemple.facilita.model.PedidoApi
import com.exemple.facilita.service.RetrofitFactory
import com.exemple.facilita.utils.TokenManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPedidosHistorico(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pedidos by remember { mutableStateOf<List<PedidoApi>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedPedido by remember { mutableStateOf<PedidoApi?>(null) }

    val service = RetrofitFactory.userService

    // Carregar pedidos da API ao iniciar
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val token = TokenManager.obterToken(context)
                if (token == null) {
                    errorMessage = "Token não encontrado. Faça login novamente."
                    isLoading = false
                    return@launch
                }

                Log.d("PEDIDOS_API", "Buscando histórico de pedidos...")
                val response = service.buscarHistoricoPedidos("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    pedidos = response.body()!!.data.pedidos
                    Log.d("PEDIDOS_API", "Pedidos carregados: ${pedidos.size}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = "Erro ao carregar pedidos: ${response.code()}"
                    Log.e("PEDIDOS_API", "Erro: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                errorMessage = "Erro: ${e.message}"
                Log.e("PEDIDOS_API", "Exceção ao buscar pedidos", e)
            } finally {
                isLoading = false
            }
        }
    }

    // Agrupar pedidos por data
    val pedidosPorData = pedidos.groupBy {
        formatarDataGrupo(it.data_solicitacao)
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF019D31), Color(0xFF06C755))
                        )
                    )
                    .height(64.dp)
            ) {
                Text(
                    text = "Histórico de Pedidos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .clickable { navController.popBackStack() }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF8F9FA), Color(0xFFE9ECEF))
                    )
                )
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF019D31),
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Carregando pedidos...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Erro",
                            tint = Color.Red,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                isLoading = true
                                errorMessage = null
                                scope.launch {
                                    try {
                                        val token = TokenManager.obterToken(context)
                                        if (token != null) {
                                            val response = service.buscarHistoricoPedidos("Bearer $token")
                                            if (response.isSuccessful && response.body() != null) {
                                                pedidos = response.body()!!.data.pedidos
                                            }
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Erro: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(),
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(50.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFF019D31), Color(0xFF06C755))
                                        ),
                                        shape = RoundedCornerShape(25.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Tentar novamente",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                pedidos.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Sem pedidos",
                            tint = Color.Gray,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhum pedido encontrado",
                            fontSize = 18.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Seus pedidos aparecerão aqui",
                            fontSize = 14.sp,
                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pedidosPorData.forEach { (data, pedidosData) ->
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Data",
                                        tint = Color(0xFF019D31),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = data,
                                        fontSize = 14.sp,
                                        color = Color(0xFF019D31),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            itemsIndexed(pedidosData) { index, pedido ->
                                PedidoCardModerno(
                                    pedido = pedido,
                                    index = index,
                                    onClick = { selectedPedido = pedido }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Modal de detalhes
        selectedPedido?.let { pedido ->
            PedidoDetalhesModal(
                pedido = pedido,
                onDismiss = { selectedPedido = null }
            )
        }
    }
}

@Composable
fun PedidoCardModerno(pedido: PedidoApi, index: Int, onClick: () -> Unit) {
    // Animação de entrada
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 50L)
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400),
        label = "alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .scale(scale)
            .clickable(onClick = onClick)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF019D31).copy(alpha = 0.25f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box {
            // Barra colorida lateral
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(120.dp)
                    .align(Alignment.CenterStart)
                    .background(
                        brush = when (pedido.status) {
                            "FINALIZADO", "CONCLUIDO" -> Brush.verticalGradient(
                                listOf(Color(0xFF019D31), Color(0xFF06C755))
                            )
                            "CANCELADO" -> Brush.verticalGradient(
                                listOf(Color(0xFFD32F2F), Color(0xFFEF5350))
                            )
                            "EM_ANDAMENTO" -> Brush.verticalGradient(
                                listOf(Color(0xFFFFA726), Color(0xFFFFB74D))
                            )
                            else -> Brush.verticalGradient(
                                listOf(Color(0xFF42A5F5), Color(0xFF64B5F6))
                            )
                        },
                        shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
            ) {
                // Header com código e status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Código",
                            tint = Color(0xFF019D31),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = String.format(Locale.getDefault(), "RVJ9G%02d", pedido.id % 100),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1B1B)
                        )
                    }

                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (pedido.status) {
                            "FINALIZADO", "CONCLUIDO" -> Color(0xFF019D31).copy(alpha = 0.15f)
                            "CANCELADO" -> Color(0xFFD32F2F).copy(alpha = 0.15f)
                            "EM_ANDAMENTO" -> Color(0xFFFFA726).copy(alpha = 0.15f)
                            else -> Color(0xFF42A5F5).copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = when (pedido.status) {
                                "EM_ANDAMENTO" -> "Em andamento"
                                "FINALIZADO", "CONCLUIDO" -> "Finalizado"
                                "CANCELADO" -> "Cancelado"
                                "PENDENTE" -> "Pendente"
                                else -> pedido.status
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            color = when (pedido.status) {
                                "FINALIZADO", "CONCLUIDO" -> Color(0xFF019D31)
                                "CANCELADO" -> Color(0xFFD32F2F)
                                "EM_ANDAMENTO" -> Color(0xFFFFA726)
                                else -> Color(0xFF42A5F5)
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Informações do prestador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Foto do prestador com borda gradiente
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    listOf(Color(0xFF019D31), Color(0xFF06C755))
                                ),
                                shape = CircleShape
                            )
                            .padding(3.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = if (pedido.prestador != null) {
                                    "https://i.pravatar.cc/150?u=${pedido.prestador.usuario?.email ?: "default"}"
                                } else {
                                    "https://i.pravatar.cc/150?img=1"
                                }
                            ),
                            contentDescription = "Foto do prestador",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pedido.categoria?.nome ?: "Serviço",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pedido.prestador?.usuario?.nome ?: "Aguardando prestador",
                            fontSize = 16.sp,
                            color = Color(0xFF1B1B1B),
                            fontWeight = FontWeight.SemiBold
                        )
                        if (pedido.prestador != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Avaliação",
                                    tint = Color(0xFFFFA726),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "4.7",
                                    fontSize = 12.sp,
                                    color = Color(0xFF888888),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Valor discreto
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "R$ %.2f".format(Locale.getDefault(), pedido.valor),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1B1B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer com ícone de toque
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Toque para ver detalhes",
                        fontSize = 11.sp,
                        color = Color(0xFF019D31),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Ver detalhes",
                        tint = Color(0xFF019D31),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PedidoDetalhesModal(pedido: PedidoApi, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 650.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    // Header com gradiente e foto
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF019D31), Color(0xFF06C755))
                                ),
                                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                            )
                    ) {
                        // Botão fechar flutuante
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Conteúdo do header
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Foto grande do prestador
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .border(
                                        width = 3.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .padding(3.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = if (pedido.prestador != null) {
                                            "https://i.pravatar.cc/150?u=${pedido.prestador.usuario?.email ?: "default"}"
                                        } else {
                                            "https://i.pravatar.cc/150?img=1"
                                        }
                                    ),
                                    contentDescription = "Foto do prestador",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = String.format(Locale.getDefault(), "Pedido #RVJ9G%02d", pedido.id % 100),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Badge de status
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = when (pedido.status) {
                                        "EM_ANDAMENTO" -> "Em andamento"
                                        "FINALIZADO", "CONCLUIDO" -> "✓ Finalizado"
                                        "CANCELADO" -> "✗ Cancelado"
                                        "PENDENTE" -> "⏳ Pendente"
                                        else -> pedido.status
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    // Conteúdo branco
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                            )
                            .padding(24.dp)
                    ) {
                        // Cards de informação
                        InfoCard(
                            titulo = "Prestador",
                            valor = pedido.prestador?.usuario?.nome ?: "Aguardando",
                            icone = Icons.Default.Person,
                            corIcone = Color(0xFF019D31)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        InfoCard(
                            titulo = "Categoria",
                            valor = pedido.categoria?.nome ?: "Não especificado",
                            icone = Icons.Default.ShoppingCart,
                            corIcone = Color(0xFF42A5F5)
                        )

                        if (pedido.prestador?.usuario?.email != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            InfoCard(
                                titulo = "Email",
                                valor = pedido.prestador.usuario.email,
                                icone = Icons.Default.Email,
                                corIcone = Color(0xFFFFA726)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        InfoCard(
                            titulo = "Data do pedido",
                            valor = formatarDataDetalhada(pedido.data_solicitacao),
                            icone = Icons.Default.DateRange,
                            corIcone = Color(0xFF9C27B0)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Card de valor total com destaque
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF019D31).copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Valor Total",
                                        fontSize = 14.sp,
                                        color = Color(0xFF019D31),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Estrela",
                                            tint = Color(0xFFFFA726),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Pagamento",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Text(
                                    text = "R$ %.2f".format(Locale.getDefault(), pedido.valor),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF019D31)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botão fechar
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(27.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFF019D31), Color(0xFF06C755))
                                        ),
                                        shape = RoundedCornerShape(27.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Ok",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Entendi",
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
        }
    }
}

@Composable
fun InfoCard(
    titulo: String,
    valor: String,
    icone: androidx.compose.ui.graphics.vector.ImageVector,
    corIcone: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = corIcone.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icone,
                    contentDescription = titulo,
                    tint = corIcone,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = valor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1B1B1B)
                )
            }
        }
    }
}

// Função para formatar data detalhada
fun formatarDataDetalhada(dataISO: String): String {
    return try {
        val localePtBR = Locale.Builder().setLanguage("pt").setRegion("BR").build()
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", localePtBR)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dataISO)

        val outputFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'às' HH:mm", localePtBR)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        Log.e("DATE_FORMAT", "Erro ao formatar data: $dataISO", e)
        "Data inválida"
    }
}

// Função para formatar data no formato do histórico (Sáb, 09/08/2025)
fun formatarDataGrupo(dataISO: String): String {
    return try {
        val localePtBR = Locale.Builder().setLanguage("pt").setRegion("BR").build()
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", localePtBR)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dataISO)

        val outputFormat = SimpleDateFormat("EEE, dd/MM/yyyy", localePtBR)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        Log.e("DATE_FORMAT", "Erro ao formatar data: $dataISO", e)
        "Data inválida"
    }
}

