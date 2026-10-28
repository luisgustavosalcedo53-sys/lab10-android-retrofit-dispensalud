# Lab 10 — CRUD de API con Retrofit II

Aplicación para **Android Studio**, escrita en Kotlin y Jetpack Compose. Consume una API Django mediante Retrofit. Incluye la parte guiada de series, el ejercicio Dispensalud y Bookshelf (actividad adicional).

## Abrir y ejecutar

1. En Android Studio: **File > Open > seleccionar la carpeta `android`** de este proyecto. Esperar a que Gradle sincronice.
2. Iniciar la API desde PowerShell, en esta carpeta: `powershell -ExecutionPolicy Bypass -File .\INICIAR-API.ps1`. Requiere Python 3.10 o superior. La primera ejecución instala dependencias y crea la base de datos.
3. Mantener esa terminal abierta. Comprobar `http://127.0.0.1:8000/serie` en el navegador.
4. Seleccionar el emulador Pixel 7 y pulsar **Run app**. Android usa `http://10.0.2.2:8000/` para acceder al servidor de la PC.
5. En Android 17 o superior, al entrar a Series o Dispensalud pulsar **Permitir conexión local** y aceptar el permiso del sistema. Después probar Agregar, Editar y Eliminar. **Bookshelf** consulta Google Books por Internet.

El proyecto Android tiene su propio Gradle Wrapper. Se usa la combinación de AGP, Gradle y SDK instalada en esta PC: AGP 9.3.3, Gradle 9.5.0, SDK 37 y plugin Compose 2.2.10. `local.properties` no se versiona: Android Studio lo genera para cada computadora. Java se obtiene del JBR de Android Studio.

## Estructura y responsabilidades

| Ruta | Responsabilidad |
|---|---|
| `android/app/src/main/java/com/tecsup/lab10/MainActivity.kt` | Actividad de entrada y tema |
| `…/data/SerieModel.kt` | Correspondencia de JSON con Kotlin, incluido `release_date` |
| `…/data/SerieApiService.kt` | GET, POST, PUT y DELETE de series |
| `…/data/MedicamentoApiService.kt` | Modelo, servicio y repositorio del ejercicio |
| `…/data/ApiClient.kt` | Instancia Retrofit, Gson y tiempos de espera |
| `…/data/CrudRepository.kt` | Acceso a datos y validación de series |
| `…/view/CrudViewModel.kt` | Estado, corutinas y coordinación de operaciones |
| `…/view/ScreenSeries.kt` | Listado, formulario y confirmación reutilizados por ambos CRUD |
| `…/view/SeriesApp.kt` | Inicio y navegación entre secciones |
| `…/data/BooksRepository.kt` | Búsqueda y detalle en Google Books |
| `…/view/ScreenBookshelf.kt` | Cuadrícula de portadas, búsqueda y detalle |
| `backend/catalogo/` | Modelos, serializadores, vistas y pruebas Django REST Framework |
| `backend/lab10/` | Configuración y rutas del servidor |

Flujo: **Compose → ViewModel → repositorio → Retrofit → HTTP → Django REST Framework → SQLite**. La respuesta JSON recorre el camino inverso y actualiza el estado visible.

## Contrato REST

| Acción | Series | Medicamentos | Respuesta |
|---|---|---|---|
| Listar | `GET /serie` | `GET /medicamento` | 200 y arreglo JSON |
| Consultar uno | `GET /serie/{id}` | `GET /medicamento/{id}` | 200 o 404 |
| Crear | `POST /serie` | `POST /medicamento` | 201 |
| Actualizar | `PUT /serie/{id}` | `PUT /medicamento/{id}` | 200 |
| Eliminar | `DELETE /serie/{id}` | `DELETE /medicamento/{id}` | 204 sin cuerpo |

Series: `id`, `name`, `release_date`, `rating`, `category`. Medicamentos: `id`, `nombre`, `dosis`, `frecuencia`, `cantidad`. El servidor asigna el ID. Las rutas no terminan en `/`, como en la guía. Los datos quedan persistidos en SQLite; no son respuestas simuladas.

El ejercicio adapta los campos de la entidad `Medicamento` de DispensaludCRUD a una API local de laboratorio. Es una copia académica independiente: no se conecta a una base de producción ni reemplaza el proyecto original.

## Diferencias justificadas respecto al ejemplo de la guía

- El guardado espera la respuesta antes de cerrar el formulario; un fallo conserva los datos escritos.
- Se valida la fecha real y los valores enteros antes de enviar. No se usan conversiones inseguras con `!!`.
- DELETE usa `Response<Unit>`, porque HTTP 204 no tiene cuerpo.
- No hay `delay(100)` para esperar datos. Las funciones `suspend` esperan la llamada de red.
- La cancelación del diálogo ocurre en un callback, sin navegar durante la composición.
- ViewModel conserva el estado al rotar y los botones se bloquean durante las mutaciones.
- Android 17 requiere `ACCESS_LOCAL_NETWORK`: se declara y solicita antes de conectar con Django. Si se rechaza, la app explica cómo habilitarlo.
- La edición usa un formulario dentro de la sección en lugar de rutas separadas por cada acción.

## Probar en teléfono físico

Con USB: ejecutar `adb reverse tcp:8000 tcp:8000`, cambiar `API_BASE_URL` en `android/app/build.gradle.kts` a `http://127.0.0.1:8000/`, sincronizar y volver a ejecutar. El XML de seguridad ya permite localhost.

Por Wi-Fi: usar la IP de la PC en `API_BASE_URL`, agregar exactamente esa IP a `network_security_config.xml` y a la variable `DJANGO_ALLOWED_HOSTS`; iniciar con `-Bind '0.0.0.0:8000'`. Ambos equipos deben estar en la misma red y Windows debe permitir el puerto. La configuración incluida está preparada para el emulador y desarrollo local.

## Pruebas reproducibles

Backend, dentro de `backend`: `python manage.py test`.

Android, dentro de `android`: `./gradlew.bat testDebugUnitTest assembleDebug`.

Con Django y un emulador activos: `./gradlew.bat connectedDebugAndroidTest`. Las pruebas de interfaz crean registros cuyo nombre termina en E2E, comprueban la actualización y los eliminan.

## Commits y entrega

El repositorio Git local contiene avances reales. Revisar con `git log --oneline`. Para subirlo, crear un repositorio vacío en tu cuenta y ejecutar desde esta carpeta:

```powershell
git remote add origin https://github.com/TU_USUARIO/TU_REPOSITORIO.git
git push -u origin main
```

La subida requiere tu URL de repositorio y una sesión autenticada. No se considera realizada solamente por tener commits locales. El informe y los videos son entregables separados; revisar `VERIFICACION.md` para distinguir lo probado de lo pendiente.

## Fuentes técnicas

- Guía proporcionada: GLAB-S10-BPAREJA-2026-01 (1).pdf.
- Android Bookshelf: https://developer.android.com/codelabs/basic-android-kotlin-compose-bookshelf
- Django REST Framework: https://www.django-rest-framework.org/api-guide/generic-views/
- Retrofit: https://github.com/square/retrofit

Desarrollo asistido por IA; revisar y comprender el código antes de la sustentación.
