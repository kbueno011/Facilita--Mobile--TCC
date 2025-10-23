package com.exemple.facilita.model
import java.io.Serializable
data class DetalhesCalculo(
    val valor_base: Int,
    val valor_adicional: Int,
    val valor_distancia: Int,
    val valor_total: Int,
    val detalhes: DetalhesInternos
): Serializable