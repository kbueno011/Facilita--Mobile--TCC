package com.exemple.facilita.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    .background(Color.White)
                    .height(64.dp)
            ) {
                Text(
                    text = "Histórico de Pedidos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1B1B1B),
                    modifier = Modifier.align(Alignment.Center)
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Voltar",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.CenterStart)
                        .offset(y = 0.dp)
                        .padding(start = 8.dp)
                        .clickable { navController.popBackStack() }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F4F4))
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF019D31)
                    )
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
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
                                containerColor = Color(0xFF019D31)
                            )
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }
                pedidos.isEmpty() -> {
                    Text(
                        text = "Nenhum pedido encontrado",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        pedidosPorData.forEach { (data, pedidosData) ->
                            item {
                                Text(
                                    text = data,
                                    fontSize = 12.sp,
                                    color = Color(0xFF888888),
                                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                                )
                            }

                            items(pedidosData) { pedido ->
                                PedidoCardOriginal(pedido = pedido)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PedidoCardOriginal(pedido: PedidoApi) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Modalidade
            Text(
                text = "Modalidade: ${pedido.categoria?.nome ?: "Carro"} - Personalizado",
                fontSize = 12.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Foto do prestador
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
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFE0E0E0), CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Código do pedido
                    Text(
                        text = String.format(Locale.getDefault(), "RVJ9G%02d", pedido.id % 100),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1B1B)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Entregador e avaliação
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Entregador : ",
                            fontSize = 11.sp,
                            color = Color(0xFF019D31)
                        )
                        Text(
                            text = pedido.prestador?.usuario?.nome ?: "Aguardando",
                            fontSize = 11.sp,
                            color = Color(0xFF019D31),
                            fontWeight = FontWeight.SemiBold
                        )
                        if (pedido.prestador != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Estrela",
                                tint = Color(0xFFFFA726),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = " 4.7",
                                fontSize = 11.sp,
                                color = Color(0xFF888888)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status e Valor
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (pedido.status) {
                        "EM_ANDAMENTO" -> Color(0xFFE8E8E8)
                        "FINALIZADO", "CONCLUIDO" -> Color(0xFF019D31)
                        "CANCELADO" -> Color(0xFFD32F2F)
                        else -> Color(0xFFFFA726)
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
                        fontSize = 11.sp,
                        color = when (pedido.status) {
                            "EM_ANDAMENTO" -> Color(0xFF333333)
                            "FINALIZADO", "CONCLUIDO" -> Color.White
                            "CANCELADO" -> Color.White
                            else -> Color.White
                        },
                        fontWeight = FontWeight.Medium
                    )
                }

                // Valor
                Text(
                    text = "R$ %.2f".format(Locale.getDefault(), pedido.valor),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1B1B)
                )
            }
        }
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

