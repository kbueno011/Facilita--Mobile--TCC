package com.exemple.facilita.network

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object DirectionsService {
    private const val TAG = "DirectionsService"

    // Sua chave da API do Google Maps
    // IMPORTANTE: Coloque sua chave real aqui ou no local.properties
    private const val API_KEY = "AIzaSyBpDzK-NLdG9TxvqOcjvzlr5xKXg0XGXkY"

    data class RouteResult(
        val points: List<LatLng>,
        val distanceMeters: Int,
        val durationSeconds: Int,
        val distanceText: String,
        val durationText: String
    )

    /**
     * Busca a rota entre dois pontos usando Google Directions API
     */
    suspend fun getRoute(
        origin: LatLng,
        destination: LatLng
    ): RouteResult? = withContext(Dispatchers.IO) {
        try {
            val originStr = "${origin.latitude},${origin.longitude}"
            val destinationStr = "${destination.latitude},${destination.longitude}"

            val urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=$originStr" +
                    "&destination=$destinationStr" +
                    "&mode=driving" +
                    "&key=$API_KEY"

            Log.d(TAG, "🗺️ Buscando rota: $originStr -> $destinationStr")

            val response = URL(urlString).readText()
            val json = JSONObject(response)

            val status = json.getString("status")

            if (status != "OK") {
                Log.e(TAG, "❌ Erro na API: $status")
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
            Log.e(TAG, "❌ Erro ao buscar rota", e)
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

