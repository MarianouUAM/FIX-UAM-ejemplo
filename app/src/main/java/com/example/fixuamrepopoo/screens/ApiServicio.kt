package com.example.fixuamrepopoo.screens

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServicio {

    @GET("reportes")
    suspend fun obtenerReportes(): List<Reporte>

    @POST("reportes")
    suspend fun crearReporte(@Body reporte: Reporte): Reporte

    @PUT("reportes/{id}")
    suspend fun actualizarReporte(@Path("id") id: Int, @Body reporte: Reporte): Map<String, String>
}