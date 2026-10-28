package com.tecsup.lab10.data

import retrofit2.Response
import retrofit2.http.*

interface SerieApiService {
    @GET("serie") suspend fun selectSeries(): List<SerieModel>
    @GET("serie/{id}") suspend fun selectSerie(@Path("id") id: Int): Response<SerieModel>
    @POST("serie") suspend fun insertSerie(@Body serie: SerieModel): Response<SerieModel>
    @PUT("serie/{id}") suspend fun updateSerie(@Path("id") id: Int, @Body serie: SerieModel): Response<SerieModel>
    @DELETE("serie/{id}") suspend fun deleteSerie(@Path("id") id: Int): Response<Unit>
}
