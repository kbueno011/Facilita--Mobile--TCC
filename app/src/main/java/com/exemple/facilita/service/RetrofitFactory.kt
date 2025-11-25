package com.exemple.facilita.service

import com.exemple.facilita.data.api.ServicoApiService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {

    private val gson = GsonBuilder()
        .serializeNulls()
        .setLenient()
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://facilita-c6hhb9csgygudrdz.canadacentral-01.azurewebsites.net/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Mantém o UserService existente para não quebrar outras partes do app
    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    // ServicoApiService para operações de serviço
    val servicoApiService: ServicoApiService by lazy {
        retrofit.create(ServicoApiService::class.java)
    }
}
