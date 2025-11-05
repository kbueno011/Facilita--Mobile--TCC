package com.exemple.facilita.model
import java.io.Serializable

data class DetalhesCalculo(
    val valor_base: Double,
    val valor_adicional: Double,
    val valor_distancia: Double,
    val valor_total: Double,
    val distancia_km: Double? = null,
    val detalhes: DetalhesInternos? = null
): Serializable