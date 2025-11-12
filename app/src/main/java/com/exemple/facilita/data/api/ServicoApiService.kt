package com.exemple.facilita.data.api

import com.exemple.facilita.data.models.MeusServicosResponse
import com.exemple.facilita.data.models.ServicoResponse
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
}

