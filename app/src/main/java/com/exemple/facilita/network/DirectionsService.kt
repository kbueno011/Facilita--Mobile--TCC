package com.exemple.facilita.network

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object DirectionsService {
    private const val TAG = "DirectionsService"

    // Chave da API do Google Maps (Directions API)
    // MESMA chave do Google Maps usado no app
    private const val API_KEY = "AIzaSyBKFwfrLdbTreqsOwnpMS9-zt9KD-HEH28"

    data class RouteResult(
        val points: List<LatLng>,
        val distanceMeters: Int,
        val durationSeconds: Int,
        val distanceText: String,
        val durationText: String
    )

    /**
     * Busca a rota entre dois pontos usando Google Directions API
     * Com suporte a waypoints (paradas intermediárias)
     */
    suspend fun getRoute(
        origin: LatLng,
        destination: LatLng,
        waypoints: List<LatLng> = emptyList()
    ): RouteResult? = withContext(Dispatchers.IO) {
        try {
            val originStr = "${origin.latitude},${origin.longitude}"
            val destinationStr = "${destination.latitude},${destination.longitude}"

            // Adiciona waypoints se existirem
            val waypointsStr = if (waypoints.isNotEmpty()) {
                val waypointsFormatted = waypoints.joinToString("|") {
                    "${it.latitude},${it.longitude}"
                }
                "&waypoints=optimize:false|$waypointsFormatted"
            } else {
                ""
            }

            val urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=$originStr" +
                    "&destination=$destinationStr" +
                    waypointsStr +
                    "&mode=driving" +
                    "&key=$API_KEY"

            Log.d(TAG, "🗺️ Buscando rota: $originStr -> ${waypoints.size} paradas -> $destinationStr")
            Log.d(TAG, "🔗 URL: $urlString")

            val response = URL(urlString).readText()
            Log.d(TAG, "📥 Resposta recebida (${response.length} chars)")

            val json = JSONObject(response)
            val status = json.getString("status")

            Log.d(TAG, "📊 Status da API: $status")

            if (status != "OK") {
                Log.e(TAG, "❌ Erro na API: $status")
                if (json.has("error_message")) {
                    Log.e(TAG, "   Mensagem: ${json.getString("error_message")}")
                }
                Log.e(TAG, "   Resposta completa: $response")
                return@withContext null
            }

            val routes = json.getJSONArray("routes")
            if (routes.length() == 0) {
                Log.e(TAG, "❌ Nenhuma rota encontrada")
                return@withContext null
            }

            val route = routes.getJSONObject(0)
            val overviewPolyline = route.getJSONObject("overview_polyline")
            val encodedPoints = overviewPolyline.getString("points")

            // Decodifica os pontos da polyline
            val points = decodePolyline(encodedPoints)

            // Obtém informações de distância e duração
            val legs = route.getJSONArray("legs")
            val leg = legs.getJSONObject(0)

            val distance = leg.getJSONObject("distance")
            val distanceMeters = distance.getInt("value")
            val distanceText = distance.getString("text")

            val duration = leg.getJSONObject("duration")
            val durationSeconds = duration.getInt("value")
            val durationText = duration.getString("text")

            Log.d(TAG, "✅ Rota encontrada: ${points.size} pontos, $distanceText, $durationText")

            RouteResult(
                points = points,
                distanceMeters = distanceMeters,
                durationSeconds = durationSeconds,
                distanceText = distanceText,
                durationText = durationText
            )

        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao buscar rota: ${e.message}")
            Log.e(TAG, "❌ Tipo: ${e.javaClass.simpleName}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Decodifica uma polyline encodada do Google Maps
     */
    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0

            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0

            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(latLng)
        }

        return poly
    }
}

