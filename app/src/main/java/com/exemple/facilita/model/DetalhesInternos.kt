package com.exemple.facilita.model
import java.io.Serializable
data class DetalhesInternos(
    val categoria: Int,
    val distancia_km: Double,
    val tarifa_por_km: Double,
    val valor_minimo: Double
): Serializable