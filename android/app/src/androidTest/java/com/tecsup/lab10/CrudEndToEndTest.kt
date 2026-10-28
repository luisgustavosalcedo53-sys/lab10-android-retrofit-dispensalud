package com.tecsup.lab10

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import android.graphics.Bitmap
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/** Ejecutar con Django activo. Crea y elimina únicamente registros de prueba. */
@RunWith(AndroidJUnit4::class)
class CrudEndToEndTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun pause() { Thread.sleep(900) }
    private fun waitFor(tag: String) {
        compose.waitUntil(30000) { compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty() }
        compose.waitForIdle()
    }
    private fun ready() {
        waitFor("new")
        compose.waitUntil(30000) { runCatching { compose.onNodeWithTag("new").assertIsEnabled(); true }.getOrDefault(false) }
    }
    private fun screenshot(name: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val directory = instrumentation.targetContext.getExternalFilesDir(null)!!
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        File(directory, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }

    @Test fun a_seriesCreateReadUpdateCancelDeleteAndValidate() {
        compose.onNodeWithTag("tab_series").performClick(); ready(); pause()
        screenshot("01-series-listado")
        compose.onNodeWithTag("new").performClick()
        compose.onNodeWithTag("save").performClick()
        compose.onNodeWithTag("error").assertIsDisplayed()
        compose.onNodeWithTag("name").performTextInput("Serie Evidencia E2E")
        compose.onNodeWithTag("release_date").performTextInput("2026-10-27")
        compose.onNodeWithTag("rating").performTextInput("8")
        compose.onNodeWithTag("category").performTextInput("Demostracion")
        compose.activityRule.scenario.onActivity { activity ->
            val imm = activity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
        pause(); screenshot("02-series-formulario")
        compose.onNodeWithTag("save").performScrollTo().performClick(); ready()
        compose.onNodeWithTag("records").performScrollToNode(hasText("Serie Evidencia E2E"))
        compose.onNodeWithContentDescription("Editar Serie Evidencia E2E").performClick(); waitFor("name")
        compose.onNodeWithTag("release_date").performTextReplacement("2026-11-01")
        compose.onNodeWithTag("save").performScrollTo().performClick(); ready()
        compose.onNodeWithTag("records").performScrollToNode(hasText("Serie Evidencia E2E"))
        compose.onNodeWithText("Estreno (AAAA-MM-DD): 2026-11-01").assertIsDisplayed()
        pause(); screenshot("03-series-actualizada")
        compose.onNodeWithContentDescription("Eliminar Serie Evidencia E2E").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Serie Evidencia E2E").assertExists()
        compose.onNodeWithContentDescription("Eliminar Serie Evidencia E2E").performClick()
        pause(); screenshot("04-series-confirmacion")
        compose.onNodeWithTag("confirm_delete").performClick(); ready()
        compose.onNodeWithText("Serie Evidencia E2E").assertDoesNotExist(); pause()
    }

    @Test fun b_dispensaludCrud() {
        compose.onNodeWithTag("tab_medicamentos").performClick(); ready(); pause()
        compose.onNodeWithTag("new").performClick()
        compose.onNodeWithTag("nombre").performTextInput("Medicamento Demo E2E")
        compose.onNodeWithTag("dosis").performTextInput("Dato de prueba")
        compose.onNodeWithTag("frecuencia").performTextInput("Horario de prueba")
        compose.onNodeWithTag("cantidad").performTextInput("20")
        compose.onNodeWithTag("save").performScrollTo().performClick(); ready()
        compose.onNodeWithTag("records").performScrollToNode(hasText("Medicamento Demo E2E"))
        pause(); screenshot("05-dispensalud-creado")
        compose.onNodeWithContentDescription("Editar Medicamento Demo E2E").performClick(); waitFor("nombre")
        compose.onNodeWithTag("cantidad").performTextReplacement("35")
        compose.onNodeWithTag("save").performScrollTo().performClick(); ready()
        compose.onNodeWithTag("records").performScrollToNode(hasText("Medicamento Demo E2E"))
        compose.onNodeWithText("Cantidad disponible: 35").assertIsDisplayed()
        pause(); screenshot("06-dispensalud-actualizado")
        compose.onNodeWithContentDescription("Eliminar Medicamento Demo E2E").performClick()
        pause(); screenshot("07-dispensalud-eliminar")
        compose.onNodeWithTag("confirm_delete").performClick(); ready()
        compose.onNodeWithText("Medicamento Demo E2E").assertDoesNotExist(); pause()
    }
}
