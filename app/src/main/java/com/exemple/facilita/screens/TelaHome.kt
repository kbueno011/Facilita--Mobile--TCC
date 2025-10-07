package com.exemple.facilita.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.exemple.facilita.R
import com.exemple.facilita.components.BottomNavBar

@Composable
fun TelaHome(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) },
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
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(color = 0xFFF2F2F2),  RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificação",
                        tint = Color.Black,
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
                    .height(140.dp), // altura maior para caber imagem e botão
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3C604B)) // verde mais escuro
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 🟢 Parte da esquerda (texto + botão)
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Monte o seu serviço",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 🔹 Botão "Montar"
                        Button(
                            onClick = { /* ação aqui */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF019D31)),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.height(20.dp), // define altura menor
                            contentPadding = PaddingValues(horizontal = 10.dp) // padding vertical agora é controlado pelo height
                        ) {
                            Text(text = "Montar", color = Color.White, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }


                    Image(
                        painter = painterResource(id = R.drawable.caminhao_servico), // 👉 coloque sua imagem aqui
                        contentDescription = "Serviço",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(start = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaServicos) { servico ->
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = (Color(color = 0xFFF2F2F2))),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = servico.imagem),
                                contentDescription = servico.nome,
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(servico.nome, fontSize = 12.sp, color = Color.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .offset(y = (-10).dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF02812B))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Em caso de dúvidas,\nconsulte o nosso suporte",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Clique aqui",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.suporte),
                        contentDescription = "Suporte",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(start = 8.dp)
                    )
                }
            }
        }
    }
}



data class Servico(val nome: String, val imagem: Int)

val listaServicos = listOf(
    Servico("Farmácia", R.drawable.farmacia),
    Servico("Correio", R.drawable.correio),
    Servico("Mercado", R.drawable.mercado),
    Servico("Shopping", R.drawable.shopping),
    Servico("Feira", R.drawable.feira),
    Servico("Hortifruti", R.drawable.hortifruti),
    Servico("Lavanderia", R.drawable.lavanderia)
)
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