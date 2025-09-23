package com.exemple.facilita.service

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactory {

    private val gson = GsonBuilder()
        .serializeNulls()
        .setLenient()
        .create()

    // Retrofit para sua API local
    private val retrofitUser: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080") // ATENÇÃO: precisa terminar com "/"
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun getUserService(): UserService {
        return retrofitUser.create(UserService::class.java)
    }

    // Retrofit para Nominatim (OpenStreetMap)
    private val retrofitNominatim: Retrofit by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "facilita-app/1.0 (seuemail@dominio.com)") // obrigatório!
                    .build()
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    fun getNominatimApi(): NominatimApi {
        return retrofitNominatim.create(NominatimApi::class.java)
    }
}
