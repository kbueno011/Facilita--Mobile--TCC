package com.exemple.facilita.screens

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.exemple.facilita.model.LocalizacaoRequest
import com.exemple.facilita.model.LocalizacaoResponse
import com.exemple.facilita.service.RetrofitFactory
import com.exemple.facilita.viewmodel.EnderecoViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private fun comp(comps: List<AddressComponent>, vararg keys: String): String {
    val set = keys.toSet()
    return comps.firstOrNull { c -> c.types.any { it in set } }?.name.orEmpty()
}

private fun shortLine(rua: String?, bairro: String?, cidade: String?, numero: String?): String {
    val partes = buildList {
        rua?.takeIf { it.isNotBlank() }?.let { add(it) }
        bairro?.takeIf { it.isNotBlank() }?.let { add(it) }
        cidade?.takeIf { it.isNotBlank() }?.let { add(it) }
        numero?.takeIf { it.isNotBlank() }?.let { add("Nº $it") }
    }
    return partes.joinToString(", ")
}

private suspend fun reverseGeocode(context: Context, lat: Double, lon: Double): android.location.Address? =
    withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale("pt", "BR"))
            @Suppress("DEPRECATION")
            geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()
        } catch (_: Exception) {
            null
        }
    }

@Composable
fun TelaEnderecoContent(
    navController: NavHostController,
    viewModel: EnderecoViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var campo by remember { mutableStateOf(viewModel.endereco.value) }

    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            val keyResId = context.resources.getIdentifier("google_maps_key", "string", context.packageName)
            val apiKey = if (keyResId != 0) context.getString(keyResId) else "SUA_API_KEY_AQUI"
            Places.initialize(context.applicationContext, apiKey, Locale("pt", "BR"))
        }
    }

    val start = LatLng(-23.5505, -46.6333)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(start, 15f)
    }

    var reverseJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(Unit) {
        snapshotFlow { cameraPositionState.isMoving }
            .debounce(300)
            .filter { !it }
            .map { cameraPositionState.position.target }
            .collectLatest { target ->
                reverseJob?.cancel()
                reverseJob = scope.launch {
                    val a = reverseGeocode(context, target.latitude, target.longitude)
                    if (a != null) {
                        val rua = a.thoroughfare ?: ""
                        val bairro = a.subLocality ?: ""
                        val cidade = a.locality ?: ""
                        val numero = a.subThoroughfare ?: ""
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

    val autocompleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { r ->
        if (r.resultCode == Activity.RESULT_OK && r.data != null) {
            val place = Autocomplete.getPlaceFromIntent(r.data!!)
            val comps = place.addressComponents?.asList().orEmpty()
            val numero = comp(comps, "street_number")
            val rua = comp(comps, "route").ifEmpty { place.name.orEmpty() }
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

            place.latLng?.let { latLng ->
                scope.launch {
                    cameraPositionState.animate(update = CameraUpdateFactory.newLatLngZoom(latLng, 17f))
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

    // ---- Lista de endereços recentes (como no código antigo) ----
    val recentes = listOf(
        Triple("Rua Vitória, Cohab 2", "São Paulo - SP", "Usado há 2h"),
        Triple("Rua Manaus, Cohab 2", "São Paulo - SP", "Usado ontem"),
        Triple("Rua Belém, Cohab 2", "Osasco - SP", "Usado há 3 dias"),
        Triple("Rua Paraná, Cohab 1", "Carapicuíba - SP", "Usado há 1 semana"),
        Triple("Avenida Brasil, Centro", "Barueri - SP", "Usado há 2 semanas"),
        Triple("Rua das Flores, Jardim Europa", "Cotia - SP", "Usado há 3 semanas"),
        Triple("Rua do Bosque, Vila Verde", "Santana de Parnaíba - SP", "Usado há 1 mês"),
        Triple("Rua Monte Alegre, Vila Nova", "Itapevi - SP", "Usado há 2 meses")
    )

    // ---- Layout ----
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F9F7))) {
        Column(modifier = Modifier.fillMaxSize().padding(bottom = 90.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                GoogleMap(
                    modifier = Modifier
                        .matchParentSize()
                        .shadow(6.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = false),
                    uiSettings = MapUiSettings(zoomControlsEnabled = false)
                )

                OutlinedTextField(
                    value = campo,
                    onValueChange = {},
                    placeholder = { Text("Buscar endereço", color = Color(0xFF6A6A6A)) },
                    singleLine = true,
                    enabled = false,
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF5D9C68)) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 40.dp)
                        .fillMaxWidth(0.9f)
                        .shadow(8.dp, RoundedCornerShape(35.dp))
                        .background(Color.White, RoundedCornerShape(35.dp))
                        .clickable { openAutocomplete() },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5D9C68),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )
            }

            Text(
                text = "Endereços Recentes",
                color = Color(0xFF4E7B5E),
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                modifier = Modifier.padding(start = 20.dp, top = 50.dp, bottom = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(recentes) { (endereco, cidade, tempo) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clickable { campo = endereco },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(5.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = Color(0xFF5D9C68),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = endereco,
                                    color = Color(0xFF2F2F2F),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(text = cidade, color = Color(0xFF6B6B6B), fontSize = 12.sp)
                            Text(text = tempo, color = Color(0xFF9C9C9C), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val service = RetrofitFactory().getUserService()
                val request = LocalizacaoRequest(
                    logradouro = viewModel.road.value,
                    numero = viewModel.houseNumber.value,
                    bairro = "",
                    cidade = viewModel.city.value,
                    cep = "",
                    latitude = cameraPositionState.position.target.latitude,
                    longitude = cameraPositionState.position.target.longitude
                )

                service.criarLocalizacao(request).enqueue(object : Callback<LocalizacaoResponse> {
                    override fun onResponse(call: Call<LocalizacaoResponse>, response: Response<LocalizacaoResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Endereço cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                            navController.navigate("tela_montar_servico/${viewModel.endereco.value}") {
                                popUpTo("tela_endereco") { inclusive = true }
                            }

                        } else {
                            Toast.makeText(context, "Erro ao cadastrar (${response.code()})", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LocalizacaoResponse>, t: Throwable) {
                        Toast.makeText(context, "Falha de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(85.dp)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF019D31), Color(0xFF06C755))
                        ),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cadastrar Endereço",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
