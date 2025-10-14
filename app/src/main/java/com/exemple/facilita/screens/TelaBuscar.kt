package com.exemplo.facilita.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.exemple.facilita.R

@Composable
fun TelaBuscarServico(navController: NavController) {
    val textoBusca = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .padding(16.dp)
    ) {
        // Campo de busca
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
                    value = textoBusca.value,
                    onValueChange = { textoBusca.value = it },
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.DarkGray),
                    decorationBox = { innerTextField ->
                        if (textoBusca.value.isEmpty()) {
                            Text(
                                text = "Solicite seu serviço",
                                color = Color.Gray,
                                fontSize = 18.sp
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Sugestões rápidas
        Text(
            text = "Sugestões rápidas",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF6C6C6C)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SugestaoRapida("Promoções")
            SugestaoRapida("Mercado")
            SugestaoRapida("Shopping")
            SugestaoRapida("Feira")
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Cards de categorias
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CardServico(
                    titulo = "Mercado",
                    cor = Color(0xFFB6D048),
                    imagem = R.drawable.mercado1,
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    tamanhoImagem = 95.dp,
                    imgAlign = Alignment.CenterEnd
                )
                CardServico(
                    titulo = "Feira",
                    cor = Color(0xFF75CD8F  ),
                    imagem = R.drawable.feira1,
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    tamanhoImagem = 180.dp,
                    imgAlign = Alignment.TopEnd
                )
            }

            CardServico(
                titulo = "Farmacia",
                cor = Color(0xFF6FAA8A),
                imagem = R.drawable.farmacia1,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                tamanhoImagem = 200.dp,
                imgAlign = Alignment.CenterEnd
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CardServico(
                    titulo = "Shopping",
                    cor = Color(0xFF388C3B),
                    imagem = R.drawable.shopping1,
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    tamanhoImagem = 110.dp,
                    imgAlign = Alignment.CenterEnd,

                    )
                CardServico(
                    titulo = "Correios",
                    cor = Color(0xFFD7BA0D),
                    imagem = R.drawable.correio1,
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    tamanhoImagem = 80.dp,
                    imgAlign = Alignment.CenterEnd
                )
            }

            CardServico(
                titulo = "Monte o seu serviço",
                cor = Color(0xFF496A52),
                imagem = R.drawable.caminhao_servico,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                tamanhoImagem = 200.dp,
                imgAlign = Alignment.CenterEnd
            )
        }
    }
}

@Composable
fun SugestaoRapida(texto: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFE3F6E6), shape = RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = texto,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF004D25),
            fontSize = 14.sp
        )
    }
}

@Composable
fun CardServico(
    titulo: String,
    cor: Color,
    imagem: Int,
    modifier: Modifier = Modifier,
    tamanhoImagem: Dp = 100.dp,
    imgAlign: Alignment = Alignment.CenterEnd
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Conteúdo textual (lado esquerdo / espaço)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            )  {
                // título em cima
                Text(
                    text = titulo,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 12.dp)
                )


                Button(
                    onClick = {
                        // ação ao clicar, ex: navegar para outra tela
                    },
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(105.dp)  // largura menor
                        .height(28.dp)
                        .scale(0.8f) // altura menor
                        .offset(x = (-14).dp, y = (-14).dp)
                ) {
                    Text(
                        text = "Buscar lojas",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Image(
                painter = painterResource(id = imagem),
                contentDescription = titulo,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 1.dp, bottom = 1.dp)
                    .size(tamanhoImagem),
                contentScale = ContentScale.Fit
            )
        }
    }
}

