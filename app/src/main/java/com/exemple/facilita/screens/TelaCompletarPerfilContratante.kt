package com.exemple.facilita.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.exemple.facilita.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCompletarPerfilContratante(navController: NavController) {
    var endereco by remember { mutableStateOf("") }
    var servicoSelecionado by remember { mutableStateOf("") }
    var necessidadeSelecionada by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(top = 30.dp),
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFFFFF)
            )
        ) {


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Foto do usuário
                Image(
                    painter = painterResource(R.drawable.foto_perfil), // depois troca pela foto real
                    contentDescription = "Foto do usuário",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Luiz da Silva",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(55.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Complete seu perfil",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(44.dp))

            // Campo Endereço
            OutlinedTextField(
                value = endereco,
                onValueChange = { endereco = it },
                label = { Text("Endereço") },
                placeholder = { Text("Endereço completo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown Preferência de Serviços
            var expandedServicos by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedServicos,
                onExpandedChange = { expandedServicos = !expandedServicos }
            ) {
                OutlinedTextField(
                    value = servicoSelecionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Preferência de Serviços") },
                    placeholder = { Text("Mercado") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedServicos,
                    onDismissRequest = { expandedServicos = false }
                ) {
                    listOf("Mercado", "Transporte", "Limpeza").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                servicoSelecionado = option
                                expandedServicos = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown Necessidades Especiais
            var expandedNecessidades by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedNecessidades,
                onExpandedChange = { expandedNecessidades = !expandedNecessidades }
            ) {
                OutlinedTextField(
                    value = necessidadeSelecionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Necessidades Especiais") },
                    placeholder = { Text("Idoso") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedNecessidades,
                    onDismissRequest = { expandedNecessidades = false }
                ) {
                    listOf("Idoso", "Deficiente físico", "Nenhuma").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                necessidadeSelecionada = option
                                expandedNecessidades = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(178.dp))

        // Botão Finalizar
        Button(
            onClick = {
                navController.navigate("homeContratante")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
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
                            listOf(Color(0xFF27743E), Color(0xFF019D31))
                        ),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Finalizar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaCompletarPerfilContratantePreview() {
    val navController = rememberNavController()
    TelaCompletarPerfilContratante(navController)
}
