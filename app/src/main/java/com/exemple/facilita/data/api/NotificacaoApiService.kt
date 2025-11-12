package com.exemple.facilita.data.api

import com.exemple.facilita.data.models.MarcarLidaResponse
import com.exemple.facilita.data.models.NotificacoesResponse
import retrofit2.Response
import retrofit2.http.*

interface NotificacaoApiService {

    // Buscar todas as notificações do usuário
    @GET("notificacoes")
    suspend fun obterNotificacoes(
        @Header("Authorization") token: String
    ): Response<NotificacoesResponse>

    // Buscar notificações não lidas
    @GET("notificacoes/nao-lidas")
    suspend fun obterNotificacoesNaoLidas(
        @Header("Authorization") token: String
    ): Response<NotificacoesResponse>

    // Marcar notificação como lida
    @PUT("notificacoes/{id}/marcar-lida")
    suspend fun marcarComoLida(
        @Header("Authorization") token: String,
        @Path("id") notificacaoId: Int
    ): Response<MarcarLidaResponse>

    // Marcar todas como lidas
    @PUT("notificacoes/marcar-todas-lidas")
    suspend fun marcarTodasComoLidas(
        @Header("Authorization") token: String
    ): Response<MarcarLidaResponse>

    // Deletar notificação
    @DELETE("notificacoes/{id}")
    suspend fun deletarNotificacao(
        @Header("Authorization") token: String,
        @Path("id") notificacaoId: Int
    ): Response<MarcarLidaResponse>
}

