package com.exemple.facilita.screens

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.exemple.facilita.viewmodel.EnderecoViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import android.location.Address as AndroidAddress

// ---------- Helpers ----------
private fun comp(comps: List<AddressComponent>, vararg keys: String): String {
    val set = keys.toSet()
    return comps.firstOrNull { c -> c.types.any { it in set } }?.name.orEmpty()
}

private fun shortLine(rua: String?, bairro: String?, cidade: String?, numero: String?): String {
    val partes = buildList {
        rua?.trim()?.takeIf { it.isNotEmpty() }?.let { add(it) }
        bairro?.trim()?.takeIf { it.isNotEmpty() }?.let { add(it) }
        cidade?.trim()?.takeIf { it.isNotEmpty() }?.let { add(it) }
        numero?.trim()?.takeIf { it.isNotEmpty() }?.let { add("Nº $it") }
    }
    return partes.joinToString(", ")
}

private suspend fun reverseGeocode(
    context: Context,
    lat: Double,
    lon: Double
): AndroidAddress? = withContext(Dispatchers.IO) {
    try {
        val geocoder = Geocoder(context, Locale("pt", "BR"))
        @Suppress("DEPRECATION")
        val list = geocoder.getFromLocation(lat, lon, 1)
        if (!list.isNullOrEmpty()) list[0] else null
    } catch (_: Exception) {
        null
    }
}

private fun ruaFrom(addr: AndroidAddress): String =
    addr.thoroughfare ?: addr.featureName ?: ""

private fun numeroFrom(addr: AndroidAddress): String =
    addr.subThoroughfare ?: ""

private fun bairroFrom(addr: AndroidAddress): String =
    addr.subLocality ?: addr.subAdminArea ?: ""

private fun cidadeFrom(addr: AndroidAddress): String =
    addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: ""

// ---------- Tela ----------
@Composable
fun TelaEnderecoContent(
    navController: NavHostController,
    viewModel: EnderecoViewModel
) {
    val context = LocalContext.current

    // Inicializa Places uma vez (usa sua API key do strings.xml)
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            val keyResId = context.resources.getIdentifier("google_maps_key", "string", context.packageName)
            val apiKey = if (keyResId != 0) context.getString(keyResId) else "SUA_API_KEY_AQUI"
            Places.initialize(context.applicationContext, apiKey, Locale("pt", "BR"))
        }
    }

    var campo by remember { mutableStateOf(viewModel.endereco.value) }

    // ----- Mapa (Compose) -----
    val start = LatLng(-23.5505, -46.6333) // São Paulo como padrão
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(start, 15f)
    }

    val scope = rememberCoroutineScope()
    var reverseJob by remember { mutableStateOf<Job?>(null) }

    // Quando o usuário PARA de mover o mapa, faz reverse geocode com Geocoder
    LaunchedEffect(Unit) {
        snapshotFlow { cameraPositionState.isMoving }
            .debounce(300)
            .filter { moving -> !moving }
            .map { cameraPositionState.position.target }
            .collectLatest { target ->
                reverseJob?.cancel()
                reverseJob = scope.launch {
                    val a = reverseGeocode(context, target.latitude, target.longitude)
                    if (a != null) {
                        val rua = ruaFrom(a)
                        val bairro = bairroFrom(a)
                        val cidade = cidadeFrom(a)
                        val numero = numeroFrom(a)
                        val curto = shortLine(rua, bairro, cidade, numero)
                            .ifBlank { a.getAddressLine(0) ?: "" }

                        viewModel.road.value = rua
                        viewModel.city.value = cidade
                        viewModel.houseNumber.value = numero
                        viewModel.displayName.value = curto
                        viewModel.endereco.value = curto
                        campo = curto
                    }
                }
            }
    }

    // Autocomplete (overlay) — digita “rua + número”, escolhe e centraliza a câmera
    val autocompleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { r ->
        if (r.resultCode == Activity.RESULT_OK && r.data != null) {
            val place = Autocomplete.getPlaceFromIntent(r.data!!)
            val comps = place.addressComponents?.asList().orEmpty()

            val numero = comp(comps, "street_number")
            val rua    = comp(comps, "route").ifEmpty { place.name.orEmpty() }
            val bairro = comp(comps, "sublocality_level_1", "sublocality", "neighborhood")
            val cidade = comp(comps, "locality", "administrative_area_level_2")

            val curto = shortLine(rua, bairro, cidade, numero)
                .ifBlank { place.address.orEmpty() }

            viewModel.road.value = rua
            viewModel.houseNumber.value = numero
            viewModel.city.value = cidade
            viewModel.displayName.value = curto
            viewModel.endereco.value = curto
            campo = curto

            place.latLng?.let {
                scope.launch {
                    cameraPositionState.animate(
                        update = { position = CameraPosition.fromLatLngZoom(it, 17f) }
                    )
                }
            }
        }
    }

    fun openAutocomplete() {
        val fields = listOf(
            Place.Field.ADDRESS_COMPONENTS,
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

    // ----- UI -----
    Column(Modifier.fillMaxSize()) {

        // Mapa (topo)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = false // habilite se for tratar permissão
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                )
            )

            // Pino central fixo
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF019D31),
                modifier = Modifier
                    .align(Alignment.Center)
                    .zIndex(2f)
            )

            // Campo "Buscar endereço" sobre o mapa
            OutlinedTextField(
                value = campo,
                onValueChange = { /* somente leitura; digitação no overlay */ },
                placeholder = { Text("Buscar endereço") },
                singleLine = true,
                enabled = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF9AA0A6)
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { openAutocomplete() }
                    .zIndex(3f)
            )
        }

        // Título
        Text(
            text = "Escolha o endereço para receber o pedido",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // Recentes (exemplo)
        Text("Recentes", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(
                listOf(
                    "Rua Vitória, cohab 2, Carapicuíba",
                    "Rua Manaus, cohab 2, Carapicuíba",
                    "Rua Belem, cohab 2, Carapicuíba",
                    "Rua Paraná, cohab 1, Carapicuíba"
                )
            ) { t ->
                RecentRow(t) {
                    campo = t
                    openAutocomplete()
                }
            }
        }
    }
}


@Composable
private fun RecentRow(text: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF019D31))
            Spacer(Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
        }
        Divider(Modifier.padding(top = 10.dp))
    }
}
