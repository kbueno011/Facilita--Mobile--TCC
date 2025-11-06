package com.exemple.facilita.service

import com.exemple.facilita.viewmodel.ProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("v1/facilita/usuario/perfil")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>
}
