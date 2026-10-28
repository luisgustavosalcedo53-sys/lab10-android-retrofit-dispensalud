package com.tecsup.lab10.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.tecsup.lab10.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesApp() {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val tabs = listOf("inicio", "series", "medicamentos", "bookshelf")
    val labels = listOf("Inicio", "Series", "Dispensalud", "Bookshelf")
    val icons = listOf(Icons.Outlined.Home, Icons.Outlined.Movie, Icons.Outlined.Medication, Icons.Outlined.MenuBook)
    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("LAB 10  /  RETROFIT", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }) },
        bottomBar = { NavigationBar {
            tabs.forEachIndexed { i, route -> NavigationBarItem(
                selected = entry?.destination?.route == route,
                onClick = { nav.navigate(route) { popUpTo(nav.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true } },
                icon = { Icon(icons[i], contentDescription = null) }, label = { Text(labels[i]) }, modifier = Modifier.testTag("tab_$route")) }
        } }) { padding ->
        NavHost(nav, startDestination = "inicio", modifier = Modifier.padding(padding)) {
            composable("inicio") {
                Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text("De la app\nal servidor", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                    Text("Crea, consulta, edita y elimina registros con una API REST.", style = MaterialTheme.typography.titleMedium)
                    ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("01  Series", fontWeight = FontWeight.Bold)
                        Text("Parte guiada: catálogo con fecha de estreno, puntuación y categoría.")
                        Button(onClick = { nav.navigate("series") }) { Text("Abrir series") }
                    } }
                    ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("02  Dispensalud", fontWeight = FontWeight.Bold)
                        Text("Ejercicio: inventario de medicamentos conectado al servicio web.")
                        Button(onClick = { nav.navigate("medicamentos") }) { Text("Abrir inventario") }
                    } }
                    Text("03  Bookshelf · Explora libros de Google Books", style = MaterialTheme.typography.bodyMedium)
                }
            }
            composable("series") { ScreenSeries(crudViewModel("series") { SeriesRepository() }) }
            composable("medicamentos") { ScreenSeries(crudViewModel("medicamentos") { MedicamentosRepository() }) }
            composable("bookshelf") { ScreenBookshelf() }
        }
    }
}

@Composable
private fun crudViewModel(key: String, repository: () -> CrudRepository): CrudViewModel = viewModel(key = key, factory = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CrudViewModel(repository()) as T
})
