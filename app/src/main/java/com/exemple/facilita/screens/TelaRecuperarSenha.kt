package com.exemple.facilita.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
import com.exemple.facilita.model.RecuperarSenhaTelefoneRequest
import com.exemple.facilita.model.VerificarCodigoRequest
import com.exemple.facilita.model.VerificarSenhaResponse
import com.exemple.facilita.service.RetrofitFactory
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun TelaRecuperacaoSenha(navController: NavController) {
    var code by remember { mutableStateOf(List(5) { "" }) }
    var timer by remember { mutableStateOf(30) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var showPhoneInput by remember { mutableStateOf(false) }
    var telefone by remember { mutableStateOf("") }
    var isSendingPhoneRequest by remember { mutableStateOf(false) }

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
        // Parte branca
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(26.dp)
            ) {
                Text(
                    text = "Recuperação de Senha",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Informe o código de 5 dígitos que foi enviado para o e-mail ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("m*****@gmail.com")
                        }
                    },
                    fontSize = 14.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

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

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(
                    onClick = { showPhoneInput = !showPhoneInput }
                ) {
                    Text(
                        text = if (!showPhoneInput) "Tentar outro método." else "Usar e-mail novamente",
                        fontSize = 14.sp,
                        color = Color(0xFF06C755)
                    )
                }

                if (showPhoneInput) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "+55", color = Color.Black, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedTextField(
                                value = telefone,
                                onValueChange = { telefone = it.filter { char -> char.isDigit() } },
                                placeholder = { Text("11 95746-4535") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (telefone.isBlank()) {
                                    Toast.makeText(context, "Preencha o telefone", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val digits = telefone.filter { it.isDigit() }
                                if (digits.length < 10) {
                                    Toast.makeText(context, "Telefone inválido", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val numeroCompleto = "+55$digits"

                                isSendingPhoneRequest = true
                                val service = RetrofitFactory().getUserService()
                                val request = RecuperarSenhaTelefoneRequest(telefone = numeroCompleto)

                                service.recuperarSenhaTelefone(request)
                                    .enqueue(object : Callback<com.exemple.facilita.model.RecuperarSenhaResponse> {
                                        override fun onResponse(
                                            call: Call<com.exemple.facilita.model.RecuperarSenhaResponse>,
                                            response: Response<com.exemple.facilita.model.RecuperarSenhaResponse>
                                        ) {
                                            isSendingPhoneRequest = false
                                            if (response.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    response.body()?.message ?: "Código enviado!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                showPhoneInput = false
                                            } else {
                                                Toast.makeText(context, "Erro: ${response.code()}", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<com.exemple.facilita.model.RecuperarSenhaResponse>,
                                            t: Throwable
                                        ) {
                                            isSendingPhoneRequest = false
                                            Toast.makeText(context, "Falha na conexão.", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(listOf(Color(0xFF019D31), Color(0xFF06C755))),
                                        shape = RoundedCornerShape(50)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSendingPhoneRequest) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Enviar código por SMS",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botão verificar código
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            val codigoCompleto = code.joinToString("")
                            if (codigoCompleto.length < 5) {
                                Toast.makeText(
                                    context,
                                    "Preencha todos os dígitos do código",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                isLoading = true
                                val service = RetrofitFactory().getUserService()
                                val request = VerificarCodigoRequest(codigo = codigoCompleto)

                                service.verificarCodigo(request)
                                    .enqueue(object : Callback<VerificarSenhaResponse> {
                                        override fun onResponse(
                                            call: Call<VerificarSenhaResponse>,
                                            response: Response<VerificarSenhaResponse>
                                        ) {
                                            isLoading = false
                                            if (response.isSuccessful && response.body()?.sucesso == true) {
                                                Toast.makeText(
                                                    context,
                                                    "Código verificado com sucesso!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navController.navigate("tela_nova_senha/${codigoCompleto}")
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Código inválido ou expirado.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<VerificarSenhaResponse>,
                                            t: Throwable
                                        ) {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Erro na conexão. Tente novamente.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            }
                        },
                        modifier = Modifier
                            .width(140.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
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
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
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
        }

        // Parte inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f)
                .background(Color(0xFF444444)),
        ) {
            Image(
                painter = painterResource(R.drawable.texturainferior),
                contentDescription = "Textura inferior",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(170.dp)
                    .height(190.dp)
            )

            Image(
                painter = painterResource(R.drawable.logotcc),
                contentDescription = "Logo Facilita",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp)
                    .height(120.dp)
                    .width(120.dp)
            )

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
