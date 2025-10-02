package com.exemple.facilita.service

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactory {

    private val gson = GsonBuilder()
        .serializeNulls()
        .setLenient()
        .create()

    private val retrofitUser: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.107.144.7:8080")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun getUserService(): UserService = retrofitUser.create(UserService::class.java)
}