package com.tecsup.lab10.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ScreenSeries(vm: CrudViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val repo = vm.repository
    BackHandler(state.editId != null || state.deleteTarget != null) { vm.cancel() }
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text(repo.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(if (repo.title == "Series") "Tu catálogo conectado a la API" else "Dispensalud · Inventario de demostración",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        state.notice?.let { Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.testTag("notice")) }
        if (state.editId == null) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = vm::create, enabled = !state.busy && !state.loading, modifier = Modifier.testTag("new")) { Text("Agregar") }
                OutlinedButton(onClick = vm::refresh, enabled = !state.busy && !state.loading) { Text("Actualizar") }
            }
        }
        if (state.loading || state.busy) LinearProgressIndicator(Modifier.fillMaxWidth().padding(vertical = 8.dp))
        if (state.deleteTarget == null) state.error?.let { ErrorMessage(it) }
        if (state.editId != null) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).imePadding(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(if (state.editId == 0) "Nueva ${repo.singular}" else "Editar ${repo.singular} #${state.editId}", style = MaterialTheme.typography.titleLarge)
                repo.fields.forEachIndexed { index, field ->
                    OutlinedTextField(value = state.values[index], onValueChange = { vm.change(index, it) },
                        label = { Text(field.label) }, singleLine = true, enabled = !state.busy,
                        keyboardOptions = KeyboardOptions(keyboardType = if (field.numeric) KeyboardType.Number else KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth().testTag(field.key))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = vm::save, enabled = !state.busy, modifier = Modifier.testTag("save")) { Text(if (state.busy) "Guardando…" else "Guardar") }
                    OutlinedButton(onClick = vm::cancel, enabled = !state.busy) { Text("Cancelar") }
                }
                Spacer(Modifier.height(16.dp))
            }
        } else {
            if (!state.loading && state.rows.isEmpty() && state.error == null) Text("Aún no hay registros. Usa Agregar para crear el primero.")
            LazyColumn(Modifier.weight(1f).testTag("records"), contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.rows, key = { it.id }) { row ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text("#${row.id}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                    Text(row.values.first(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                IconButton(onClick = { vm.edit(row.id) }, enabled = !state.busy && !state.loading, modifier = Modifier.testTag("edit_${row.id}")) {
                                    Icon(Icons.Outlined.Edit, contentDescription = "Editar ${row.values.first()}")
                                }
                                IconButton(onClick = { vm.askDelete(row) }, enabled = !state.busy && !state.loading, modifier = Modifier.testTag("delete_${row.id}")) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Eliminar ${row.values.first()}")
                                }
                            }
                            row.values.drop(1).forEachIndexed { index, value -> Text("${repo.fields[index + 1].label}: $value", style = MaterialTheme.typography.bodyMedium) }
                        }
                    }
                }
            }
        }
    }
    state.deleteTarget?.let { row ->
        AlertDialog(onDismissRequest = vm::cancel,
            title = { Text("Confirmar eliminación") },
            text = { Column { Text("¿Eliminar «${row.values.first()}»? Esta acción borra el registro del servidor."); state.error?.let { ErrorMessage(it) } } },
            confirmButton = { Button(onClick = vm::delete, enabled = !state.busy, modifier = Modifier.testTag("confirm_delete")) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = vm::cancel, enabled = !state.busy) { Text("Cancelar") } })
    }
}

@Composable
fun ErrorMessage(message: String) {
    Text(message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp).testTag("error"))
}
