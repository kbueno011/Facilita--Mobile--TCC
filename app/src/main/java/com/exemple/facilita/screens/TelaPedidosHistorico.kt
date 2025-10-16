package com.exemple.facilita.screens

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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.exemple.facilita.components.BottomNavBar

data class Pedido(
    val data: String,
    val modalidade: String,
    val codigo: String,
    val entregador: String,
    val avaliacao: Double,
    val valor: String,
    val status: String,
    val foto: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPedidosHistorico(navController: NavController) {
    val pedidos = listOf(
        Pedido("Sáb, 09/08/2025", "Serviço a feira", "RVJ9G33", "Kaike Bueno", 4.7, "R$ 119,99", "Em andamento", "https://i.pravatar.cc/150?img=1"),
        Pedido("Sáb, 09/08/2025", "Serviço ao hortifruti", "XTD2K19", "Letícia Mello", 4.9, "R$ 291,76", "Finalizado", "https://i.pravatar.cc/150?img=2"),
        Pedido("Qua, 02/07/2025", "Serviço ao correio e feira", "FFV3G45", "Bruno Silva", 4.5, "R$ 65,47", "Finalizado", "https://i.pravatar.cc/150?img=3"),
    )

    val pedidosPorData = pedidos.groupBy { it.data }

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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título "Histórico"
                item {
                    Text(
                        text = "Histórico",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF000000),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                    Text(
                        text = "Seus serviços recentes apareceram aqui",
                        fontSize = 14.sp,
                        color = Color(0xFF000000),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 0.dp)
                    )
                }


                pedidosPorData.forEach { (data, pedidosDoDia) ->
                    // Header da data
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF4F4F4))
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = data,
                                fontSize = 14.sp,
                                color = Color(0xFF6D6D6D),
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                        }
                    }

                    // Cards do dia
                    items(pedidosDoDia) { pedido ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            PedidoCardFuturista(pedido)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PedidoCardFuturista(pedido: Pedido) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(135.dp) // altura total do card
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try { awaitRelease() } finally { pressed = false }
                    },
                    onTap = { /* abrir detalhes */ }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize() // 🔹 importante! ocupa toda a altura do Card
                .border(
                    BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(Color(0xFF3C604B), Color(0xFF00B14F))
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFF8F8F8), Color(0xFFFDFDFD))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp) // padding interno continua normal
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Foto do entregador
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE9E9E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(pedido.foto),
                        contentDescription = "Foto do entregador",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pedido.modalidade,
                            color = Color(0xFF2D2D2D),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14   .sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Avaliação",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = pedido.avaliacao.toString(),
                            fontSize = 12.sp,
                            color = Color(0xFF6D6D6D),
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Entregador: ${pedido.entregador}",
                        color = Color(0xFF6D6D6D),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Surface(
                        shape = RoundedCornerShape(20),
                        color = Color(0x22019D31)
                    ) {
                        Text(
                            text = pedido.status,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = if (pedido.status == "Finalizado") Color(0xFF019D31) else Color(0xFF000000),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = pedido.valor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF3C604B)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x11019D31)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Abrir",
                        tint = Color(0xFF019D31)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaPedidosHistoricoPreview() {
    val navController = rememberNavController()
    TelaPedidosHistorico(navController)
}