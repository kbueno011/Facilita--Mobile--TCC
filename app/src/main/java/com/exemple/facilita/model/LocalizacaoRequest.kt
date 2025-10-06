package com.exemple.facilita.model

data class LocalizacaoRequest(
    val logradouro: String,
    val numero: String,
    val bairro: String,
    val cidade: String,
    val cep: String,
    val latitude: Double,
    val longitude: Double
)