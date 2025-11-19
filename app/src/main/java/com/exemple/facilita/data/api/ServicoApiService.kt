package com.exemple.facilita.data.api

import com.exemple.facilita.data.models.MeusServicosResponse
import com.exemple.facilita.data.models.ServicoResponse
import com.exemple.facilita.data.models.ServicosPorStatusResponse
import retrofit2.Response
import retrofit2.http.*

interface ServicoApiService {

    // Buscar meus serviços (contratante)
    @GET("servico/meus-servicos")
    suspend fun meusServicos(
        @Header("Authorization") token: String
    ): Response<MeusServicosResponse>

    // Buscar detalhes de um serviço específico
    @GET("servico/{id}")
    suspend fun obterServico(
        @Header("Authorization") token: String,
        @Path("id") servicoId: String
    ): Response<ServicoResponse>

    // Cancelar serviço
    @PUT("servico/{id}/cancelar")
    suspend fun cancelarServico(
        @Header("Authorization") token: String,
        @Path("id") servicoId: String
    ): Response<ServicoResponse>

    // Buscar serviços por status - contratante (com polling)
    @GET("servico/contratante/pedidos")
    suspend fun buscarServicosPorStatus(
        @Header("Authorization") token: String,
        @Query("status") status: String
    ): Response<ServicosPorStatusResponse>

    // Buscar TODOS os pedidos do contratante (sem filtro de status)
    @GET("servico/contratante/pedidos")
    suspend fun buscarTodosPedidos(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<ServicosPorStatusResponse>

    // Contratante vê seu serviço sendo executado (GET genérico)
    @GET("servico")
    suspend fun obterServicoEmExecucao(
        @Header("Authorization") token: String
    ): Response<MeusServicosResponse>
}

