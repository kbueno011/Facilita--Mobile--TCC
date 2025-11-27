package com.exemple.facilita.screens


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@Composable
fun TelaAvaliacaoCliente(
    navController: NavController,
    servicoId: String = "0",
    prestadorNome: String = "Prestador",
    valorServico: String = "0.00"
) {
    var avaliacao by remember { mutableStateOf(5) }
    var comentario by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F7))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header igual ao padrão do projeto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF3C604B), Color(0xFF00B14F))
                        ),
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Voltar",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Avalie o serviço",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Card do Prestador
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar com iniciais
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF3C604B), Color(0xFF00B14F))
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = prestadorNome.take(2).uppercase(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = prestadorNome,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D2D)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Como foi sua experiência?",
                            fontSize = 14.sp,
                            color = Color(0xFF6D6D6D)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Card de Avaliação
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Avaliação",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D2D)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Estrelas
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (i in 1..5) {
                                Icon(
                                    imageVector = if (i <= avaliacao) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = "Estrela $i",
                                    tint = if (i <= avaliacao) Color(0xFFFFD700) else Color(0xFFE0E0E0),
                                    modifier = Modifier
                                        .size(44.dp)
                                        .padding(4.dp)
                                        .clickable { avaliacao = i }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Texto da avaliação
                        Text(
                            text = when (avaliacao) {
                                1 -> "Péssimo"
                                2 -> "Ruim"
                                3 -> "Regular"
                                4 -> "Bom"
                                else -> "Excelente"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF00B14F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Card de Comentário
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Comentário (opcional)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D2D)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = comentario,
                            onValueChange = { comentario = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = {
                                Text(
                                    "Conte-nos mais sobre sua experiência...",
                                    fontSize = 14.sp,
                                    color = Color(0xFF9D9D9D)
                                )
                            },
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00B14F),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedContainerColor = Color(0xFFFAFAFA),
                                unfocusedContainerColor = Color(0xFFFAFAFA)
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botão Enviar Avaliação
                Button(
                    onClick = {
                        // Enviar avaliação
                        println("Avaliação: $avaliacao estrelas - $comentario")
                        // TODO: Implementar chamada à API

                        Toast.makeText(
                            context,
                            "Obrigado por usar nosso aplicativo! Sua avaliação foi registrada com sucesso.",
                            Toast.LENGTH_LONG
                        ).show()

                        // Navega para home
                        navController.navigate("tela_home") {
                            popUpTo("tela_home") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00B14F)
                    )
                ) {
                    Text(
                        text = "Enviar Avaliação",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaAvaliacaoClientePreview() {
    val navController = rememberNavController()
    TelaAvaliacaoCliente(navController = navController)
}
