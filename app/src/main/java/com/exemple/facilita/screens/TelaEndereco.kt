package com.exemple.facilita.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.Manifest
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.exemple.facilita.model.NominatimResult
import com.exemple.facilita.service.NominatimApi
import com.exemple.facilita.viewmodel.EnderecoViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

// --- Função para obter localização atual ---
@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context): Pair<Double, Double>? {
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasPermission) return null

    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    val location = fusedLocationClient.lastLocation.await()
    return location?.let { it.latitude to it.longitude }
}

// --- Tela de endereço ---
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TelaEnderecoContent(
    navController: NavHostController,
    viewModel: EnderecoViewModel,
    nominatimApi: NominatimApi
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val sugestoes = remember { mutableStateListOf<NominatimResult>() }
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    // --- Permissão ---
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        } else {
            userLocation = getCurrentLocation(context)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- MAPA (área superior) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) { }

            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "MAPA AQUI",
                    modifier = Modifier.align(Alignment.Center)
                )

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Buscar endereço") },
                    singleLine = true,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        // --- BUSCA COM PROXIMIDADE ---
        LaunchedEffect(query, userLocation) {
            if (query.length > 3 && userLocation != null) {
                try {
                    val (lat, lon) = userLocation!!
                    val results = nominatimApi.searchAddress(
                        query = query + "&lat=$lat&lon=$lon"
                    )
                    sugestoes.clear()
                    sugestoes.addAll(results)
                } catch (e: Exception) {
                    e.printStackTrace()
                    sugestoes.clear()
                }
            } else {
                sugestoes.clear()
            }
        }

        // --- LISTA DE RESULTADOS ---
        if (sugestoes.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                items(sugestoes) { endereco ->
                    EnderecoItem(endereco) {
                        viewModel.atualizarEndereco(
                            house = endereco.address?.house_number ?: "",
                            roadName = endereco.address?.road ?: "",
                            cityName = endereco.address?.city ?: "",
                            display = endereco.display_name
                        )
                        query = endereco.display_name
                        sugestoes.clear()
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Recentes",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                listOf(
                    "Rua Vitória, cohab 2, Carapicuíba",
                    "Rua Manaus, cohab 2, Carapicuíba",
                    "Rua Belem, cohab 2, Carapicuíba"
                ).forEach { endereco ->
                    EnderecoItemSimples(titulo = endereco) {
                        query = endereco
                    }
                }
            }
        }
    }
}

// --- Composable para item de endereço ---
@Composable
fun EnderecoItem(endereco: NominatimResult, onClick: () -> Unit) {
    val titulo = buildString {
        val road = endereco.address?.road ?: ""
        append(road)
        endereco.address?.house_number?.let { if (road.isNotEmpty()) append(", $it") else append(it) }
    }.ifBlank { endereco.display_name }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF019D31)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = endereco.display_name,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        Divider(modifier = Modifier.padding(top = 10.dp))
    }
}

@Composable
fun EnderecoItemSimples(titulo: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF019D31)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Divider(modifier = Modifier.padding(top = 10.dp))
    }
}
