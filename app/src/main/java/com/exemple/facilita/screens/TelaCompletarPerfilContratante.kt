package com.exemple.facilita.screens

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.coroutines.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCompletarPerfilContratante(navController: NavController) {
    val context = LocalContext.current

    // Inicializa o Places API
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            val keyResId = context.resources.getIdentifier("google_maps_key", "string", context.packageName)
            val apiKey = if (keyResId != 0) context.getString(keyResId) else "SUA_API_KEY_AQUI"
            Places.initialize(context.applicationContext, apiKey, Locale("pt", "BR"))
        }
    }

    var endereco by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var necessidade by remember { mutableStateOf("") }

    val autocompleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            endereco = place.address.orEmpty()
        }
    }

    fun openAutocomplete() {
        val fields = listOf(
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.NAME
        )
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setCountries(listOf("BR"))
            .setTypesFilter(listOf("address"))
            .build(context)
        autocompleteLauncher.launch(intent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // FOTO DE PERFIL
        Image(
            painter = rememberAsyncImagePainter("https://i.pravatar.cc/150?img=5"), // coloque sua URL aqui
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Luiz da Silva",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1C1E)
        )

        Spacer(Modifier.height(32.dp))

        Text(
            "Complete seu perfil",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF000000)
        )

        Spacer(Modifier.height(20.dp))

        // CAMPO ENDEREÇO
        OutlinedTextField(
            value = endereco,
            onValueChange = { endereco = it },
            label = { Text("Endereço completo") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { openAutocomplete() },
            readOnly = true,
            shape = RoundedCornerShape(8.dp),
            colors = outlinedTextFieldColors()
        )

        Spacer(Modifier.height(16.dp))

        // CAMPO NECESSIDADE ESPECIAL (Dropdown)
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

        // CAMPO CPF
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
                .clickable { /* ação finalizar */ },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Finalizar",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
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
