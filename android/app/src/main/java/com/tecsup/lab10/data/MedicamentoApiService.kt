package com.tecsup.lab10.data

import retrofit2.Response
import retrofit2.http.*

// Conserva los campos de la entidad Medicamento del proyecto DispensaludCRUD.
data class MedicamentoModel(val id: Int = 0, val nombre: String, val dosis: String, val frecuencia: String, val cantidad: Int)

interface MedicamentoApiService {
    @GET("medicamento") suspend fun list(): List<MedicamentoModel>
    @GET("medicamento/{id}") suspend fun get(@Path("id") id: Int): Response<MedicamentoModel>
    @POST("medicamento") suspend fun create(@Body item: MedicamentoModel): Response<MedicamentoModel>
    @PUT("medicamento/{id}") suspend fun update(@Path("id") id: Int, @Body item: MedicamentoModel): Response<MedicamentoModel>
    @DELETE("medicamento/{id}") suspend fun delete(@Path("id") id: Int): Response<Unit>
}

class MedicamentosRepository(
    private val api: MedicamentoApiService = ApiClient.retrofit(com.tecsup.lab10.BuildConfig.API_BASE_URL).create(MedicamentoApiService::class.java)
) : CrudRepository {
    override val title = "Medicamentos"
    override val singular = "medicación"
    override val fields = listOf(Field("nombre", "Nombre"), Field("dosis", "Dosis registrada"), Field("frecuencia", "Frecuencia registrada"), Field("cantidad", "Cantidad disponible", true))
    private fun MedicamentoModel.record() = Record(id, listOf(nombre, dosis, frecuencia, cantidad.toString()))
    override suspend fun list() = api.list().map { it.record() }
    override suspend fun get(id: Int) = api.get(id).requiredBody().record()
    override suspend fun save(id: Int, values: List<String>) {
        require(validate(values) == null)
        val item = MedicamentoModel(id, values[0].trim(), values[1].trim(), values[2].trim(), values[3].toInt())
        (if (id == 0) api.create(item) else api.update(id, item)).requiredBody()
    }
    override suspend fun delete(id: Int) = api.delete(id).checkSuccess()
    override fun validate(values: List<String>): String? {
        if (values.size != 4 || values.any { it.isBlank() }) return "Completa todos los campos."
        if (values[0].trim().length > 120 || values[1].trim().length > 80 || values[2].trim().length > 80) return "Nombre: máximo 120 caracteres. Dosis y frecuencia: máximo 80."
        if (values[3].toIntOrNull() !in 0..1000000) return "La cantidad debe ser un entero entre 0 y 1000000."
        return null
    }
}
