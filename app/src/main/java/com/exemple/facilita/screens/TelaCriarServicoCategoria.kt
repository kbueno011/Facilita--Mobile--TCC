package com.exemple.facilita.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.exemple.facilita.R
import com.exemple.facilita.model.*
import com.exemple.facilita.service.RetrofitFactory
import com.exemple.facilita.utils.TokenManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.launch

data class CategoriaInfo(
    val id: Int,
    val nome: String,
    val icone: Int,
    val cor: Color,
    val descricao: String
)

val categoriasMap = mapOf(
    "Farmácia" to CategoriaInfo(1, "Farmácia", R.drawable.farmacia, Color(0xFF4CAF50), "Buscar medicamentos e produtos"),
    "Correio" to CategoriaInfo(2, "Correio", R.drawable.correio, Color(0xFF2196F3), "Retirar e entregar encomendas"),
    "Mercado" to CategoriaInfo(3, "Mercado", R.drawable.mercado, Color(0xFFFF9800), "Compras de supermercado"),
    "Feira" to CategoriaInfo(4, "Feira", R.drawable.feira, Color(0xFF4CAF50), "Frutas, verduras e legumes"),
    "Hortifruti" to CategoriaInfo(5, "Hortifruti", R.drawable.hortifruti, Color(0xFF8BC34A), "Produtos frescos e naturais"),
    "Lavanderia" to CategoriaInfo(6, "Lavanderia", R.drawable.lavanderia, Color(0xFF00BCD4), "Lavar e passar roupas")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCriarServicoCategoria(
    navController: NavController,
    categoriaNome: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val categoria = categoriasMap[categoriaNome] ?: categoriasMap["Farmácia"]!!

    var descricao by remember { mutableStateOf("") }
    var origemEndereco by remember { mutableStateOf("") }
    var destinoEndereco by remember { mutableStateOf("") }
    var valorAdicional by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // Coordenadas
    var origemLat by remember { mutableStateOf(0.0) }
    var origemLng by remember { mutableStateOf(0.0) }
    var destinoLat by remember { mutableStateOf(0.0) }
    var destinoLng by remember { mutableStateOf(0.0) }

    // Paradas
    var paradas by remember { mutableStateOf<List<ParadaData>>(emptyList()) }
    var mostrarAddParada by remember { mutableStateOf(false) }

    // Google Places
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            try {
                Places.initialize(context, context.getString(R.string.google_maps_key))
            } catch (e: Exception) {
                Log.e("PLACES_API", "Erro ao inicializar", e)
            }
        }
    }

    val placesClient = remember { Places.createClient(context) }
    val sessionToken = remember { AutocompleteSessionToken.newInstance() }

    // Sugestões de autocomplete
    var sugestoesOrigem by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    var sugestoesDestino by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    var mostrarSugestoesOrigem by remember { mutableStateOf(false) }
    var mostrarSugestoesDestino by remember { mutableStateOf(false) }

    fun buscarSugestoes(query: String, onResult: (List<AutocompletePrediction>) -> Unit) {
        if (query.length > 2) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setSessionToken(sessionToken)
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    onResult(response.autocompletePredictions)
                }
                .addOnFailureListener {
                    onResult(emptyList())
                }
        } else {
            onResult(emptyList())
        }
    }

    fun obterDetalhesLugar(
        placeId: String,
        onSuccess: (Double, Double, String) -> Unit
    ) {
        val placeRequest = FetchPlaceRequest.builder(
            placeId,
            listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS)
        ).setSessionToken(sessionToken).build()

        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { response ->
                val place = response.place
                val lat = place.latLng?.latitude ?: 0.0
                val lng = place.latLng?.longitude ?: 0.0
                val endereco = place.address ?: ""
                onSuccess(lat, lng, endereco)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Erro ao obter detalhes do local", Toast.LENGTH_SHORT).show()
            }
    }

    fun criarServico() {
        if (origemEndereco.isEmpty() || destinoEndereco.isEmpty() || descricao.isEmpty()) {
            Toast.makeText(context, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        loading = true
        scope.launch {
            try {
                val token = TokenManager.obterToken(context)
                if (token == null) {
                    Toast.makeText(context, "Token não encontrado", Toast.LENGTH_SHORT).show()
                    loading = false
                    return@launch
                }

                val service = RetrofitFactory.userService
                val request = ServicoCategoriaRequest(
                    descricao_personalizada = descricao,
                    valor_adicional = valorAdicional.toDoubleOrNull() ?: 0.0,
                    origem_lat = origemLat,
                    origem_lng = origemLng,
                    origem_endereco = origemEndereco,
                    destino_lat = destinoLat,
                    destino_lng = destinoLng,
                    destino_endereco = destinoEndereco,
                    paradas = paradas.map {
                        ParadaServico(
                            lat = it.lat,
                            lng = it.lng,
                            descricao = it.descricao,
                            endereco_completo = it.endereco
                        )
                    }.takeIf { it.isNotEmpty() }
                )

                val response = service.criarServicoCategoria(
                    "Bearer $token",
                    categoria.id,
                    request
                )

                loading = false
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(context, "Serviço criado com sucesso!", Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Erro desconhecido"
                    Toast.makeText(context, "Erro: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Erro ao criar serviço: $errorMsg")
                }
            } catch (e: Exception) {
                loading = false
                Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", "Exceção", e)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = categoria.cor
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header com ícone da categoria
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(categoria.cor, categoria.cor.copy(alpha = 0.7f))
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = categoria.icone),
                                        contentDescription = categoria.nome,
                                        modifier = Modifier.size(40.dp),
                                        tint = Color.White
                                    )
                                }

                                Column {
                                    Text(
                                        text = categoria.nome,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = categoria.descricao,
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Descrição do serviço
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "O que você precisa?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D2D2D)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = descricao,
                                onValueChange = { descricao = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Ex: Comprar remédios da receita") },
                                minLines = 3,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = categoria.cor,
                                    unfocusedBorderColor = Color(0xFFE0E0E0)
                                )
                            )
                        }
                    }
                }

                // Endereços
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Endereços",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D2D2D)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Origem
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(categoria.cor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = categoria.cor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Origem",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box {
                                        OutlinedTextField(
                                            value = origemEndereco,
                                            onValueChange = {
                                                origemEndereco = it
                                                mostrarSugestoesOrigem = true
                                                buscarSugestoes(it) { sugestoesOrigem = it }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            placeholder = { Text("Digite o endereço de origem", fontSize = 14.sp) },
                                            shape = RoundedCornerShape(12.dp),
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = categoria.cor,
                                                unfocusedBorderColor = Color(0xFFE0E0E0)
                                            )
                                        )

                                        if (mostrarSugestoesOrigem && sugestoesOrigem.isNotEmpty()) {
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 60.dp),
                                                elevation = CardDefaults.cardElevation(4.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color.White)
                                            ) {
                                                Column {
                                                    sugestoesOrigem.take(3).forEach { prediction ->
                                                        Text(
                                                            text = prediction.getFullText(null).toString(),
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable {
                                                                    origemEndereco = prediction.getFullText(null).toString()
                                                                    mostrarSugestoesOrigem = false
                                                                    obterDetalhesLugar(prediction.placeId) { lat, lng, end ->
                                                                        origemLat = lat
                                                                        origemLng = lng
                                                                        origemEndereco = end
                                                                    }
                                                                }
                                                                .padding(12.dp),
                                                            fontSize = 14.sp
                                                        )
                                                        if (prediction != sugestoesOrigem.take(3).last()) {
                                                            Divider()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Destino
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF5252).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Destino",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box {
                                        OutlinedTextField(
                                            value = destinoEndereco,
                                            onValueChange = {
                                                destinoEndereco = it
                                                mostrarSugestoesDestino = true
                                                buscarSugestoes(it) { sugestoesDestino = it }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            placeholder = { Text("Digite o endereço de destino", fontSize = 14.sp) },
                                            shape = RoundedCornerShape(12.dp),
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = categoria.cor,
                                                unfocusedBorderColor = Color(0xFFE0E0E0)
                                            )
                                        )

                                        if (mostrarSugestoesDestino && sugestoesDestino.isNotEmpty()) {
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 60.dp),
                                                elevation = CardDefaults.cardElevation(4.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color.White)
                                            ) {
                                                Column {
                                                    sugestoesDestino.take(3).forEach { prediction ->
                                                        Text(
                                                            text = prediction.getFullText(null).toString(),
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable {
                                                                    destinoEndereco = prediction.getFullText(null).toString()
                                                                    mostrarSugestoesDestino = false
                                                                    obterDetalhesLugar(prediction.placeId) { lat, lng, end ->
                                                                        destinoLat = lat
                                                                        destinoLng = lng
                                                                        destinoEndereco = end
                                                                    }
                                                                }
                                                                .padding(12.dp),
                                                            fontSize = 14.sp
                                                        )
                                                        if (prediction != sugestoesDestino.take(3).last()) {
                                                            Divider()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Paradas (se houver)
                if (paradas.isNotEmpty()) {
                    itemsIndexed(paradas) { index, parada ->
                        ParadaCard(
                            parada = parada,
                            numero = index + 1,
                            cor = categoria.cor,
                            onRemove = {
                                paradas = paradas.filterIndexed { i, _ -> i != index }
                            }
                        )
                    }
                }

                // Botão adicionar parada
                if (paradas.size < 3) {
                    item {
                        OutlinedButton(
                            onClick = { mostrarAddParada = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, categoria.cor.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = categoria.cor
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Adicionar parada (${paradas.size}/3)")
                        }
                    }
                }

                // Valor adicional
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        text = "Gorjeta (Opcional)",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2D2D2D)
                                    )
                                    Text(
                                        text = "Valorize o prestador",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFA726)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = valorAdicional,
                                onValueChange = { valorAdicional = it.filter { c -> c.isDigit() || c == '.' } },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("R$ 0,00") },
                                leadingIcon = {
                                    Text("R$", fontWeight = FontWeight.Bold)
                                },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = categoria.cor,
                                    unfocusedBorderColor = Color(0xFFE0E0E0)
                                )
                            )
                        }
                    }
                }

                // Botão criar serviço
                item {
                    Button(
                        onClick = { criarServico() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = categoria.cor
                        ),
                        enabled = !loading
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Criar Serviço",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Dialog adicionar parada
            if (mostrarAddParada) {
                DialogAdicionarParada(
                    categoria = categoria,
                    placesClient = placesClient,
                    sessionToken = sessionToken,
                    onDismiss = { mostrarAddParada = false },
                    onAdd = { novaParada ->
                        paradas = paradas + novaParada
                        mostrarAddParada = false
                    }
                )
            }
        }
    }
}

