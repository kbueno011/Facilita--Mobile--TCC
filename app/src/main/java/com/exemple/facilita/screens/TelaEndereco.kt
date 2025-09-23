package com.exemple.facilita.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.exemple.facilita.model.NominatimResult
import com.exemple.facilita.viewmodel.EnderecoViewModel

@Composable
fun TelaEnderecoContent(
    navController: NavHostController,
    viewModel: EnderecoViewModel
) {
    var query by remember { mutableStateOf("") }
    val sugestoes = remember { mutableStateListOf<NominatimResult>() }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Navegar de volta */ }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar")
            }
            OutlinedTextField(
                value = viewModel.endereco.value,
                onValueChange = { viewModel.endereco.value = it },
                placeholder = { Text("Digite o endereço") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Lista de sugestões
        Column(modifier = Modifier.fillMaxWidth()) {
            sugestoes.forEach { endereco ->
                EnderecoItem(endereco) {
                    viewModel.atualizarEndereco(
                        house = endereco.address?.house_number ?: "",
                        roadName = endereco.address?.road ?: "",
                        cityName = endereco.address?.city ?: "",
                        display = endereco.display_name
                    )
                    // TODO: Navegar para próxima tela se necessário
                }
            }
        }
    }
}

@Composable
fun EnderecoItem(endereco: NominatimResult, onClick: () -> Unit) {
    val titulo = buildString {
        append(endereco.address?.road ?: "")
        endereco.address?.house_number?.let { append(", $it") }
    }.ifBlank { endereco.display_name }

    val detalhes = endereco.display_name

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF019D31)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = detalhes,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
    Divider()
}
