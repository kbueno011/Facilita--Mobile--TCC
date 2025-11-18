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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.exemple.facilita.R
import com.exemple.facilita.components.BottomNavBar

import com.exemple.facilita.components.IconeNotificacao
import com.exemple.facilita.utils.TokenManager
import com.exemple.facilita.utils.sdp
import com.exemple.facilita.utils.ssp

@Composable
fun TelaHome(navController: NavController) {
    val context = LocalContext.current
    val nomeUsuario = TokenManager.obterNomeUsuario(context) ?: "Usuário"

    Scaffold(bottomBar = { BottomNavBar(navController) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.sdp())
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Olá, $nomeUsuario",
                        fontSize = 24.ssp(),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2D2D2D)
                    )
                }
                IconeNotificacao(navController = navController)
            }

            Spacer(modifier = Modifier.height(16.sdp()))

            // Search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.sdp())
                    .clip(RoundedCornerShape(14.sdp()))
                    .background(Color(0xFFF2F2F2))
                    .border(
                        BorderStroke(1.sdp(), Color(0x22019D31)),
                        shape = RoundedCornerShape(14.sdp())
                    )
                    .padding(horizontal = 14.sdp()),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Pesquisar",
                        tint = Color(0xFF019D31),
                        modifier = Modifier.size(24.sdp())
                    )
                    Spacer(modifier = Modifier.width(10.sdp()))
                    Text(text = "Solicite seu serviço", color = Color.Gray, fontSize = 14.ssp())
                }
            }

            Spacer(modifier = Modifier.height(18.sdp()))

            // Monte seu serviço
            var pressedCard by remember { mutableStateOf(false) }
            val scaleCard by animateFloatAsState(if (pressedCard) 0.97f else 1f)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.sdp())
                    .scale(scaleCard)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                pressedCard = true
                                try {
                                    awaitRelease()
                                    navController.navigate("tela_endereco")
                                } finally {
                                    pressedCard = false
                                }
                            }
                        )
                    },
                shape = RoundedCornerShape(20.sdp()),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(6.sdp())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF246537), Color(0xFF699D78))
                            ),
                            shape = RoundedCornerShape(20.sdp())
                        )
                        .padding(18.sdp())
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
                                fontSize = 18.ssp()
                            )

                            Button(
                                onClick = { navController.navigate("tela_endereco") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(34.sdp())
                            ) {
                                Text("Montar", color = Color(0xFF4E7B5E), fontSize = 13.ssp())
                                Spacer(Modifier.width(6.sdp()))
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFF4E7B5E),
                                    modifier = Modifier.size(18.sdp())
                                )
                            }
                        }

                        Image(
                            painter = painterResource(id = R.drawable.caminhao_servico),
                            contentDescription = "Serviço",
                            modifier = Modifier.size(110.sdp())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.sdp()))

            // Serviços recentes
            Text(
                text = "Serviços Recentes",
                color = Color(0xFF2D2D2D),
                fontWeight = FontWeight.Bold,
                fontSize = 16.ssp()
            )

            Spacer(modifier = Modifier.height(12.sdp()))

            RecentesFuturistaClaro(
                lista = listaServicos,
                onItemClick = { /* ação */ }
            )

            Spacer(modifier = Modifier.height(20.sdp()))

            // Grid de serviços (categorias)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.sdp()),
                verticalArrangement = Arrangement.spacedBy(12.sdp()),
                horizontalArrangement = Arrangement.spacedBy(12.sdp())
            ) {
                items(listaServicos) { servico ->
                    var pressed by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(if (pressed) 0.95f else 1f)

                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.sdp()))
                            .fillMaxWidth()
                            .scale(scale)
                            .pointerInput(servico) {
                                detectTapGestures(
                                    onPress = {
                                        pressed = true
                                        try {
                                            awaitRelease()
                                        } finally {
                                            pressed = false
                                        }
                                    },
                                    onTap = {
                                        navController.navigate("tela_servico_categoria/${servico.nome}")
                                    }
                                )
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F6F6)),
                        elevation = CardDefaults.cardElevation(2.sdp())
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.sdp())
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = servico.imagem),
                                contentDescription = servico.nome,
                                modifier = Modifier.size(42.sdp())
                            )
                            Spacer(modifier = Modifier.height(6.sdp()))
                            Text(servico.nome, fontSize = 11.ssp(), color = Color(0xFF2D2D2D))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.sdp()))

            // Card de suporte futurista
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.sdp())
                    .clickable { navController.navigate("tela_ajuda_suporte") },
                shape = RoundedCornerShape(20.sdp()),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(6.sdp())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF019D31), Color(0xFF71C58C))
                            ),
                            shape = RoundedCornerShape(20.sdp())
                        )
                        .padding(18.sdp())
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
                                fontSize = 17.ssp()
                            )
                            Spacer(modifier = Modifier.height(4.sdp()))
                            Text(
                                text = "Fale com nosso suporte a qualquer momento.",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.ssp()
                            )
                            Spacer(modifier = Modifier.height(10.sdp()))
                            Button(
                                onClick = { navController.navigate("tela_ajuda_suporte") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(36.sdp())
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HeadsetMic,
                                    contentDescription = null,
                                    tint = Color(0xFF019D31),
                                    modifier = Modifier.size(18.sdp())
                                )
                                Spacer(modifier = Modifier.width(6.sdp()))
                                Text("Entrar em contato", color = Color(0xFF019D31), fontSize = 13.ssp())
                            }
                        }

                        Image(
                            painter = painterResource(id = R.drawable.suporte),
                            contentDescription = "Suporte",
                            modifier = Modifier.size(90.sdp())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.sdp()))
        }
    }
}

