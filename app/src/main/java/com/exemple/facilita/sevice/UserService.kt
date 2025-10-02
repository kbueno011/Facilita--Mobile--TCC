package com.exemple.facilita.service

import com.exemple.facilita.model.Login
import com.exemple.facilita.model.LoginResponse
import com.exemple.facilita.model.RecuperarSenhaRequest
import com.exemple.facilita.model.RecuperarSenhaResponse
import com.exemple.facilita.model.Register
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Serviço para API
interface UserService {

    @Headers("Content-Type: application/json")
    @POST("/v1/facilita/usuario/register")
    fun saveUser(@Body user: Register): Call<Register>

    @Headers("Content-Type: application/json")
    @POST("/v1/facilita/usuario/login")
    fun loginUser(@Body user: Login): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("/v1/facilita/usuario/recuperar-senha")
    fun recuperarSenha(@Body request: RecuperarSenhaRequest): Call<RecuperarSenhaResponse>

}
