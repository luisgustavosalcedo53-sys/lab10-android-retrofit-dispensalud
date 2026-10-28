package com.tecsup.lab10.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

/** Android 17 requiere autorización adicional para conectar con la API de la PC. */
@Composable
fun LocalNetworkGate(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val permission = "android.permission.ACCESS_LOCAL_NETWORK"
    fun isGranted() = Build.VERSION.SDK_INT < 37 || context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    var granted by remember { mutableStateOf(isGranted()) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { granted = isGranted() }
    if (granted) content() else Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Conectar con tu servidor", style = MaterialTheme.typography.headlineMedium)
        Text("Permite el acceso a la red local para consultar y guardar los registros en el servidor Django de tu computadora.")
        Button(onClick = { launcher.launch(permission) }) { Text("Permitir conexión local") }
        TextButton(onClick = { context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))) }) { Text("Abrir permisos de la app") }
    }
}
