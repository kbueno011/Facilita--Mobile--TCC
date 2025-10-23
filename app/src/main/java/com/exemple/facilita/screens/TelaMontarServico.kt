package com.exemple.facilita.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.exemple.facilita.components.BottomNavBar
import com.exemple.facilita.model.ServicoRequest
import com.exemple.facilita.model.ServicoResponse
import com.exemple.facilita.service.RetrofitFactory
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.launch
import java.io.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaMontarServico(
    navController: NavController,
    endereco: String?
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Inicializa Places API
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, "SUA_API_KEY_AQUI")
        }
    }
    val placesClient = remember { Places.createClient(context) }
    val sessionToken = remember { AutocompleteSessionToken.newInstance() }

    // Campos e estados
    var descricao by remember { mutableStateOf("") }
    var localServico by remember { mutableStateOf("") }
    var sugestoes by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    var exibirSugestoes by remember { mutableStateOf(false) }

    var origemLat by remember { mutableStateOf(-23.5505) }
    var origemLng by remember { mutableStateOf(-46.6333) }
    var destinoLat by remember { mutableStateOf<Double?>(null) }
    var destinoLng by remember { mutableStateOf<Double?>(null) }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFFF0F2F5)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF0F2F5))
        ) {
            // Cabeçalho
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(Color(0xFF00A651), Color(0xFF06C755))),
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { navController.popBackStack() }
                    )
                    Spacer(Modifier.width(20.dp))
                    Text(
                        text = "Monte o seu serviço",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            Text(
                text = "Descreva o serviço e escolha como deseja receber",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF2A2A2A),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Endereço de entrega
            Text(
                text = "Entregar em",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2A2A2A),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(18.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF6B6B6B))
                    Spacer(Modifier.width(12.dp))
                    Text(text = endereco ?: "Nenhum endereço selecionado", fontSize = 14.sp, color = Color(0xFF4A4A4A))
                }
            }

            Spacer(Modifier.height(25.dp))

            // Local do serviço
            Text(
                text = "Local do serviço",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2A2A2A),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(10.dp))
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                OutlinedTextField(
                    value = localServico,
                    onValueChange = {
                        localServico = it
                        exibirSugestoes = true
                        if (it.length > 2) {
                            val request = FindAutocompletePredictionsRequest.builder()
                                .setQuery(it)
                                .setSessionToken(sessionToken)
                                .build()
                            placesClient.findAutocompletePredictions(request)
                                .addOnSuccessListener { response -> sugestoes = response.autocompletePredictions }
                                .addOnFailureListener { sugestoes = emptyList() }
                        } else sugestoes = emptyList()
                    },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color(0xFF6B6B6B)) },
                    placeholder = { Text("Digite o local onde será realizado o serviço") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00A651),
                        unfocusedBorderColor = Color(0xFFBDBDBD),
                        cursorColor = Color(0xFF00A651),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text
                    )
                )

                if (exibirSugestoes && sugestoes.isNotEmpty()) {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(top = 4.dp)
                    ) {
                        items(sugestoes) { prediction ->
                            Text(
                                text = prediction.getFullText(null).toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        localServico = prediction.getFullText(null).toString()
                                        exibirSugestoes = false
                                        sugestoes = emptyList()
                                        val placeId = prediction.placeId
                                        val placeRequest = FetchPlaceRequest.builder(
                                            placeId, listOf(Place.Field.LAT_LNG)
                                        ).setSessionToken(sessionToken).build()
                                        placesClient.fetchPlace(placeRequest)
                                            .addOnSuccessListener { result ->
                                                result.place.latLng?.let {
                                                    destinoLat = it.latitude
                                                    destinoLng = it.longitude
                                                }
                                                Toast.makeText(context, "Endereço selecionado!", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(context, "Falha ao obter detalhes do endereço", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(25.dp))

            // Descrição
            Text(
                text = "Descrição do serviço",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2A2A2A),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                placeholder = { Text("Ex: Comprar remédios na farmácia...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00A651),
                    unfocusedBorderColor = Color(0xFFBDBDBD),
                    cursorColor = Color(0xFF00A651),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(Modifier.weight(1f))

            // Botão Confirmar
            Button(
                onClick = {
                    if (descricao.isBlank() || destinoLat == null || destinoLng == null) {
                        Toast.makeText(context, "Preencha todos os campos antes de confirmar.", Toast.LENGTH_SHORT).show()
                    } else {
                        coroutineScope.launch {
                            try {
                                val token = obterToken(context) ?: ""
                                val servicoRequest = ServicoRequest(
                                    id_categoria = 1,
                                    descricao = descricao,
                                    valor_adicional = 0,
                                    origem_lat = origemLat,
                                    origem_lng = origemLng,
                                    destino_lat = destinoLat!!,
                                    destino_lng = destinoLng!!
                                )
                                val response = RetrofitFactory().getUserService().criarServico(
                                    authToken = "Bearer $token",
                                    request = servicoRequest
                                )
                                if (response.isSuccessful) {
                                    val servicoCriado: ServicoResponse? = response.body()
                                    Toast.makeText(context, "Serviço criado com sucesso!", Toast.LENGTH_LONG).show()
                                    servicoCriado?.let {
                                        navController.currentBackStackEntry?.savedStateHandle?.set("servicoData", it as Serializable)
                                        navController.navigate("tela_status_pagamento")
                                    }
                                } else {
                                    Toast.makeText(context, "Erro ao criar serviço: ${response.code()}", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Falha na conexão: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(85.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF00C755), Color(0xFF019D31)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Confirmar Serviço", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
