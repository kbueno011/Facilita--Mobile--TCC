package com.exemple.facilita.service

import com.exemple.facilita.model.Login
import com.exemple.facilita.model.LoginResponse
import com.exemple.facilita.model.NominatimResult
import com.exemple.facilita.model.Register
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

// Serviço para sua API
interface UserService {

    @Headers("Content-Type: application/json")
    @POST("/v1/facilita/usuario/register")
    fun saveUser(@Body user: Register): Call<Register>

    @Headers("Content-Type: application/json")
    @POST("/v1/facilita/usuario/login")
    fun loginUser(@Body user: Login): Call<LoginResponse>
}

// Serviço para Nominatim (OpenStreetMap)
interface NominatimApi {
    @GET("search")
    suspend fun searchAddress(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressdetails: Int = 1,
        @Query("limit") limit: Int = 5
    ): List<NominatimResult>
}
