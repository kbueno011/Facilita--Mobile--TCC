package com.exemple.facilita.model

import java.io.Serializable

data class ServicoResponse(
    val id: Int?,
    val descricao: String?,
    val valor: Double?,
    val status: String?,
    val categoria: Categoria?,
    val contratante: Contratante?,
    val detalhes_valor: DetalhesValor?
) : Serializable
