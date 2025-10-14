package com.exemple.facilita.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exemple.facilita.R
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@Composable
fun TelaAvaliacaoCliente(navController: NavController) {
    var avaliacao by remember { mutableStateOf(5) }
    var comentario by remember { mutableStateOf("O prestador foi pontual e atencioso.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 Cabeçalho verde
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Voltar",
                    tint = Color.Black,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
                Spacer(modifier = Modifier.width(80.dp))
                Text(
                    text = "Avalie o serviço",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Texto introdutório
        Text(
            text = "Sua opinião ajuda a melhorar a experiência de todos",
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Foto do prestador
        Image(
            painter = painterResource(id = R.drawable.foto_perfil),
            contentDescription = "Foto do prestador",
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Nome e serviço
        Text(
            text = "José Silva",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = "Acompanhamento à consulta - 09/09/2025",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Estrelas
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= avaliacao) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Estrela $i",
                    tint = if (i <= avaliacao) Color(0xFFFFD700) else Color.Gray,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(4.dp)
                        .clickable { avaliacao = i }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = when (avaliacao) {
                1 -> "Péssimo"
                2 -> "Ruim"
                3 -> "Regular"
                4 -> "Bom"
                else -> "Excelente"
            },
            color = Color.Gray,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Campo de opinião com linha verde
        Text(
            text = "Escreva aqui sua opinião (opcional)",
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = comentario,
            onValueChange = { comentario = it },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(100.dp),
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF019D31),
                unfocusedBorderColor = Color(0xFF019D31)
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
        )

        Spacer(modifier = Modifier.height(180.dp))

        // 🔹 Botão com gradiente verde
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF019D31), Color(0xFF006622))
                    )
                )
                .clickable {
                    // Enviar avaliação aqui
                    println("Avaliação: $avaliacao estrelas - $comentario")
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Enviar Avaliação",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaAvaliacaoClientePreview() {
    val navController = rememberNavController()
    TelaAvaliacaoCliente(navController = navController)
}
