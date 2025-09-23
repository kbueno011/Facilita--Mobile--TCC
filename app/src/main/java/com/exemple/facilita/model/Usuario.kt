package com.exemple.facilita.model

data class Usuario(
    val id: Int,
    val nome: String,
    val senha_hash: String,
    val email: String,
    val telefone: String,
    val tipo_conta: String?,
    val criado_em: String,
    val prestador: Any?,
    val contratante: Any?
)
