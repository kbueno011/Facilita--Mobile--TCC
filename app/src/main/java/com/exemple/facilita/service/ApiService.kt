package com.exemple.facilita.service

import com.exemple.facilita.viewmodel.ProfileResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("v1/facilita/usuario/perfil")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>

    @Headers("Content-Type: application/json")
    @PUT("v1/facilita/usuario/perfil")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body dados: Map<String, String>
    ): Response<ProfileResponse>

    @Headers("Content-Type: application/json")
    @PUT("v1/facilita/localizacao")
    suspend fun updateLocalizacao(
        @Header("Authorization") token: String,
        @Body localizacao: Map<String, String>
    ): Response<ProfileResponse>
}
