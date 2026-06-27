package com.example.fixuamrepopoo.screens

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object RetrofitCliente {

    // Esta es la IP mágica que conecta el emulador con tu servidor en VS Code
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val api: ApiServicio by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServicio::class.java)
    }
}