package com.exemple.facilita.model

data class PedidoApi(
    val id: Int,
    val descricao: String,
    val status: String,
    val valor: Double,
    val data_solicitacao: String,
    val data_conclusao: String?,
    val categoria: Categoria?,
    val localizacao: Localizacao?,
    val prestador: Prestador?
)