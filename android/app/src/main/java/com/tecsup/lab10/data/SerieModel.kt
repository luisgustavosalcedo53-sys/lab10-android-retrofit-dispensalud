package com.tecsup.lab10.data

import com.google.gson.annotations.SerializedName

data class SerieModel(
    val id: Int = 0,
    val name: String,
    @SerializedName("release_date") val releaseDate: String,
    val rating: Int,
    val category: String
)
