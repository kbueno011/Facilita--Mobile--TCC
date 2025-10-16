package com.exemple.facilita.screens

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.exemple.facilita.R

@Composable
fun TelaInformacoesPerfil(navController: NavController) {
    var emailAtual by remember { mutableStateOf("") }
    var novoEmail by remember { mutableStateOf("") }
    var confirmarEmail by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 Barra superior
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Voltar",
                tint = Color.Black,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { navController.popBackStack() }
            )

            Text(
                text = "Informações do Perfil",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Foto de perfil
        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = painterResource(id = R.drawable.foto_perfil),
                contentDescription = "Foto do usuário",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.LightGray, CircleShape),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = Color(0xFF019D31), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Alterar Email",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(60.dp))

        // 🔹 Campo: Email atual
        OutlinedTextField(
            value = emailAtual,
            onValueChange = { emailAtual = it },
            placeholder = { Text("Digite o email atual") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF019D31),
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 🔹 Campo: Novo email
        OutlinedTextField(
            value = novoEmail,
            onValueChange = { novoEmail = it },
            placeholder = { Text("Novo email") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF019D31),
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 🔹 Campo: Confirmar email
        OutlinedTextField(
            value = confirmarEmail,
            onValueChange = { confirmarEmail = it },
            placeholder = { Text("Confirmar novo email") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF019D31),
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Mensagem de erro ou sucesso
        if (mensagem.isNotEmpty()) {
            Text(
                text = mensagem,
                color = if (erro) Color.Red else Color(0xFF019D31),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 🔹 Botão salvar
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF019D31), Color(0xFF007A26))
                    )
                )
                .clickable {
                    when {
                        novoEmail != confirmarEmail -> {
                            erro = true
                            mensagem = "Os emails não coincidem."
                        }
                        !Patterns.EMAIL_ADDRESS.matcher(novoEmail).matches() -> {
                            erro = true
                            mensagem = "Formato de email inválido."
                        }
                        else -> {
                            erro = false
                            mensagem = "Email alterado com sucesso!"
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Salvar",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

