package com.tecsup.lab10.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lab10.data.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class CrudState(
    val rows: List<Record> = emptyList(), val loading: Boolean = true,
    val busy: Boolean = false, val error: String? = null,
    val notice: String? = null, val editId: Int? = null,
    val values: List<String> = emptyList(), val deleteTarget: Record? = null
)

class CrudViewModel(val repository: CrudRepository) : ViewModel() {
    private val mutable = MutableStateFlow(CrudState())
    val state = mutable.asStateFlow()
    init { refresh() }

    private fun message(e: Exception) = when (e) {
        is HttpException -> when (e.code()) {
            404 -> "El registro ya no existe. Actualiza la lista."
            400 -> "El servidor rechazó los datos. Revisa los campos."
            else -> "El servidor respondió HTTP ${e.code()}. Inténtalo nuevamente."
        }
        is IOException -> "No se pudo conectar. Verifica que Django esté activo y vuelve a intentar."
        else -> "No se pudo completar la operación. Inténtalo nuevamente."
    }

    fun refresh() {
        if (mutable.value.busy) return
        mutable.value = mutable.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try { mutable.value = mutable.value.copy(rows = repository.list(), loading = false) }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { mutable.value = mutable.value.copy(loading = false, error = message(e)) }
        }
    }

    fun create() { mutable.value = mutable.value.copy(editId = 0, values = List(repository.fields.size) { "" }, error = null, notice = null) }
    fun edit(id: Int) {
        if (mutable.value.busy) return
        mutable.value = mutable.value.copy(busy = true, error = null, notice = null)
        viewModelScope.launch {
            try {
                val row = repository.get(id)
                mutable.value = mutable.value.copy(busy = false, editId = id, values = row.values)
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { mutable.value = mutable.value.copy(busy = false, error = message(e)) }
        }
    }
    fun change(index: Int, value: String) {
        if (!mutable.value.busy) mutable.value = mutable.value.copy(values = mutable.value.values.mapIndexed { i, old -> if (i == index) value else old }, error = null)
    }
    fun cancel() { if (!mutable.value.busy) mutable.value = mutable.value.copy(editId = null, deleteTarget = null, error = null) }
    fun askDelete(row: Record) { mutable.value = mutable.value.copy(deleteTarget = row, error = null, notice = null) }

    fun save() {
        val before = mutable.value
        val id = before.editId ?: return
        if (before.busy) return
        val error = repository.validate(before.values)
        if (error != null) { mutable.value = before.copy(error = error); return }
        mutate("Registro guardado.") { repository.save(id, before.values) }
    }
    fun delete() {
        val row = mutable.value.deleteTarget ?: return
        if (!mutable.value.busy) mutate("Registro eliminado.") { repository.delete(row.id) }
    }
    private fun mutate(notice: String, operation: suspend () -> Unit) {
        mutable.value = mutable.value.copy(busy = true, error = null)
        viewModelScope.launch {
            try {
                operation()
                // Close only after the HTTP operation succeeds; refresh failures never repeat a POST.
                mutable.value = mutable.value.copy(busy = false, editId = null, deleteTarget = null, notice = notice)
                refresh()
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { mutable.value = mutable.value.copy(busy = false, error = message(e)) }
        }
    }
}
