package com.exemplo.facilita.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.draw.clip

@Composable
fun TelaChat() {
    val greenColor = Color(0xFF019D31)
    val lightGray = Color(0xFFF7F7F7)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE7E7E7))
    ) {
        // Topo verde
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(greenColor)
                .height(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { }
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 18.dp)
                ) {
                    Text(
                        text = "Vithor Kouzmin",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "PRETA-GFQ-3125",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Chamada",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Vídeo",
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { }
                    )
                }
            }
        }

        // Conteúdo do chat
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9F9F9))
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(Color((0xFFF2F2F2)), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Hoje",
                    color = Color(0xFF7C7C7C),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mensagem recebida
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 60.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp) // 🔹 altura menor
            ) {
                Column {
                    Text(
                        text = "Olá, gostaria de escolher seus produtos na feira por ligação de video ou audio?",
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "15:33",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mensagem enviada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 60.dp)
                    .background(greenColor, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 1.dp) // 🔹 altura menor
                    .align(Alignment.End)
            ) {
                Column {
                    Text(
                        text = "Ola Vithor, gostaria sim",
                        color = Color.White,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "15:33",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                            .offset(y = (-1).dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Caixa de texto
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val textState = remember { mutableStateOf(TextFieldValue("")) }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEDEDED))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (textState.value.text.isEmpty()) {
                        Text(
                            text = "Enviar mensagem",
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                    }
                    BasicTextField(
                        value = textState.value,
                        onValueChange = { textState.value = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.Black,
                            fontSize = 15.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaChatPreview() {
    MaterialTheme {
        TelaChat()
    }
}
