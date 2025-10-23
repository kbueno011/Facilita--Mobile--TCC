package com.exemple.facilita.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.exemple.facilita.components.BottomNavBar
import com.exemple.facilita.model.ServicoResponse
import java.io.Serializable
import kotlinx.coroutines.delay

@Composable
fun TelaStatusPagamento(navController: NavController) {
    val servico by remember {
        mutableStateOf(
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Serializable>("servicoData") as? ServicoResponse
        )
    }

    val scrollState = rememberScrollState()
    val headerGradient = Brush.horizontalGradient(listOf(Color(0xFF06C755), Color(0xFF00A651)))
    val valueGradient = Brush.verticalGradient(listOf(Color(0xFF00D97E), Color(0xFF019D31)))

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFFF5F5F7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabeçalho futurista menor
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        brush = headerGradient,
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowLeft,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { navController.popBackStack() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Status do Serviço",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            servico?.let { s ->
                // Card principal animado
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 500)
                    ) + fadeIn(animationSpec = tween(500))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F5)),
                        elevation = CardDefaults.cardElevation(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            val valorTotal = s.detalhes_valor?.valor_total ?: s.valor ?: 0.0
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(valueGradient, RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "Valor a ser pago",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        "R$ ${"%.2f".format(valorTotal)}",
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Linhas de informação animadas individualmente
                            LinhaInfoFuturistaAnimada("ID do Serviço", s.id?.toString() ?: "—", delay = 100)
                            LinhaInfoFuturistaAnimada("Status", s.status ?: "PENDENTE", delay = 200)
                            LinhaInfoFuturistaAnimada("Categoria", s.categoria?.nome ?: "Não informado", delay = 300)
                            LinhaInfoFuturistaAnimada("Descrição", s.descricao ?: "Não informado", delay = 400)
                            LinhaInfoFuturistaAnimada("Contratante", s.contratante?.usuario?.nome ?: "Não informado", delay = 500)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botão Confirmar Valor fora do card
                var botaoVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(600)
                    botaoVisible = true
                }

                AnimatedVisibility(
                    visible = botaoVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(500)
                    ) + fadeIn(animationSpec = tween(500))
                ) {
                    Button(
                        onClick = { navController.navigate("tela_confirmacao") },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(65.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(listOf(Color(0xFF00C755), Color(0xFF019D31))),
                                    shape = RoundedCornerShape(50)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Confirmar Valor",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF06C755),
                        strokeWidth = 6.dp
                    )
                }
            }
        }
    }
}

@Composable
fun LinhaInfoFuturistaAnimada(titulo: String, valor: String, delay: Int = 0) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it / 2 },
            animationSpec = tween(durationMillis = 400)
        ) + fadeIn(animationSpec = tween(400))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .background(
                    Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                titulo,
                fontSize = 13.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                valor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF006622)
            )
            Divider(
                color = Color.Gray.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}
