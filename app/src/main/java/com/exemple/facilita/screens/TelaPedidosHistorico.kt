package com.exemple.facilita.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
fun TelaPedidos(navController: NavController) {
    val pedidos = listOf(
        Pedido("Sáb, 09/08/2025", "Carro - Personalizado", "RVJ9G33", "Kaike Bueno", 4.7, "R$ 119.99", "Em andamento", "https://i.pravatar.cc/100?img=1"),
        Pedido("Sáb, 05/07/2025", "Carro - Personalizado", "RVJ9G33", "Kaike Bueno", 4.7, "R$ 291.76", "Finalizado", "https://i.pravatar.cc/100?img=2"),
        Pedido("Qua, 02/07/2025", "Carro - Personalizado", "FFV3G45", "Kaike Bueno", 4.7, "R$ 65.47", "Finalizado", "https://i.pravatar.cc/100?img=3"),
    )

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .padding(innerPadding)
        ) {
            Text(
                "Histórico",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF000000),
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pedidos) { pedido ->
                    PedidoCard(pedido)
                }
            }
        }
    }
}

@Composable
fun PedidoCard(pedido: Pedido) {
    val corBorda = if (pedido.status == "Em andamento") Color(0xFF00B14F) else Color(0xFFE5E7EB)
    val corStatus = if (pedido.status == "Finalizado") Color(0xFF00B14F) else Color(0xFF9CA3AF)
    val corTextoStatus = if (pedido.status == "Finalizado") Color.White else Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, corBorda),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Modalidade: Carro - Personalizado",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF1C1C1E),
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color(0xFF6B7280))
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(pedido.foto),
                    contentDescription = "Foto do entregador",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(8.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = pedido.codigo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1C1C1E)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Entregador: ${pedido.entregador}",
                            color = Color(0xFF0077FF),
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("⭐ ${pedido.avaliacao}", fontSize = 13.sp, color = Color(0xFF555555))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(corStatus)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = pedido.status,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = corTextoStatus
                    )
                }

                Text(
                    text = pedido.valor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1C1C1E)
                )
            }
        }
    }
}