data class ParadaData(
    val endereco: String,
    val descricao: String,
    val lat: Double,
    val lng: Double
)

@Composable
fun ParadaCard(
    parada: ParadaData,
    numero: Int,
    cor: Color,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(cor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = numero.toString(),
                        fontWeight = FontWeight.Bold,
                        color = cor
                    )
                }

                Column {
                    Text(
                        text = parada.descricao,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = parada.endereco,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remover",
                    tint = Color(0xFFFF5252)
                )
            }
        }
    }
}

@Composable
fun DialogAdicionarParada(
    categoria: CategoriaInfo,
    placesClient: com.google.android.libraries.places.api.net.PlacesClient,
    sessionToken: AutocompleteSessionToken,
    onDismiss: () -> Unit,
    onAdd: (ParadaData) -> Unit
) {
    var endereco by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf(0.0) }
    var lng by remember { mutableStateOf(0.0) }
    var sugestoes by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    var mostrarSugestoes by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Parada") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("O que fazer aqui?") },
                    placeholder = { Text("Ex: Buscar encomenda") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Box {
                    OutlinedTextField(
                        value = endereco,
                        onValueChange = {
                            endereco = it
                            mostrarSugestoes = true
                            if (it.length > 2) {
                                val request = FindAutocompletePredictionsRequest.builder()
                                    .setQuery(it)
                                    .setSessionToken(sessionToken)
                                    .build()

                                placesClient.findAutocompletePredictions(request)
                                    .addOnSuccessListener { response ->
                                        sugestoes = response.autocompletePredictions
                                    }
                            }
                        },
                        label = { Text("Endereço") },
                        placeholder = { Text("Digite o endereço") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (mostrarSugestoes && sugestoes.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 60.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column {
                                sugestoes.take(3).forEach { prediction ->
                                    Text(
                                        text = prediction.getFullText(null).toString(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                endereco = prediction.getFullText(null).toString()
                                                mostrarSugestoes = false

                                                val placeRequest = FetchPlaceRequest.builder(
                                                    prediction.placeId,
                                                    listOf(Place.Field.LAT_LNG)
                                                ).setSessionToken(sessionToken).build()

                                                placesClient.fetchPlace(placeRequest)
                                                    .addOnSuccessListener { response ->
                                                        lat = response.place.latLng?.latitude ?: 0.0
                                                        lng = response.place.latLng?.longitude ?: 0.0
                                                    }
                                            }
                                            .padding(12.dp),
                                        fontSize = 14.sp
                                    )
                                    if (prediction != sugestoes.take(3).last()) {
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (endereco.isNotEmpty() && descricao.isNotEmpty()) {
                        onAdd(ParadaData(endereco, descricao, lat, lng))
                    } else {
                        Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = categoria.cor)
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

