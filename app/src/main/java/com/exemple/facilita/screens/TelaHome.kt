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

@Composable
fun TelaHome(navController: NavController) {
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(bottom = 25.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MenuItem("Home", Icons.Default.Home, true)
                MenuItem("Buscar", Icons.Default.Search, false)
                MenuItem("Pedidos", Icons.Default.List, false)
                MenuItem("Carteira", Icons.Default.AccountBalanceWallet, false)
                MenuItem("Perfil", Icons.Default.Person, false)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Endereço e Notificação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Rua Elton Silva, 509", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = "Olá, Lara",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF019D31), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificação",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Barra de pesquisa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .background(Color(0xFFF2F2F2), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Pesquisar",
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Solicite seu serviço", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Monte seu serviço
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF019D31))
            ) {
                Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Monte o seu serviço",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Grid de categorias
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoriaHome("Farmácia", Icons.Default.LocalPharmacy)
                    CategoriaHome("Correio", Icons.Default.LocalPostOffice)
                    CategoriaHome("Mercado", Icons.Default.ShoppingCart)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoriaHome("Shopping", Icons.Default.Store)
                    CategoriaHome("Feira", Icons.Default.OutdoorGrill)
                    CategoriaHome("Hortifruti", Icons.Default.Eco)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    CategoriaHome("Lavanderia", Icons.Default.LocalLaundryService)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Banner de suporte
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF019D31))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Em caso de dúvidas,\nconsulte o nosso suporte",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Clique aqui",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Suporte",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoriaHome(titulo: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Card(
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F6F6))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = icon,
                    contentDescription = titulo,
                    tint = Color(0xFF019D31),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = titulo, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MenuItem(titulo: String, icon: androidx.compose.ui.graphics.vector.ImageVector, ativo: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = titulo,
            tint = if (ativo) Color.Black else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = titulo,
            fontSize = 12.sp,
            color = if (ativo) Color.Black else Color.Gray
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaHomePreview() {
    val navController = rememberNavController()
    TelaHome(navController = navController)
}
