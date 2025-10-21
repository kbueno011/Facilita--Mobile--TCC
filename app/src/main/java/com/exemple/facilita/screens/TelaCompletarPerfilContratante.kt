package com.exemple.facilita.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.exemple.facilita.model.CompletarPerfilRequest
import com.exemple.facilita.model.CompletarPerfilResponse
import com.exemple.facilita.model.LocalizacaoRequest
import com.exemple.facilita.model.LocalizacaoResponse
import com.exemple.facilita.service.RetrofitFactory
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCompletarPerfilContratante(navController: NavController) {
    val context = LocalContext.current
    val nomeUsuario by remember { mutableStateOf(getNomeUsuario(context)) }
    val tokenUsuario by remember { mutableStateOf(getTokenFromPreferences(context)) }

    // Inicializa Google Places
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, "SUA_API_KEY_AQUI")
        }
    }

    val placesClient = remember { Places.createClient(context) }
    val sessionToken = remember { AutocompleteSessionToken.newInstance() }

    // Estados do formulário
    var endereco by remember { mutableStateOf("") }
    var sugestoes by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    var exibirSugestoes by remember { mutableStateOf(false) }

    var cpf by remember { mutableStateOf("") }
    var necessidade by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // Dados detalhados do endereço
    var logradouro by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var bairro by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf("") }
    var cep by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }

    val service = RetrofitFactory().getUserService()

    fun enviarDados() {
        if (cpf.isBlank() || necessidade.isBlank() || endereco.isBlank()) {
            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        loading = true

        if (logradouro.isBlank()) logradouro = endereco
        if (cidade.isBlank()) cidade = "Não informada"

        // 1️⃣ POST endereço
        val localRequest = LocalizacaoRequest(
            logradouro = logradouro,
            numero = numero,
            bairro = bairro,
            cidade = cidade,
            cep = cep,
            latitude = latitude,
            longitude = longitude
        )

        service.criarLocalizacao(localRequest).enqueue(object : Callback<LocalizacaoResponse> {
            override fun onResponse(call: Call<LocalizacaoResponse>, response: Response<LocalizacaoResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val idEndereco = response.body()!!.id

                    // 2️⃣ POST completar perfil com token
                    val perfilRequest = CompletarPerfilRequest(
                        id_localizacao = idEndereco,
                        cpf = cpf,
                        necessidade = necessidade.uppercase()
                    )

                    service.cadastrarContratante("Bearer $tokenUsuario", perfilRequest)
                        .enqueue(object : Callback<CompletarPerfilResponse> {
                            override fun onResponse(
                                call: Call<CompletarPerfilResponse>,
                                response: Response<CompletarPerfilResponse>
                            ) {
                                loading = false
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("tela_home") {
                                        popUpTo("tela_completar_perfil") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<CompletarPerfilResponse>, t: Throwable) {
                                loading = false
                                Toast.makeText(context, "Falha ao completar perfil: ${t.message}", Toast.LENGTH_LONG).show()
                            }
                        })
                } else {
                    loading = false
                    Toast.makeText(context, "Erro ao cadastrar endereço", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LocalizacaoResponse>, t: Throwable) {
                loading = false
                Toast.makeText(context, "Falha de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(horizontal = 24.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter("https://i.pravatar.cc/150?img=7"),
            contentDescription = "Foto de perfil",
            modifier = Modifier.size(100.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = nomeUsuario.ifEmpty { "Usuário" },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1C1E)
        )

        Spacer(Modifier.height(28.dp))
        Text("Complete seu perfil", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(24.dp))

        // Campo Endereço com autocomplete
        Box {
            OutlinedTextField(
                value = endereco,
                onValueChange = {
                    endereco = it
                    exibirSugestoes = true
                    if (it.length > 2) {
                        val request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(it)
                            .setSessionToken(sessionToken)
                            .build()
                        placesClient.findAutocompletePredictions(request)
                            .addOnSuccessListener { response -> sugestoes = response.autocompletePredictions }
                            .addOnFailureListener { sugestoes = emptyList() }
                    } else {
                        sugestoes = emptyList()
                    }
                },
                label = { Text("Endereço") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = outlinedTextFieldColors()
            )

            if (exibirSugestoes && sugestoes.isNotEmpty()) {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 4.dp)
                ) {
                    items(sugestoes) { prediction ->
                        Text(
                            text = prediction.getFullText(null).toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    endereco = prediction.getFullText(null).toString()
                                    exibirSugestoes = false
                                    sugestoes = emptyList()

                                    val placeId = prediction.placeId
                                    val placeRequest = FetchPlaceRequest.builder(
                                        placeId,
                                        listOf(Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG)
                                    ).setSessionToken(sessionToken).build()

                                    placesClient.fetchPlace(placeRequest)
                                        .addOnSuccessListener { result ->
                                            val place = result.place
                                            val components = place.addressComponents
                                            latitude = place.latLng?.latitude ?: 0.0
                                            longitude = place.latLng?.longitude ?: 0.0
                                            components?.asList()?.forEach { comp ->
                                                when {
                                                    comp.types.contains("route") -> logradouro = comp.name
                                                    comp.types.contains("street_number") -> numero = comp.name
                                                    comp.types.contains("sublocality") || comp.types.contains("neighborhood") -> bairro = comp.name
                                                    comp.types.contains("locality") -> cidade = comp.name
                                                    comp.types.contains("postal_code") -> cep = comp.name
                                                }
                                            }
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

        Spacer(Modifier.height(16.dp))

        // Campo Necessidade
        var expanded by remember { mutableStateOf(false) }
        val opcoes = listOf("Nenhuma", "Idoso", "PcD", "Gestante")
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = necessidade,
                onValueChange = {},
                readOnly = true,
                label = { Text("Necessidades Especiais") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = outlinedTextFieldColors()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                opcoes.forEach { opcao ->
                    DropdownMenuItem(
                        text = { Text(opcao) },
                        onClick = {
                            necessidade = opcao
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Campo CPF
        OutlinedTextField(
            value = cpf,
            onValueChange = { if (it.length <= 11 && it.all { c -> c.isDigit() }) cpf = it },
            label = { Text("Digite seu CPF") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = outlinedTextFieldColors()
        )

        Spacer(Modifier.height(40.dp))

        // Botão Finalizar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(50))
                .background(brush = Brush.horizontalGradient(listOf(Color(0xFF00B14F), Color(0xFF007E32))))
                .clickable(enabled = !loading) { enviarDados() },
            contentAlignment = Alignment.Center
        ) {
            if (loading)
                CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
            else
                Text("Finalizar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// Funções utilitárias
fun getNomeUsuario(context: Context): String {
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return sharedPref.getString("nomeUsuario", "") ?: ""
}
fun getTokenFromPreferences(context: Context): String {
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return sharedPref.getString("auth_token", "") ?: ""
}

@Composable
fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFFD1D5DB),
    unfocusedBorderColor = Color(0xFFE5E7EB),
    focusedLabelColor = Color(0xFF6B7280),
    unfocusedLabelColor = Color(0xFF9CA3AF)
)
