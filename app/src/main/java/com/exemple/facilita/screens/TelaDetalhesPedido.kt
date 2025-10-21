package com.exemple.facilita.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.exemple.facilita.model.PedidoApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalhesPedido(navController: NavController, pedidoJson: String?) {
    val pedido = remember {
        pedidoJson?.let { Gson().fromJson(it, PedidoApi::class.java) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Pedido") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (pedido != null) {
                Text("Descrição: ${pedido.descricao}", fontWeight = FontWeight.Bold)
                Text("Categoria: ${pedido.categoria?.nome ?: "-"}")
                Text("Valor: R$ ${pedido.valor}")
                Text("Status: ${pedido.status}")
                Text("Data solicitação: ${pedido.data_solicitacao}")
                Text("Prestador: ${pedido.prestador?.usuario?.nome ?: "Sem prestador"}")
            } else {
                Text("Erro ao carregar pedido.")
            }
        }
    }
}
