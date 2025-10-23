package com.exemple.facilita.service

import com.exemple.facilita.model.*
import com.exemple.facilita.screens.ApiResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserService {

    @Headers("Content-Type: application/json")
    @POST("v1/facilita/usuario/register")
    fun saveUser(@Body user: Register): Call<RegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/facilita/usuario/login")
    fun loginUser(@Body user: Login): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/facilita/usuario/recuperar-senha")
    fun recuperarSenha(@Body request: RecuperarSenhaRequest): Call<RecuperarSenhaResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/facilita/usuario/recuperar-senha")
    fun recuperarSenhaTelefone(@Body request: RecuperarSenhaTelefoneRequest): Call<RecuperarSenhaResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/facilita/usuario/verificar-codigo")
    fun verificarCodigo(@Body request: VerificarCodigoRequest): Call<VerificarSenhaResponse>

    @GET("v1/facilita/servico/contratante/pedidos")
    suspend fun getPedidosContratante(
        @Header("Authorization") token: String
    ): Response<PedidosResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/facilita/localizacao")
    fun criarLocalizacao(@Body request: LocalizacaoRequest): Call<LocalizacaoResponse>

    @POST("v1/facilita/contratante/register")
    fun cadastrarContratante(
        @Header("Authorization") authToken: String,
        @Body request: CompletarPerfilRequest
    ): Call<CompletarPerfilResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/facilita/usuario/redefinir-senha")
    fun trocarSenha(@Body request: TrocarSenhaRequest): Call<TrocarSenhaResponse>

    @GET("v1/facilita/servico/contratante/pedidos")
    fun buscarPedidos(
        @Header("Authorization") authToken: String
    ): Call<PedidosResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/facilita/servico")
    suspend fun criarServico(
        @Header("Authorization") authToken: String,
        @Body request: ServicoRequest
    ): Response<ServicoResponse>

    @GET("contratante/pedidos") // ajuste o endpoint se necessário
    suspend fun getPedidos(@Header("Authorization") token: String): Response<ApiResponse>



}
