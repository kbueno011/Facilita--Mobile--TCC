package com.exemple.facilita.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import com.exemple.facilita.R

class PerfilPrestadorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PerfilPrestadorScreen()
        }
    }
}

@Composable
fun PerfilPrestadorScreen() {
    var tipoVeiculo by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var ano by remember { mutableStateOf("") }
    var possuiSeguro by remember { mutableStateOf("") }
    var compartimento by remember { mutableStateOf("") }
    var revisao by remember { mutableStateOf("") }
    var antecedentes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto de perfil com ícone de adicionar
        Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = painterResource(id = R.drawable.foto_perfil), // substitua com sua imagem
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar foto",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF4B6EFC), CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Bem vindo entregador", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Complete seu perfil", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // Campos do veículo
        Text("Informações do veículo", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = tipoVeiculo,
            onValueChange = { tipoVeiculo = it },
            label = { Text("Tipo de veículo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = modelo,
                onValueChange = { modelo = it },
                label = { Text("Modelo") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = ano,
                onValueChange = { ano = it },
                label = { Text("Ano") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = possuiSeguro,
            onValueChange = { possuiSeguro = it },
            label = { Text("O veículo possui seguro?") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = compartimento,
            onValueChange = { compartimento = it },
            label = { Text("O veículo tem compartimento adequado para transportar mercadorias?") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = revisao,
            onValueChange = { revisao = it },
            label = { Text("O veículo possui a revisão em dia?") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = antecedentes,
            onValueChange = { antecedentes = it },
            label = { Text("Antecedentes Criminais") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* ação de finalizar */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF019D31)),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Finalizar", color = Color.White, fontSize = 16.sp)
        }
    }
}