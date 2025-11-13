package com.exemple.facilita.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Resposta da API ao criar serviço (estrutura completa)
data class CriarServicoResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ServicoData  // Usa ServicoData do arquivo ServicoData.kt
) : Serializable

// Modelo antigo mantido para compatibilidade
data class ServicoResponse(
    val id: Int?,
    val descricao: String?,
    val valor: Double?,
    val status: String?,
    val categoria: Categoria?,
    val contratante: Contratante?,
    val detalhes_valor: DetalhesValor?
) : Serializable