// --- Serviços recentes futurista ---
@Composable
private fun RecentesFuturistaClaro(lista: List<Servico>, onItemClick: (Servico) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.sdp()),
        contentPadding = PaddingValues(horizontal = 4.sdp()),
        horizontalArrangement = Arrangement.spacedBy(12.sdp())
    ) {
        items(lista) { servico ->
            var pressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(if (pressed) 0.97f else 1f)

            Card(
                modifier = Modifier
                    .width(250.sdp())
                    .height(140.sdp())
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
                shape = RoundedCornerShape(16.sdp()),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(4.sdp())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            BorderStroke(
                                2.sdp(),
                                Brush.horizontalGradient(listOf(Color(0xFF3C604B), Color(0xFF019D31)))
                            ),
                            shape = RoundedCornerShape(16.sdp())
                        )
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFF4F4F4), Color(0xFFF5F5F5))
                            ),
                            shape = RoundedCornerShape(16.sdp())
                        )
                        .padding(12.sdp())
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.sdp())
                                .clip(CircleShape)
                                .background(Color(0xFFE8E8E8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.sdp())
                                    .clip(CircleShape)
                                    .background(Color(0xFF019D31).copy(alpha = 0.9f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = servico.imagem),
                                    contentDescription = servico.nome,
                                    modifier = Modifier.size(34.sdp())
                                )
                            }
                        }

                        Spacer(Modifier.width(12.sdp()))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(servico.nome, color = Color(0xFF2D2D2D), fontWeight = FontWeight.Bold, fontSize = 15.ssp())
                            Spacer(modifier = Modifier.height(5.sdp()))
                            Text("Entrega rápida • 1.2 km", color = Color(0xFF6D6D6D), fontSize = 11.ssp())
                            Spacer(modifier = Modifier.height(7.sdp()))

                            Surface(
                                shape = RoundedCornerShape(20),
                                color = Color(0x22019D31)
                            ) {
                                Text(
                                    text = "R$ 4,50",
                                    modifier = Modifier.padding(horizontal = 8.sdp(), vertical = 5.sdp()),
                                    color = Color(0xFF3C604B),
                                    fontSize = 11.ssp()
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(34.sdp())
                                .clip(RoundedCornerShape(10.sdp()))
                                .background(Color(0x11019D31)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Abrir",
                                tint = Color(0xFF019D31),
                                modifier = Modifier.size(20.sdp())
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
    Servico("Feira", R.drawable.feira),
    Servico("Hortifruti", R.drawable.hortifruti),
    Servico("Lavanderia", R.drawable.lavanderia)
)

