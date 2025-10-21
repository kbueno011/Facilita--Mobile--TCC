package com.exemple.facilita.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

data class Notificacao(
    val titulo: String,
    val descricao: String,
    val data: String,
    val cor: Color,
    val icone: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNotificacoes(navController: NavController) {
    val listaNotificacoes = listOf(
        Notificacao(
            "O entregador está próximo do destino",
            "Seu pedido chega daqui 15 minutos. Aguarde em frente ao local",
            "Hoje - Sept 09, 2025",
            Color(0xFF28A745)
        ) { Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.White) },
        Notificacao(
            "Pagamento confirmado",
            "Pagamento referente ao pedido #148449 confirmado",
            "Hoje - Sept 09, 2025",
            Color(0xFF28A745)
        ) { Icon(Icons.Default.Check, contentDescription = null, tint = Color.White) },
        Notificacao(
            "Solicite um serviço",
            "Nossos serviços estão disponíveis 24h. Acesse agora mesmo",
            "Domingo - Sept 08, 2025",
            Color.Black
        ) { Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White) },
        Notificacao(
            "Acesse o Facilita",
            "Utilize o Facilita para mais praticidade e serviços rápidos",
            "Domingo - Sept 08, 2025",
            Color.Gray
        ) { Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color.White) },
        Notificacao(
            "Pedido cancelado",
            "Infelizmente seu serviço foi cancelado. Acesse o app para mais informações",
            "Domingo - Sept 08, 2025",
            Color(0xFFE53935)
        ) { Icon(Icons.Default.Block, contentDescription = null, tint = Color.White) },
        Notificacao(
            "Falha no pagamento",
            "Algo deu errado na transação. Troque a forma de pagamento",
            "Domingo - Sept 08, 2025",
            Color(0xFF28A745)
        ) { Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White) },
    )

    // 🔹 Agrupa as notificações por data
    val notificacoesAgrupadas = listaNotificacoes.groupBy { it.data }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notificações",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 80.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF009E3A)
                ),
                modifier = Modifier
                    .offset(y = (-12).dp)

                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 🔹 Para cada data, mostra as notificações correspondentes
            notificacoesAgrupadas.forEach { (data, notificacoes) ->
                item {
                    Text(
                        text = data,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color(0xFF333333),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
                    )
                }

                items(notificacoes) { notif ->
                    CardNotificacao(notificacao = notif)
                }
            }
        }
    }
}

@Composable
fun CardNotificacao(notificacao: Notificacao) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🔹 Ícone circular colorido
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(notificacao.cor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                notificacao.icone()
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notificacao.titulo,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
                Text(
                    text = notificacao.descricao,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaNotificacoesPreview() {
    MaterialTheme {
        TelaNotificacoes(navController = rememberNavController())
    }
}