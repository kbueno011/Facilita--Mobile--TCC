package com.exemple.facilita.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.exemple.facilita.R
import com.exemple.facilita.components.BottomNavBar

@Composable
fun TelaHome(navController: NavController) {
    Scaffold(bottomBar = { BottomNavBar(navController) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(18.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Rua Elton Silva, 509", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = "Olá, Lara",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2D2D2D)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFD5D4D4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificação",
                        tint = Color(0xFF019D31),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF2F2F2))
                    .border(
                        BorderStroke(1.dp, Color(0x22019D31)),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Pesquisar",
                        tint = Color(0xFF019D31)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Solicite seu serviço", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Monte seu serviço
            var pressedCard by remember { mutableStateOf(false) }
            val scaleCard by animateFloatAsState(if (pressedCard) 0.97f else 1f)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .scale(scaleCard)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                pressedCard = true
                                try {
                                    awaitRelease()
                                    navController.navigate("tela_montar_servico")
                                } finally {
                                    pressedCard = false
                                }
                            }
                        )
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF246537), Color(0xFF699D78))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Monte o seu serviço",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )

                            Button(
                                onClick = { navController.navigate("montarPedido") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Montar", color = Color(0xFF4E7B5E), fontSize = 14.sp)
                                Spacer(Modifier.width(6.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF4E7B5E))
                            }
                        }

                        Image(
                            painter = painterResource(id = R.drawable.caminhao_servico),
                            contentDescription = "Serviço",
                            modifier = Modifier.size(130.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Serviços recentes
            Text(
                text = "Serviços Recentes",
                color = Color(0xFF2D2D2D),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            RecentesFuturistaClaro(
                lista = listaServicos,
                onItemClick = { /* ação */ }
            )

            Spacer(modifier = Modifier.height(25.dp))

            // Grid de serviços (categorias)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaServicos) { servico ->
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F6F6)),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = servico.imagem),
                                contentDescription = servico.nome,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(servico.nome, fontSize = 12.sp, color = Color(0xFF2D2D2D))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card de suporte futurista (abaixo das categorias)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable { /* ação de suporte */ },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF019D31), Color(0xFF71C58C))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(22.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Precisa de ajuda?",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Fale com nosso suporte a qualquer momento.",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { /* abrir chat suporte */ },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(50)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HeadsetMic,
                                    contentDescription = null,
                                    tint = Color(0xFF019D31)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Entrar em contato", color = Color(0xFF019D31), fontSize = 14.sp)
                            }
                        }

                        Image(
                            painter = painterResource(id = R.drawable.suporte),
                            contentDescription = "Suporte",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

// --- Serviços recentes futurista ---
@Composable
private fun RecentesFuturistaClaro(lista: List<Servico>, onItemClick: (Servico) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(lista) { servico ->
            var pressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(if (pressed) 0.97f else 1f)

            Card(
                modifier = Modifier
                    .width(260.dp)
                    .height(150.dp)
                    .scale(scale)
                    .pointerInput(servico) {
                        detectTapGestures(
                            onPress = {
                                pressed = true
                                try { awaitRelease() } finally { pressed = false }
                            },
                            onTap = { onItemClick(servico) }
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            BorderStroke(
                                2.dp,
                                Brush.horizontalGradient(listOf(Color(0xFF3C604B), Color(0xFF019D31)))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFF4F4F4), Color(0xFFF5F5F5))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8E8E8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF019D31).copy(alpha = 0.9f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = servico.imagem),
                                    contentDescription = servico.nome,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(servico.nome, color = Color(0xFF2D2D2D), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Entrega rápida • 1.2 km", color = Color(0xFF6D6D6D), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(20),
                                color = Color(0x22019D31)
                            ) {
                                Text(
                                    text = "R$ 4,50",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    color = Color(0xFF3C604B),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
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
    }
}

// --- Modelos ---
data class Servico(val nome: String, val imagem: Int)

val listaServicos = listOf(
    Servico("Farmácia", R.drawable.farmacia),
    Servico("Correio", R.drawable.correio),
    Servico("Mercado", R.drawable.mercado),
//    Servico("Shopping", R.drawable.shopping),
    Servico("Feira", R.drawable.feira),
    Servico("Hortifruti", R.drawable.hortifruti),
    Servico("Lavanderia", R.drawable.lavanderia)
)
