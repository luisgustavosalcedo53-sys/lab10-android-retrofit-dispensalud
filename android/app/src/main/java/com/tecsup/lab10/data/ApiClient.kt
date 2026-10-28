package com.tecsup.lab10.data

import com.tecsup.lab10.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val http = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .callTimeout(25, TimeUnit.SECONDS)
        .build()

    fun retrofit(url: String): Retrofit = Retrofit.Builder().baseUrl(url)
        .client(http).addConverterFactory(GsonConverterFactory.create()).build()

    private val local by lazy { retrofit(BuildConfig.API_BASE_URL) }
    val series: SerieApiService by lazy { local.create(SerieApiService::class.java) }
}
