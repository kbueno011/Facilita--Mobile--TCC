package com.exemple.facilita.model

data class ServicoRequest(
    val id_categoria: Int,
    val descricao: String,
    val valor_adicional: Int,
    val origem_lat: Double,
    val origem_lng: Double,
    val destino_lat: Double,
    val destino_lng: Double
)
