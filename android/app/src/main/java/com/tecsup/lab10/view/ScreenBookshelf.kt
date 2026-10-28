package com.tecsup.lab10.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tecsup.lab10.data.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException

class BooksViewModel : ViewModel() {
    private val repo = BooksRepository()
    var books by mutableStateOf<List<Book>>(emptyList()); private set
    var selected by mutableStateOf<Book?>(null); private set
    var loading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set
    init { search("kotlin") }
    fun dismiss() { selected = null }
    private fun request(action: suspend () -> Unit) {
        if (loading) return
        loading = true; error = null
        viewModelScope.launch {
            try { action() }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { error = if (e is HttpException && e.code() == 429) "Google Books alcanzó su límite de solicitudes. Intenta más tarde." else "No se pudieron obtener los libros. Revisa tu conexión e intenta de nuevo." }
            finally { loading = false }
        }
    }
    fun search(query: String) { if (query.isNotBlank()) request { books = repo.search(query) } }
    fun detail(id: String) = request { selected = repo.detail(id) }
}

@Composable
fun ScreenBookshelf(vm: BooksViewModel = viewModel()) {
    var query by rememberSaveable { mutableStateOf("kotlin") }
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Bookshelf", style = MaterialTheme.typography.headlineMedium)
        Text("Descubre tu próxima lectura · Google Books", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(query, { query = it }, label = { Text("Título, autor o tema") }, singleLine = true, modifier = Modifier.fillMaxWidth().testTag("book_query"))
        Button(onClick = { vm.search(query) }, enabled = !vm.loading && query.isNotBlank()) { Text("Buscar libros") }
        if (vm.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        vm.error?.let { ErrorMessage(it) }
        if (!vm.loading && vm.books.isEmpty() && vm.error == null) Text("No se encontraron libros para esta búsqueda.")
        LazyVerticalGrid(columns = GridCells.Adaptive(140.dp), contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(vm.books, key = { it.id }) { book ->
                ElevatedCard(Modifier.fillMaxWidth().clickable(enabled = !vm.loading) { vm.detail(book.id) }) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (book.volumeInfo.imageLinks?.secureThumbnail() != null) AsyncImage(
                            model = book.volumeInfo.imageLinks.secureThumbnail(), contentDescription = "Portada de ${book.volumeInfo.title}",
                            modifier = Modifier.fillMaxWidth().height(170.dp), contentScale = ContentScale.Fit)
                        else Box(Modifier.height(170.dp).fillMaxWidth()) { Text("Sin portada disponible") }
                        Text(book.volumeInfo.title, style = MaterialTheme.typography.titleSmall, maxLines = 3)
                        Text(book.volumeInfo.authors.joinToString().ifBlank { "Autor no indicado" }, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                    }
                }
            }
        }
    }
    vm.selected?.let { book -> AlertDialog(onDismissRequest = vm::dismiss,
        title = { Text(book.volumeInfo.title) },
        text = { Text(book.volumeInfo.authors.joinToString().ifBlank { "Autor no indicado" } + "\n\n" + android.text.Html.fromHtml(book.volumeInfo.description ?: "Sin descripción disponible.", android.text.Html.FROM_HTML_MODE_LEGACY).toString().take(1000)) },
        confirmButton = { TextButton(onClick = vm::dismiss) { Text("Cerrar") } }) }
}
