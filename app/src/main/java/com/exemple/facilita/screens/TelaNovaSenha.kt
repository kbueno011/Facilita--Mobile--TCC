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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.exemple.facilita.R
import com.exemple.facilita.model.TrocarSenhaRequest
import com.exemple.facilita.model.TrocarSenhaResponse
import com.exemple.facilita.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun TelaNovaSenha(navController: NavController, codigo: String) {
    var novaSenha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF444444)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Parte branca superior
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Crie uma nova senha",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = novaSenha,
                    onValueChange = { novaSenha = it },
                    label = { Text("Nova Senha") },
                    placeholder = { Text("Digite sua nova senha") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmarSenha,
                    onValueChange = { confirmarSenha = it },
                    label = { Text("Confirmar Senha") },
                    placeholder = { Text("Confirme sua senha") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botão "Salvar"
                Button(
                    onClick = {
                        if (novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
                            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (novaSenha != confirmarSenha) {
                            Toast.makeText(context, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        val service = RetrofitFactory().getUserService()
                        val request = TrocarSenhaRequest(codigo = codigo, novaSenha = novaSenha)

                        service.trocarSenha(request).enqueue(object : Callback<TrocarSenhaResponse> {
                            override fun onResponse(
                                call: Call<TrocarSenhaResponse>,
                                response: Response<TrocarSenhaResponse>
                            ) {
                                isLoading = false
                                if (response.isSuccessful) {
                                    val body = response.body()
                                    if (body?.sucesso == true) {
                                        Toast.makeText(context, "Senha atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("tela_login")
                                    } else {
                                        Toast.makeText(context, body?.message ?: "Erro ao atualizar senha.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Erro: ${response.code()}", Toast.LENGTH_SHORT).show()
                                }

                            }

                            override fun onFailure(call: Call<TrocarSenhaResponse>, t: Throwable) {
                                isLoading = false
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
                                text = "Salvar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Parte inferior escura com imagens
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f)
                .background(Color(0xFF444444)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(R.drawable.iconcarromenu),
                contentDescription = "Carro de entregas",
                modifier = Modifier
                    .height(300.dp)
                    .align(Alignment.Center)
            )

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
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaNovaSenhaPreview() {
    val navController = rememberNavController()
    TelaNovaSenha(navController = navController, codigo = "92156")
}
