package com.exemple.facilita.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.exemple.facilita.components.BottomNavBar

@Composable
fun TelaStatusPagamento(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF4F4F4)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Cabeçalho verde
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF00A651),
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    )
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(60.dp))
                    Text(
                        text = "Status do pagamento",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }


            // Card branco encostando no verde e nas laterais
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 25.dp, bottomEnd = 25.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Card verde do valor dentro do branco
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(70.dp)
                            .background(
                                color = Color(0xFF00A651),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Valor a ser pago",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "R$ 120.00",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Informações da transação (lado a lado)
                    LinhaInfo("ID da Transferência", "00000123")

                    Spacer(modifier = Modifier.height(15.dp))

                    LinhaInfo("Status", "Concluído")

                    Spacer(modifier = Modifier.height(15.dp))

                    LinhaInfo("Valor da Transferência", "R$ 120.00")

                    Spacer(modifier = Modifier.height(15.dp))

                    LinhaInfo("Data", "21/09/25")

                    Spacer(modifier = Modifier.height(15.dp))

                    LinhaInfo("Hora", "11:09 AM")
                }
            }

            Spacer(modifier = Modifier.height(65.dp))

            // Três bolinhas fora do card branco (embaixo dele)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCircle(Icons.Default.Map, "Confirmado", Color.LightGray)
                IconCircle(Icons.Default.Description, "Processando", Color.LightGray)
                IconCircle(Icons.Default.IncompleteCircle, "Concluído", Color(0xFF00A651))
            }
        }
    }
}

@Composable
fun IconCircle(icone: androidx.compose.ui.graphics.vector.ImageVector, descricao: String, cor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color = cor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icone,
                contentDescription = descricao,
                tint = if (cor == Color.LightGray) Color.White.copy(alpha = 0.8f) else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = descricao, fontSize = 12.sp, color = Color.Black)
    }
}

@Composable
fun LinhaInfo(titulo: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            fontSize = 13.sp,
            color = Color.Gray.copy(alpha = 0.8f)
        )
        Text(
            text = valor,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewTelaStatusPagamento() {
    val navController = rememberNavController()
    TelaStatusPagamento(navController = navController)
}