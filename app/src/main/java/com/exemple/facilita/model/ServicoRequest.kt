package com.exemple.facilita.model

data class ServicoRequest(
    val id_categoria: Int,
    val descricao: String,
    val valor_adicional: Double,
    val origem_lat: Double,
    val origem_lng: Double,
    val origem_endereco: String,
    val destino_lat: Double,
    val destino_lng: Double,
    val destino_endereco: String,
    val paradas: List<Parada> = emptyList()
)
