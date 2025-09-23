package com.exemple.facilita.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.exemple.facilita.viewmodel.EnderecoViewModel
import kotlinx.coroutines.launch
@Composable
fun TelaEndereco(navController: NavController, viewModel: EnderecoViewModel) {
    val sugestoes by viewModel.sugestoes.collectAsState() // observa o flow
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Escolha o endereço para receber o pedido",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.buscarSugestoes(query) // chama direto
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar endereço") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(sugestoes) { endereco ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Navega passando coordenadas
                            navController.navigate(
                                "confirmar_endereco/${endereco.lat},${endereco.lon}"
                            )
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF019D31)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(endereco.display_name)
                }
                Divider()
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaEnderecoPreview() {
    TelaEndereco()
}
