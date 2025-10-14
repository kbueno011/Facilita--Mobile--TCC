package com.exemple.facilita.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.exemple.facilita.service.RetrofitFactory
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCompletarPerfilContratante(navController: NavController) {
    val context = LocalContext.current

    // Inicializa o Google Places API
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, "SUA_CHAVE_API_GOOGLE_PLACES_AQUI")
        }
    }

    val placesClient = remember { Places.createClient(context) }
    val sessionToken = remember { AutocompleteSessionToken.newInstance() }

    // Estados dos campos
    var endereco by remember { mutableStateOf("") }
    var sugestoes by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    var exibirSugestoes by remember { mutableStateOf(false) }

    var cpf by remember { mutableStateOf("") }
    var necessidade by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // Função para enviar os dados à API
    fun completarPerfil() {
        if (cpf.isBlank() || necessidade.isBlank() || endereco.isBlank()) {
            Toast.makeText(context, "Preencha todos os campos antes de continuar", Toast.LENGTH_SHORT).show()
            return
        }

        loading = true

        val request = CompletarPerfilRequest(
            id_localizacao = 1,
            necessidade = necessidade,
            cpf = cpf
        )

        val userService = RetrofitFactory().getUserService()
        val call = userService.cadastrarContratante(request)

        call.enqueue(object : Callback<CompletarPerfilResponse> {
            override fun onResponse(
                call: Call<CompletarPerfilResponse>,
                response: Response<CompletarPerfilResponse>
            ) {
                loading = false
                if (response.isSuccessful) {
                    Toast.makeText(context, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    navController.navigate("tela_home")
                } else {
                    Toast.makeText(context, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CompletarPerfilResponse>, t: Throwable) {
                loading = false
                Toast.makeText(context, "Falha de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(horizontal = 24.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // FOTO
        Image(
            painter = rememberAsyncImagePainter("https://i.pravatar.cc/150?img=7"),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Luiz da Silva",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1C1E)
        )

        Spacer(Modifier.height(28.dp))

        Text(
            "Complete seu perfil",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF000000)
        )

        Spacer(Modifier.height(24.dp))

        // ENDEREÇO com autocomplete igual ao da tela de cadastro
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
                            .addOnSuccessListener { response ->
                                sugestoes = response.autocompletePredictions
                            }
                            .addOnFailureListener {
                                sugestoes = emptyList()
                            }
                    } else {
                        sugestoes = emptyList()
                    }
                },
                label = { Text("Endereço") },
                placeholder = { Text("Digite seu endereço") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = outlinedTextFieldColors()
            )

            if (exibirSugestoes && sugestoes.isNotEmpty()) {
                LazyColumn(
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
                                }
                                .padding(12.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // NECESSIDADE ESPECIAL
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
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
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

        // CPF
        OutlinedTextField(
            value = cpf,
            onValueChange = {
                if (it.length <= 11 && it.all { c -> c.isDigit() }) cpf = it
            },
            label = { Text("Digite seu CPF") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = outlinedTextFieldColors()
        )

        Spacer(Modifier.height(40.dp))

        // BOTÃO FINALIZAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF00B14F), Color(0xFF007E32))
                    )
                )
                .clickable(enabled = !loading) { completarPerfil() },
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
            } else {
                Text(
                    text = "Finalizar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFFD1D5DB),
    unfocusedBorderColor = Color(0xFFE5E7EB),
    focusedLabelColor = Color(0xFF6B7280),
    unfocusedLabelColor = Color(0xFF9CA3AF),
    cursorColor = Color(0xFF00B14F),
    focusedTextColor = Color(0xFF1C1C1E),
    unfocusedTextColor = Color(0xFF1C1C1E)
)
