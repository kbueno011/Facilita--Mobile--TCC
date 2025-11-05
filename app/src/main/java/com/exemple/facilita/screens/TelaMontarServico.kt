package com.exemple.facilita.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.exemple.facilita.components.BottomNavBar
import com.exemple.facilita.model.Parada
import com.exemple.facilita.model.ServicoRequest
import com.exemple.facilita.service.RetrofitFactory
import com.exemple.facilita.utils.TokenManager
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaMontarServico(
    navController: NavController,
    endereco: String?,
    idCategoria: Int = 1 // Valor padrão caso não seja passado
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Inicializa Places API
    val placesClient = remember {
        if (!Places.isInitialized()) {
            Places.initialize(context, context.getString(com.exemple.facilita.R.string.google_maps_key))
        }
        Places.createClient(context)
    }

    val sessionToken = remember { AutocompleteSessionToken.newInstance() }

    // --- Estados de campos ---
    var descricao by remember { mutableStateOf("") }
    var origem by remember { mutableStateOf(TextFieldValue(endereco ?: "")) }
    var destino by remember { mutableStateOf(TextFieldValue("")) }
    var paradas by remember { mutableStateOf(listOf<TextFieldValue>()) }
    val maxParadas = 3

    // Estados para coordenadas
    var origemLat by remember { mutableStateOf<Double?>(null) }
    var origemLng by remember { mutableStateOf<Double?>(null) }
    var destinoLat by remember { mutableStateOf<Double?>(null) }
    var destinoLng by remember { mutableStateOf<Double?>(null) }
    var paradasCoords by remember { mutableStateOf<List<Pair<Double, Double>>>(emptyList()) }

    // Estados para PlaceId (necessário para buscar coordenadas)
    var origemPlaceId by remember { mutableStateOf<String?>(null) }
    var destinoPlaceId by remember { mutableStateOf<String?>(null) }
    var paradasPlaceIds by remember { mutableStateOf<List<String>>(emptyList()) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Estados para autocomplete
    var sugestoesOrigem by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    var sugestoesDestino by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    var sugestoesParadas by remember { mutableStateOf(mutableMapOf<Int, List<AutocompletePrediction>>()) }

    var campoAtivo by remember { mutableStateOf<String?>(null) } // "origem", "destino", "parada_0", etc

    val headerBrush = Brush.horizontalGradient(listOf(Color(0xFF00A651), Color(0xFF06C755)))

    // Função para buscar coordenadas de um PlaceId
    fun buscarCoordenadas(placeId: String, onResult: (Double, Double) -> Unit) {
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                place.latLng?.let { latLng ->
                    onResult(latLng.latitude, latLng.longitude)
                }
            }
            .addOnFailureListener { exception ->
                if (exception is ApiException) {
                    Log.e("PlacesAPI", "Error fetching place: ${exception.statusCode}")
                }
            }
    }

    // Função para buscar sugestões do Google Places
    fun buscarSugestoes(query: String, campo: String) {
        if (query.length < 2) {
            return
        }

        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                when {
                    campo == "origem" -> sugestoesOrigem = response.autocompletePredictions
                    campo == "destino" -> sugestoesDestino = response.autocompletePredictions
                    campo.startsWith("parada_") -> {
                        val index = campo.removePrefix("parada_").toInt()
                        sugestoesParadas = sugestoesParadas.toMutableMap().apply {
                            this[index] = response.autocompletePredictions
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                if (exception is ApiException) {
                    Log.e("PlacesAPI", "Error: ${exception.statusCode}")
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        .height(88.dp)
                        .background(brush = headerBrush, shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Voltar",
                            tint = Color.White,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { navController.popBackStack() }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Monte o seu serviço",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Card único contendo origem / paradas / destino / descrição
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Indicador visual de rota (linha conectando pontos)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Coluna com ícones e linha
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(32.dp)
                            ) {
                                // Ícone origem
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color(0xFF00A651), CircleShape)
                                )

                                // Linha vertical conectando - ajuste dinâmico
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(
                                            when (paradas.size) {
                                                0 -> 140.dp  // Altura base sem paradas
                                                1 -> 230.dp  // Com 1 parada
                                                2 -> 320.dp  // Com 2 paradas
                                                3 -> 410.dp  // Com 3 paradas
                                                else -> 140.dp
                                            }
                                        )
                                        .background(Color(0xFFE0E0E0))
                                )

                                // Ícone destino - verde igual ao projeto
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF00A651),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Coluna com os campos
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Campo Origem com Autocomplete
                                AddressFieldWithAutocomplete(
                                    label = "Origem",
                                    textValue = origem,
                                    onValueChange = { newValue ->
                                        origem = newValue
                                        origemPlaceId = null // Reset placeId quando usuário digita
                                        campoAtivo = "origem"
                                        buscarSugestoes(newValue.text, "origem")
                                    },
                                    placeholder = "De onde você sai?",
                                    suggestions = if (campoAtivo == "origem") sugestoesOrigem else emptyList(),
                                    onSuggestionClick = { prediction ->
                                        origem = TextFieldValue(prediction.getFullText(null).toString())
                                        origemPlaceId = prediction.placeId
                                        campoAtivo = null
                                    },
                                    onFocusChange = { focused ->
                                        if (focused) {
                                            campoAtivo = "origem"
                                            if (origem.text.length >= 2) {
                                                buscarSugestoes(origem.text, "origem")
                                            }
                                        }
                                    }
                                )

                                // Paradas dinâmicas com animação
                                paradas.forEachIndexed { idx, parada ->
                                    val campoParada = "parada_$idx"
                                    AddressFieldWithAutocomplete(
                                        label = "Parada ${idx + 1}",
                                        textValue = parada,
                                        onValueChange = { newValue ->
                                            paradas = paradas.toMutableList().also { it[idx] = newValue }
                                            // Reset placeId quando usuário digita
                                            paradasPlaceIds = paradasPlaceIds.toMutableList().also {
                                                while (it.size <= idx) it.add("")
                                                it[idx] = ""
                                            }
                                            campoAtivo = campoParada
                                            buscarSugestoes(newValue.text, campoParada)
                                        },
                                        placeholder = "Adicione uma parada",
                                        suggestions = if (campoAtivo == campoParada) sugestoesParadas[idx] ?: emptyList() else emptyList(),
                                        onSuggestionClick = { prediction ->
                                            paradas = paradas.toMutableList().also {
                                                it[idx] = TextFieldValue(prediction.getFullText(null).toString())
                                            }
                                            // Atualizar placeIds das paradas
                                            paradasPlaceIds = paradasPlaceIds.toMutableList().also {
                                                while (it.size <= idx) it.add("")
                                                it[idx] = prediction.placeId
                                            }
                                            campoAtivo = null
                                        },
                                        onFocusChange = { focused ->
                                            if (focused) {
                                                campoAtivo = campoParada
                                                if (parada.text.length >= 2) {
                                                    buscarSugestoes(parada.text, campoParada)
                                                }
                                            }
                                        },
                                        showRemoveButton = true,
                                        onRemove = {
                                            paradas = paradas.toMutableList().also { list ->
                                                list.removeAt(idx)
                                            }
                                            paradasPlaceIds = paradasPlaceIds.toMutableList().also { list ->
                                                if (idx < list.size) list.removeAt(idx)
                                            }
                                        }
                                    )
                                }

                                // Botão adicionar parada
                                if (paradas.size < maxParadas) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(0xFF00A651), RoundedCornerShape(8.dp))
                                            .clickable {
                                                if (paradas.size < maxParadas) {
                                                    paradas = paradas + TextFieldValue("")
                                                }
                                            }
                                            .padding(vertical = 12.dp, horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color(0xFF00A651),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Adicionar parada (${paradas.size}/$maxParadas)",
                                            color = Color(0xFF00A651),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                // Campo Destino com Autocomplete
                                AddressFieldWithAutocomplete(
                                    label = "Destino",
                                    textValue = destino,
                                    onValueChange = { newValue ->
                                        destino = newValue
                                        destinoPlaceId = null // Reset placeId quando usuário digita
                                        campoAtivo = "destino"
                                        buscarSugestoes(newValue.text, "destino")
                                    },
                                    placeholder = "Para onde você vai?",
                                    suggestions = if (campoAtivo == "destino") sugestoesDestino else emptyList(),
                                    onSuggestionClick = { prediction ->
                                        destino = TextFieldValue(prediction.getFullText(null).toString())
                                        destinoPlaceId = prediction.placeId
                                        campoAtivo = null
                                    },
                                    onFocusChange = { focused ->
                                        if (focused) {
                                            campoAtivo = "destino"
                                            if (destino.text.length >= 2) {
                                                buscarSugestoes(destino.text, "destino")
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                        // Descrição do serviço
                        Column {
                            Text(
                                text = "Descrição do serviço",
                                fontSize = 14.sp,
                                color = Color(0xFF6D6D6D),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = descricao,
                                onValueChange = { descricao = it },
                                placeholder = { Text("Ex: Comprar remédios na farmácia...", color = Color(0xFFBDBDBD)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00A651),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color(0xFFFAFAFA)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                maxLines = 4
                            )
                        }

                        // Botão confirmar
                        Button(
                            onClick = {
                                // Validação básica
                                if (origem.text.isEmpty()) {
                                    Toast.makeText(context, "Preencha o endereço de origem", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (destino.text.isEmpty()) {
                                    Toast.makeText(context, "Preencha o endereço de destino", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (descricao.isEmpty()) {
                                    Toast.makeText(context, "Preencha a descrição do serviço", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                // Validação dos PlaceIds
                                if (origemPlaceId == null) {
                                    Toast.makeText(context, "Selecione a ORIGEM da lista de sugestões", Toast.LENGTH_LONG).show()
                                    return@Button
                                }

                                if (destinoPlaceId == null) {
                                    Toast.makeText(context, "Selecione o DESTINO da lista de sugestões", Toast.LENGTH_LONG).show()
                                    return@Button
                                }

                                // Validar paradas (se existirem, devem ter placeId)
                                paradas.forEachIndexed { idx, parada ->
                                    if (parada.text.isNotEmpty()) {
                                        if (idx >= paradasPlaceIds.size || paradasPlaceIds[idx].isEmpty()) {
                                            Toast.makeText(context, "Selecione a PARADA ${idx + 1} da lista de sugestões", Toast.LENGTH_LONG).show()
                                            return@Button
                                        }
                                    }
                                }

                                isLoading = true
                                errorMessage = null

                                // Buscar coordenadas da origem
                                buscarCoordenadas(origemPlaceId!!) { latOrigem, lngOrigem ->
                                    origemLat = latOrigem
                                    origemLng = lngOrigem

                                    // Buscar coordenadas do destino
                                    buscarCoordenadas(destinoPlaceId!!) { latDestino, lngDestino ->
                                        destinoLat = latDestino
                                        destinoLng = lngDestino

                                        // Se houver paradas, buscar coordenadas de cada uma
                                        if (paradasPlaceIds.isNotEmpty()) {
                                            val coords = mutableListOf<Pair<Double, Double>>()
                                            var processedCount = 0

                                            paradasPlaceIds.forEachIndexed { index, placeId ->
                                                buscarCoordenadas(placeId) { lat, lng ->
                                                    coords.add(Pair(lat, lng))
                                                    processedCount++

                                                    // Quando todas as coordenadas forem processadas
                                                    if (processedCount == paradasPlaceIds.size) {
                                                        paradasCoords = coords

                                                        // Enviar para API
                                                        enviarServicoParaAPI(
                                                            context = context,
                                                            scope = scope,
                                                            navController = navController,
                                                            idCategoria = idCategoria,
                                                            descricao = descricao,
                                                            origemLat = latOrigem,
                                                            origemLng = lngOrigem,
                                                            origemEndereco = origem.text,
                                                            destinoLat = latDestino,
                                                            destinoLng = lngDestino,
                                                            destinoEndereco = destino.text,
                                                            paradas = paradas,
                                                            paradasCoords = coords,
                                                            onLoadingChange = { isLoading = it },
                                                            onError = { errorMessage = it }
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            // Sem paradas, enviar direto
                                            enviarServicoParaAPI(
                                                context = context,
                                                scope = scope,
                                                navController = navController,
                                                idCategoria = idCategoria,
                                                descricao = descricao,
                                                origemLat = latOrigem,
                                                origemLng = lngOrigem,
                                                origemEndereco = origem.text,
                                                destinoLat = latDestino,
                                                destinoLng = lngDestino,
                                                destinoEndereco = destino.text,
                                                paradas = emptyList(),
                                                paradasCoords = emptyList(),
                                                onLoadingChange = { isLoading = it },
                                                onError = { errorMessage = it }
                                            )
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                if (isLoading) Color(0xFF808080) else Color(0xFF00C755),
                                                if (isLoading) Color(0xFF666666) else Color(0xFF019D31)
                                            )
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Confirmar Serviço",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Mensagem de erro se houver
                        errorMessage?.let { error ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error,
                                color = Color(0xFFD32F2F),
                                fontSize = 13.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Componente de campo de endereço com Google Places Autocomplete integrado
 * Design inspirado no Uber/99 com visual clean e moderno
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressFieldWithAutocomplete(
    label: String,
    textValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String = "",
    suggestions: List<AutocompletePrediction>,
    onSuggestionClick: (AutocompletePrediction) -> Unit,
    onFocusChange: (Boolean) -> Unit = {},
    showRemoveButton: Boolean = false,
    onRemove: () -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Label
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF6D6D6D),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Campo de texto
        OutlinedTextField(
            value = textValue,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFFBDBDBD), fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    onFocusChange(focusState.isFocused)
                },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00A651),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFFAFAFA)
            ),
            trailingIcon = if (showRemoveButton) {
                {
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remover",
                            tint = Color(0xFF757575),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else null,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        // Sugestões do Google Places
        if (isFocused && suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                LazyColumn {
                    items(suggestions) { prediction ->
                        SuggestionItem(
                            prediction = prediction,
                            onClick = { onSuggestionClick(prediction) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Item de sugestão individual
 */
@Composable
fun SuggestionItem(
    prediction: AutocompletePrediction,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color(0xFF757575),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = prediction.getPrimaryText(null).toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2A2A2A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = prediction.getSecondaryText(null).toString(),
                fontSize = 12.sp,
                color = Color(0xFF757575),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
}

/**
 * Função para enviar o serviço para a API
 */
private fun enviarServicoParaAPI(
    context: Context,
    scope: kotlinx.coroutines.CoroutineScope,
    navController: NavController,
    idCategoria: Int,
    descricao: String,
    origemLat: Double,
    origemLng: Double,
    origemEndereco: String,
    destinoLat: Double,
    destinoLng: Double,
    destinoEndereco: String,
    paradas: List<TextFieldValue>,
    paradasCoords: List<Pair<Double, Double>>,
    onLoadingChange: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    scope.launch {
        try {
            // Buscar token usando TokenManager
            val token = TokenManager.obterToken(context)

            if (token == null) {
                onLoadingChange(false)
                onError("Token de autenticação não encontrado. Faça login novamente.")
                Toast.makeText(
                    context,
                    "Token de autenticação não encontrado. Faça login novamente.",
                    Toast.LENGTH_LONG
                ).show()
                return@launch
            }

            // Verificar se é CONTRATANTE
            val tipoConta = TokenManager.obterTipoConta(context)
            Log.d("API_DEBUG", "Tipo de conta: $tipoConta")

            if (tipoConta != "CONTRATANTE") {
                onLoadingChange(false)
                val mensagem = if (tipoConta == null) {
                    "Você precisa completar seu perfil de CONTRATANTE para criar serviços."
                } else {
                    "Apenas usuários CONTRATANTE podem criar serviços. Você está logado como: $tipoConta"
                }
                onError(mensagem)
                Toast.makeText(context, mensagem, Toast.LENGTH_LONG).show()
                Log.e("API_ERROR", "Tentativa de criar serviço sem ser CONTRATANTE: $tipoConta")
                return@launch
            }

            // Criar lista de paradas para a API
            val paradasAPI = paradas.mapIndexed { index, parada ->
                if (index < paradasCoords.size && parada.text.isNotEmpty()) {
                    Parada(
                        lat = paradasCoords[index].first,
                        lng = paradasCoords[index].second,
                        descricao = descricao, // Mesma descrição do serviço
                        endereco_completo = parada.text
                    )
                } else null
            }.filterNotNull()

            // Criar request do serviço
            val servicoRequest = ServicoRequest(
                id_categoria = idCategoria,
                descricao = descricao,
                valor_adicional = 0.0, // Você pode adicionar um campo para isso depois
                origem_lat = origemLat,
                origem_lng = origemLng,
                origem_endereco = origemEndereco,
                destino_lat = destinoLat,
                destino_lng = destinoLng,
                destino_endereco = destinoEndereco,
                paradas = paradasAPI
            )

            // Fazer chamada à API
            val service = RetrofitFactory().getUserService()

            // Logs para debug
            Log.d("API_DEBUG", "Token sendo usado: Bearer ${token.take(30)}...")
            Log.d("API_DEBUG", "Categoria: $idCategoria")
            Log.d("API_DEBUG", "Descrição: $descricao")
            Log.d("API_DEBUG", "Origem: $origemEndereco ($origemLat, $origemLng)")
            Log.d("API_DEBUG", "Destino: $destinoEndereco ($destinoLat, $destinoLng)")
            Log.d("API_DEBUG", "Número de paradas: ${paradasAPI.size}")

            val response = service.criarServico("Bearer $token", servicoRequest)

            onLoadingChange(false)

            if (response.isSuccessful) {
                val servico = response.body()
                Log.d("API_DEBUG", "Serviço criado com sucesso! ID: ${servico?.id}")
                Toast.makeText(
                    context,
                    "Serviço criado com sucesso! ID: ${servico?.id}",
                    Toast.LENGTH_LONG
                ).show()

                // Navegar para tela home
                navController.navigate("tela_home") {
                    popUpTo("tela_montar_servico/{endereco}") { inclusive = true }
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("API_ERROR", "Código: ${response.code()}")
                Log.e("API_ERROR", "Mensagem: ${response.message()}")
                Log.e("API_ERROR", "Body: $errorBody")

                val mensagemErro = when (response.code()) {
                    403 -> "Acesso negado. Verifique se:\n1. Você completou seu perfil de contratante\n2. Seu token não expirou\n3. Você tem permissão para criar serviços"
                    401 -> "Token inválido ou expirado. Faça login novamente."
                    400 -> "Dados inválidos: $errorBody"
                    else -> "Erro ao criar serviço (${response.code()}): ${response.message()}"
                }

                onError(mensagemErro)
                Toast.makeText(
                    context,
                    mensagemErro,
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            onLoadingChange(false)
            Log.e("API_ERROR", "Exceção ao criar serviço", e)
            onError("Erro: ${e.message}")
            Toast.makeText(
                context,
                "Erro: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

