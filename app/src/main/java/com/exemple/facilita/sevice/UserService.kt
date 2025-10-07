package com.exemple.facilita.service

import com.exemple.facilita.model.CompletarPerfilRequest
import com.exemple.facilita.model.LocalizacaoRequest
import com.exemple.facilita.model.LocalizacaoResponse
import com.exemple.facilita.model.Login
import com.exemple.facilita.model.LoginResponse
import com.exemple.facilita.model.RecuperarSenhaRequest
import com.exemple.facilita.model.RecuperarSenhaResponse
import com.exemple.facilita.model.RecuperarSenhaTelefoneRequest
import com.exemple.facilita.model.Register
import com.exemple.facilita.model.TrocarSenhaRequest
import com.exemple.facilita.model.TrocarSenhaResponse
import com.exemple.facilita.model.VerificarCodigoRequest
import com.exemple.facilita.model.VerificarSenhaResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Serviço para API
interface UserService {

    @Headers("Content-Type: application/json")
    @POST("v1/facilita/usuario/register")
    fun saveUser(@Body user: Register): Call<Register>

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


    @Headers("Content-Type: application/json")
    @POST("v1/facilita/localizacao")
    fun criarLocalizacao(@Body request: LocalizacaoRequest): Call<LocalizacaoResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/facilita/localizacao")
    fun cadastrarContratante(@Body request: CompletarPerfilRequest): Call<LocalizacaoResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/facilita/usuario/redefinir-senha")
    fun trocarSenha(@Body request: TrocarSenhaRequest): Call<TrocarSenhaResponse>

}
