package com.exemple.facilita.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.exemple.facilita.service.NominatimApi
import com.exemple.facilita.viewmodel.EnderecoViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.Locale

// ---- Helpers ----
private fun comp(components: List<AddressComponent>, vararg keys: String): String {
    val set = keys.toSet()
    return components.firstOrNull { c -> c.types.any { it in set } }?.name.orEmpty()
}

private fun bairroFrom(comps: List<AddressComponent>): String =
    comp(comps, "sublocality_level_1", "sublocality", "neighborhood")

private fun cidadeFrom(comps: List<AddressComponent>): String =
    comp(comps, "locality", "administrative_area_level_2")

private fun formatRuaBairroCidadeNumero(
    rua: String?, bairro: String?, cidade: String?, numero: String?
): String {
    val partes = buildList {
        rua?.trim()?.takeIf { it.isNotEmpty() }?.let { add(it) }
        bairro?.trim()?.takeIf { it.isNotEmpty() }?.let { add(it) }
        cidade?.trim()?.takeIf { it.isNotEmpty() }?.let { add(it) }
        numero?.trim()?.takeIf { it.isNotEmpty() }?.let { add("Nº $it") }
    }
    return partes.joinToString(", ")
}

@Composable
fun TelaEnderecoContent(
    navController: NavHostController,
    viewModel: EnderecoViewModel,
    nominatimApi: NominatimApi // não usado nesta versão
) {
    val context = LocalContext.current

    // Inicializa o Places (use a key do strings.xml)
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            val keyResId = context.resources.getIdentifier("google_maps_key", "string", context.packageName)
            val apiKey = if (keyResId != 0) context.getString(keyResId) else "SUA_API_KEY_AQUI"
            Places.initialize(context.applicationContext, apiKey, Locale("pt", "BR"))
        }
    }

    var enderecoCurto by remember { mutableStateOf(viewModel.endereco.value) }

    // Launcher do Autocomplete (overlay)
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            val comps = place.addressComponents?.asList().orEmpty()

            val numero = comp(comps, "street_number")
            val rua    = comp(comps, "route").ifEmpty { place.name.orEmpty() }
            val bairro = bairroFrom(comps)
            val cidade = cidadeFrom(comps)

            val curto = formatRuaBairroCidadeNumero(rua, bairro, cidade, numero)
                .ifBlank { place.address.orEmpty() }

            // Preenche o ViewModel no seu formato
            viewModel.road.value = rua
            viewModel.houseNumber.value = numero
            viewModel.city.value = cidade
            viewModel.displayName.value = curto
            viewModel.endereco.value = curto

            enderecoCurto = curto
        }
    }

    // Intenção do Autocomplete (BR + endereços)
    fun openAutocomplete() {
        val fields = listOf(
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.NAME
        )
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setCountries(listOf("BR"))
            // TypeFilter.ADDRESS no Intent usa a API nova com lista de strings:
            .setTypesFilter(listOf(TypeFilter.ADDRESS.toString().lowercase()))
            .build(context)
        launcher.launch(intent)
    }

    // ---- UI simples: um campo "Toque para buscar" + linha com endereço escolhido ----
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Endereço", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = enderecoCurto,
            onValueChange = { /* leitura somente; o overlay cuida da digitação */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { openAutocomplete() },
            enabled = false,
            placeholder = { Text("Digite rua e número (ex.: Av. Paulista 1200)") },
            leadingIcon = {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF019D31))
            },
            supportingText = { Text("Toque para procurar", color = Color.Gray) }
        )

        Spacer(Modifier.height(24.dp))

        if (enderecoCurto.isNotBlank()) {
            Text("Endereço selecionado:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF019D31))
                Spacer(Modifier.width(8.dp))
                Text(enderecoCurto, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
            }
        }
    }
}
