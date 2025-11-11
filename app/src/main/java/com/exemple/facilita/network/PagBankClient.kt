package com.exemple.facilita.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object PagBankClient {

    // TOKEN DO SANDBOX PAGBANK
    // IMPORTANTE: Substitua este token pelo seu token real do sandbox
    // Para obter: https://sandbox.pagseguro.uol.com.br/
    private const val SANDBOX_TOKEN = "YOUR_SANDBOX_TOKEN_HERE"

    // URLs do PagBank
    private const val SANDBOX_BASE_URL = "https://sandbox.api.pagseguro.com/"
    private const val PRODUCTION_BASE_URL = "https://api.pagseguro.com/"

    // Use sandbox para testes
    private const val USE_SANDBOX = true

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $SANDBOX_TOKEN")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(if (USE_SANDBOX) SANDBOX_BASE_URL else PRODUCTION_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    inline fun <reified T> createService(): T {
        return retrofit.create(T::class.java)
    }
}

