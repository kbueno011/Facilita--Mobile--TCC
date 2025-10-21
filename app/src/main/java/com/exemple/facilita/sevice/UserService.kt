package com.exemple.facilita.service

import com.exemple.facilita.model.CompletarPerfilRequest
import com.exemple.facilita.model.CompletarPerfilResponse
import com.exemple.facilita.model.LocalizacaoRequest
import com.exemple.facilita.model.LocalizacaoResponse
import com.exemple.facilita.model.Login
import com.exemple.facilita.model.LoginResponse
import com.exemple.facilita.model.PedidosResponse
import com.exemple.facilita.model.RecuperarSenhaRequest
import com.exemple.facilita.model.RecuperarSenhaResponse
import com.exemple.facilita.model.RecuperarSenhaTelefoneRequest
import com.exemple.facilita.model.Register
import com.exemple.facilita.model.RegisterResponse
import com.exemple.facilita.model.TrocarSenhaRequest
import com.exemple.facilita.model.TrocarSenhaResponse
import com.exemple.facilita.model.VerificarCodigoRequest
import com.exemple.facilita.model.VerificarSenhaResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

// Serviço para API
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
        @Header("Authorization") authToken: String, // <- aqui
        @Body request: CompletarPerfilRequest
    ): Call<CompletarPerfilResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/facilita/usuario/redefinir-senha")
    fun trocarSenha(@Body request: TrocarSenhaRequest): Call<TrocarSenhaResponse>

    @GET("/v1/facilita/servico/contratante/pedidos")
    fun buscarPedidos(
        @Header("Authorization") authToken: String
    ): Call<PedidosResponse>


}
