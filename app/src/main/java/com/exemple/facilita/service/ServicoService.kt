package com.exemple.facilita.service

import retrofit2.Call
import retrofit2.http.*

// ==================== MODELOS DE DADOS ====================

// Usuário
data class Usuario(
    val id: Int,
    val nome: String,
    val email: String,
    val telefone: String,
    val cpf: String? = null
)

// Contratante
data class Contratante(
    val id: Int,
    val usuario: Usuario
)

// Categoria
data class Categoria(
    val id: Int,
    val nome: String,
    val descricao: String? = null
)

// Pedido do Histórico
data class PedidoHistorico(
    val id: Int,
    val descricao: String,
    val valor: Double,
    val status: String,
    val data_solicitacao: String,
    val endereco: String,
    val observacoes: String = "",
    val contratante: Contratante?,
    val categoria: Categoria
)

// Resposta do Histórico
data class HistoricoPedidosData(
    val pedidos: List<PedidoHistorico>
)

data class HistoricoPedidosResponse(
    val status_code: Int,
    val message: String,
    val data: HistoricoPedidosData
)

// Resposta de Detalhes do Pedido
data class DetalhePedidoResponse(
    val status_code: Int,
    val message: String,
    val data: PedidoHistorico
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

