package com.exemple.facilita.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.exemple.facilita.R
import kotlinx.coroutines.delay

@Composable
fun TelaRecuperacaoSenha(navController: NavController) {
    var code by remember { mutableStateOf(List(5) { "" }) }
    var timer by remember { mutableStateOf(30) }

    // Contador regressivo para reenvio do código
    LaunchedEffect(Unit) {
        while (timer > 0) {
            delay(1000)
            timer--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF444444)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Parte branca com inputs

        Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(4f)
            .background(Color.White, shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .padding(horizontal = 24.dp, vertical = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )

        ){
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Recuperação de Senha",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Informe o código de 5 dígitos que foi enviado para o e-mail")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("m*****@gmail.com")
                        }
                    },
                    fontSize = 14.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campos para código de 5 dígitos
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    code.forEachIndexed { index, value ->
                        OutlinedTextField(
                            value = value,
                            onValueChange = {
                                if (it.length <= 1) {
                                    code = code.toMutableList().also { list -> list[index] = it }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (timer > 0) "Reenviar o código em $timer segundos." else "Reenviar código",
                    fontSize = 14.sp,
                    color = if (timer > 0) Color.Gray else Color(0xFF06C755),
                    fontWeight = if (timer == 0) FontWeight.Bold else FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Botão verificar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tentar outro método.",
                        fontSize = 14.sp,
                        color = Color(0xFF06C755),
                        modifier = Modifier.padding(end = 16.dp)
                    )

                    Button(
                        onClick = { /* verificar código */ },
                        modifier = Modifier
                            .width(140.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFF019D31), Color(0xFF06C755))
                                    ),
                                    shape = RoundedCornerShape(50)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Verificar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }



        // Parte inferior com imagem + logo + textura
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f)
                .background(Color(0xFF444444)),
        ) {
            // Textura alinhada ao fundo
            Image(
                painter = painterResource(R.drawable.texturainferior),
                contentDescription = "Textura inferior",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(170.dp)
                    .height(190.dp)

            )

            // Logo sobre a textura
            Image(
                painter = painterResource(R.drawable.logotcc),
                contentDescription = "Logo Facilita",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp)
                    .height(120.dp)
                    .width(120.dp)

            )

            // Carro centralizado acima
            Image(
                painter = painterResource(R.drawable.iconcarromenu),
                contentDescription = "Ilustração carro",
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(350.dp)
                    .width(350.dp)
            )
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaRecuperacaoSenhaPreview() {
    val navController = rememberNavController()
    TelaRecuperacaoSenha(navController = navController)
}