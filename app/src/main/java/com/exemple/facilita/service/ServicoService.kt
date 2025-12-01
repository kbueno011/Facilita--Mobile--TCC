package com.exemple.facilita.service

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*

// ==================== MODELOS DE DADOS ====================

// Usuário
data class Usuario(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nome") val nome: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("telefone") val telefone: String = "",
    @SerializedName("cpf") val cpf: String? = null
)

// Contratante
data class Contratante(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("usuario") val usuario: Usuario? = null
)

// Categoria
data class Categoria(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nome") val nome: String = "",
    @SerializedName("descricao") val descricao: String? = null
)

// Prestador
data class Prestador(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("usuario") val usuario: Usuario? = null
)

// Localização
data class Localizacao(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("cidade") val cidade: String = ""
)

// Paginação
data class Paginacao(
    @SerializedName("pagina_atual") val pagina_atual: Int = 1,
    @SerializedName("total_paginas") val total_paginas: Int = 1,
    @SerializedName("total_pedidos") val total_pedidos: Int = 0,
    @SerializedName("por_pagina") val por_pagina: Int = 10
)

// Pedido do Histórico (estrutura da API)
data class PedidoHistorico(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("descricao") val descricao: String = "",
    @SerializedName("valor") val valor: Double = 0.0,
    @SerializedName("status") val status: String = "PENDENTE",
    @SerializedName("data_solicitacao") val data_solicitacao: String = "",
    @SerializedName("data_conclusao") val data_conclusao: String? = null,
    @SerializedName("categoria") val categoria: Categoria? = null,
    @SerializedName("localizacao") val localizacao: Localizacao? = null,
    @SerializedName("prestador") val prestador: Prestador? = null,
    @SerializedName("contratante") val contratante: Contratante? = null,
    @SerializedName("endereco") val endereco: String = "",
    @SerializedName("observacoes") val observacoes: String = ""
)

// Resposta do Histórico
data class HistoricoPedidosData(
    @SerializedName("pedidos") val pedidos: List<PedidoHistorico> = emptyList(),
    @SerializedName("paginacao") val paginacao: Paginacao? = null
)

data class HistoricoPedidosResponse(
    @SerializedName("status_code") val status_code: Int = 200,
    @SerializedName("data") val data: HistoricoPedidosData? = null
)

// Resposta de Detalhes do Pedido
data class DetalhePedidoResponse(
    @SerializedName("status_code") val status_code: Int = 200,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val data: PedidoHistorico? = null
)

// ==================== INTERFACE DO SERVIÇO ====================

interface ServicoService {
    @GET("v1/facilita/servico/contratante/pedidos")
    fun getHistoricoPedidos(
        @Header("Authorization") token: String
    ): Call<HistoricoPedidosResponse>

    @GET("v1/facilita/servico/{id}")
    fun getDetalhesPedido(
        @Header("Authorization") token: String,
        @Path("id") pedidoId: Int
    ): Call<DetalhePedidoResponse>
}

