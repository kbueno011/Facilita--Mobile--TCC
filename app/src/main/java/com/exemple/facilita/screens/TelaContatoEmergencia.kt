package com.exemple.facilita.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TelaContatoEmergencia(
    onSalvar: () -> Unit = {},
    onVoltar: (() -> Unit)? = null
) {
    var nomeContato by remember { mutableStateOf("") }
    var telefoneContato by remember { mutableStateOf("") }
    var parentesco by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFFF2F2F2)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Contato de Emergência",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = nomeContato,
                onValueChange = { nomeContato = it },
                label = { Text("Nome completo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = telefoneContato,
                onValueChange = { telefoneContato = it },
                label = { Text("Telefone") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = parentesco,
                onValueChange = { parentesco = it },
                label = { Text("Grau de parentesco") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onSalvar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF019D31)),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text("Salvar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTelaContatoEmergencia() {
    TelaContatoEmergencia()
}
