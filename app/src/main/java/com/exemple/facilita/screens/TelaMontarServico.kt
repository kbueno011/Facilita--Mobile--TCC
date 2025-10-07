package com.exemple.facilita.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun TelaMontarServico(navController: NavController) {
    var pedido by remember { mutableStateOf("") }
    var opcaoSelecionada by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar()
        },
        containerColor = Color(0xFFF4F4F4)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF4F4F4)),
            verticalArrangement = Arrangement.SpaceBetween // distribui conteúdo e botão
        ) {

            Column {
                // Cabeçalho
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .background(
                            color = Color(0xFF00A651),
                            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                        ),
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
                        Spacer(modifier = Modifier.width(65.dp))
                        Text(
                            text = "Monte o seu serviço",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Descrição
                Text(
                    text = "Descreva o que você precisa e escolha como deseja receber",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.85f),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Título do pedido
                Text(
                    text = "Entregar em",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                // Card de endereço
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Localização",
                            tint = Color(0xFF989898)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rua Vitória, Cohab 2, Carapicuíba",
                            fontSize = 14.sp,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Título do pedido
                Text(
                    text = "Pedido",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Pedido",
                            tint = Color(0xFF00A651)
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        OutlinedTextField(
                            value = pedido,
                            onValueChange = { pedido = it },
                            placeholder = { Text("Descreva seu pedido aqui") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            maxLines = 4,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                errorContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                val opcoes = listOf(
                    "Ir ao mercado",
                    "Buscar remédios na farmácia",
                    "Acompanhar em consultas médicas",
                    "Transporte",
                    "Ir a feira",
                    "Retirar pacote no correio"
                )

                // Cards de sugestões
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(opcoes) { opcao ->
                        val isSelected = opcaoSelecionada == opcao
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(width = 160.dp, height = 90.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) Color(0xFF00A651)
                                    else Color.White
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { opcaoSelecionada = opcao }
                        ) {
                            Text(
                                text = opcao,
                                color = if (isSelected) Color.White else Color.Black,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }

            // Botão "Confirmar Serviço" mais embaixo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 70.dp, vertical = 25.dp)
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF019D31), Color(0xFF06C755))
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Confirmar Serviço",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// Barra inferior
@Composable
fun BottomNavigationBar() {
    NavigationBar(containerColor = Color.White, tonalElevation = 4.dp) {
        val itens = listOf(
            Icons.Default.Home to "Home",
            Icons.Default.Search to "Buscar",
            Icons.Default.List to "Pedidos",
            Icons.Default.AccountBalanceWallet to "Carteira",
            Icons.Default.Person to "Perfil"
        )

        itens.forEach { (icone, label) ->
            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = { Icon(icone, contentDescription = label, tint = Color.Gray) },
                label = {
                    Text(
                        label,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaMontarServicoPreview() {
    val navController = rememberNavController()
    TelaMontarServico(navController = navController)
}
