package com.tecsup.lab10.data

import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate

data class Field(val key: String, val label: String, val numeric: Boolean = false)
data class Record(val id: Int, val values: List<String>)

interface CrudRepository {
    val title: String
    val singular: String
    val fields: List<Field>
    suspend fun list(): List<Record>
    suspend fun get(id: Int): Record
    suspend fun save(id: Int, values: List<String>)
    suspend fun delete(id: Int)
    fun validate(values: List<String>): String?
}

fun <T> Response<T>.requiredBody(): T {
    if (!isSuccessful) throw HttpException(this)
    return body() ?: throw IOException("El servidor devolvió una respuesta vacía.")
}

fun Response<*>.checkSuccess() {
    if (!isSuccessful) throw HttpException(this)
}

class SeriesRepository(private val api: SerieApiService = ApiClient.series) : CrudRepository {
    override val title = "Series"
    override val singular = "serie"
    override val fields = listOf(Field("name", "Nombre"), Field("release_date", "Estreno (AAAA-MM-DD)"),
        Field("rating", "Puntuación (0 a 10)", true), Field("category", "Categoría"))
    private fun SerieModel.record() = Record(id, listOf(name, releaseDate, rating.toString(), category))
    override suspend fun list() = api.selectSeries().map { it.record() }
    override suspend fun get(id: Int) = api.selectSerie(id).requiredBody().record()
    override suspend fun save(id: Int, values: List<String>) {
        require(validate(values) == null)
        val model = SerieModel(id, values[0].trim(), values[1].trim(), values[2].toInt(), values[3].trim())
        (if (id == 0) api.insertSerie(model) else api.updateSerie(id, model)).requiredBody()
    }
    override suspend fun delete(id: Int) = api.deleteSerie(id).checkSuccess()
    override fun validate(values: List<String>): String? {
        if (values.size != 4 || values.any { it.isBlank() }) return "Completa todos los campos."
        if (values[0].trim().length > 120 || values[3].trim().length > 80) return "Nombre: máximo 120 caracteres. Categoría: máximo 80."
        if (!Regex("\\d{4}-\\d{2}-\\d{2}").matches(values[1]) || runCatching { LocalDate.parse(values[1]) }.isFailure)
            return "Ingresa una fecha real con formato AAAA-MM-DD."
        if (values[2].toIntOrNull() !in 0..10) return "La puntuación debe ser un entero entre 0 y 10."
        return null
    }
}
