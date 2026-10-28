# Laboratorio 10: CRUD de API con Retrofit II

**Borrador de apoyo separado del proyecto.** Completa tus datos, revisa las evidencias y adapta la reflexión a tu experiencia antes de entregarlo. No se atribuyen a ti pruebas o dificultades personales no confirmadas.

**Alumno:** [completar]  
**Curso:** Programación en Móviles  
**Grupo y ciclo:** [completar]  
**Fecha de entrega:** [completar]  
**Repositorio remoto:** https://github.com/luisgustavosalcedo53-sys/lab10-android-retrofit-dispensalud

## 1. Objetivos

Consumir una API REST desde Android mediante Retrofit, representar datos JSON con modelos Kotlin y mostrar los resultados en Jetpack Compose. Implementar las operaciones de creación, lectura, actualización y eliminación tanto en la parte guiada de series como en una tabla del proyecto Dispensalud.

## 2. Herramientas y arquitectura

Se construyó una aplicación Android con Kotlin, Jetpack Compose, Material 3, Navigation Compose, Retrofit, Gson y corutinas. La API local se implementó con Django REST Framework y una base de datos SQLite. Android Studio permite abrir, compilar y ejecutar la aplicación en el emulador.

La arquitectura separa la interfaz, el estado y el acceso a datos. Las pantallas muestran el estado del ViewModel. El ViewModel llama al repositorio, que valida los datos y utiliza la interfaz Retrofit. Retrofit transforma las funciones Kotlin en solicitudes HTTP y Gson convierte las respuestas JSON en objetos. Django recibe cada petición, valida el contenido y realiza la operación en SQLite.

```text
Pantalla Compose → ViewModel → Repositorio → Retrofit
                                            ↓ HTTP/JSON
                                      Django REST Framework
                                            ↓
                                           SQLite
```

El archivo `SerieModel.kt` representa los datos de una serie. La anotación `SerializedName("release_date")` mantiene la correspondencia entre el nombre del campo JSON y la propiedad `releaseDate` de Kotlin. `SerieApiService.kt` declara las operaciones HTTP. `ApiClient.kt` configura la URL base y el convertidor Gson. `CrudViewModel.kt` controla la carga, el formulario, los errores y las operaciones pendientes. `ScreenSeries.kt` muestra el listado, la edición y la confirmación para eliminar. `SeriesApp.kt` organiza la navegación.

## 3. Parte guiada: CRUD de series

### Configuración de conexión

Se agregó el permiso `android.permission.INTERNET` al manifiesto. Se creó `network_security_config.xml` para permitir HTTP únicamente hacia las direcciones locales usadas en las pruebas. El servidor se ejecuta en el puerto 8000. La URL `http://10.0.2.2:8000/` permite que el emulador acceda a la PC anfitriona; `localhost` dentro del emulador representa al propio Android.

Django incluye `127.0.0.1`, `localhost` y `10.0.2.2` en los hosts admitidos. Para un dispositivo físico se documentaron dos alternativas: redirección USB con `adb reverse` o configuración de la dirección IP de la PC. Como el emulador usa Android 17 y la app apunta a SDK 37, también se agregó el permiso `ACCESS_LOCAL_NETWORK` y una pantalla para solicitarlo antes de acceder a los CRUD. Este requisito adicional se verificó en la documentación actual de Android: https://developer.android.com/privacy-and-security/local-network-permission.

### Operaciones realizadas

| Operación | Solicitud | Comportamiento esperado |
|---|---|---|
| Listar | `GET /serie` | Recibir un arreglo JSON y mostrar tarjetas |
| Consultar | `GET /serie/{id}` | Cargar los valores antes de editar |
| Crear | `POST /serie` | Persistir una serie y recibir HTTP 201 |
| Actualizar | `PUT /serie/{id}` | Guardar los cambios y recibir HTTP 200 |
| Eliminar | `DELETE /serie/{id}` | Borrar el registro y recibir HTTP 204 |

El formulario solicita nombre, fecha de estreno, puntuación y categoría. Se valida que los campos no estén vacíos, que la fecha exista y que la puntuación sea un número entero entre 0 y 10. El identificador es asignado por el servidor. Para eliminar se solicita confirmación; cancelar mantiene el registro.

La aplicación espera la respuesta HTTP antes de cerrar el formulario y actualizar el listado. Si el servidor falla, se informa el problema y se conservan los datos escritos. DELETE utiliza una respuesta sin contenido, coherente con el código HTTP 204.

**Insertar evidencias:** listado, formulario, modificación de fecha, confirmación de eliminación y enlace del video. Consultar la carpeta de evidencias y `VERIFICACION.md` para el estado real de las pruebas.

## 4. Ejercicio: Dispensalud

Se eligió la tabla de medicamentos del proyecto DispensaludCRUD. Se conservaron sus campos: `id`, `nombre`, `dosis`, `frecuencia` y `cantidad`. La versión de laboratorio utiliza una API independiente para practicar Retrofit; no modifica el proyecto original ni datos reales.

Se implementaron las rutas `/medicamento` y `/medicamento/{id}`. El usuario puede registrar un elemento de prueba, consultar el inventario, editar sus campos y eliminarlo. La cantidad debe ser un entero no negativo. La validación ocurre tanto en Android como en el servidor.

Los campos de dosis y frecuencia almacenan texto de demostración. La función desarrollada es un CRUD de inventario; no calcula ni recomienda tratamientos.

**Insertar evidencias:** creación, actualización de cantidad, eliminación y enlace al video del ejercicio.

## 5. Actividad adicional: Bookshelf

Se implementó una sección que consulta Google Books usando Retrofit y Gson. Incluye búsqueda por texto, una cuadrícula de libros, descarga de portadas mediante Coil y consulta de detalle al seleccionar un libro. El repositorio está separado de la interfaz y cuenta con una prueba de contrato usando un servidor de prueba inyectado.

En la comprobación de conectividad realizada durante el desarrollo, Google Books respondió HTTP 429. Esto indica una limitación de solicitudes del servicio. La aplicación muestra el error y permite intentar otra búsqueda posteriormente. **No presentar esa respuesta de error como evidencia de libros cargados correctamente.** La demostración con datos reales queda sujeta a que Google Books responda satisfactoriamente.

Referencia: https://developer.android.com/codelabs/basic-android-kotlin-compose-bookshelf

## 6. Pruebas y control de versiones

Se añadieron pruebas del backend para verificar el CRUD, la persistencia de cambios, la eliminación, los datos inválidos y el comportamiento de un identificador inexistente. Se añadieron pruebas Android de contrato HTTP y pruebas de interfaz contra la API local. Los resultados finales deben tomarse de `VERIFICACION.md`, no de la sola existencia de archivos de pruebas.

Los avances se registraron mediante commits locales con mensajes descriptivos. Antes de entregar se debe subir el repositorio remoto y verificar que el historial esté visible. **Commits locales y commits subidos son evidencias distintas.**

**Insertar:** enlace del repositorio, captura del historial remoto y resultados de pruebas.

## 7. Preguntas de observación

### Parte más desafiante

Propuesta técnica para adaptar a tu experiencia:

“Una parte que requirió especial cuidado fue coordinar las operaciones de red con la interfaz. El ejemplo podía regresar al listado antes de que terminara el guardado. Eso podía mostrar datos antiguos o dar la impresión de que una operación había funcionado aunque el servidor hubiera respondido con un error. La solución fue realizar la solicitud dentro de una corutina del ViewModel, comprobar la respuesta y actualizar la pantalla únicamente después del éxito. También se bloquearon los botones mientras la operación estaba pendiente y se añadieron mensajes de error.”

Usa este texto solo si puedes explicar el problema y la solución. Puedes sustituirlo por otra dificultad que hayas experimentado personalmente, como la configuración de `10.0.2.2` o la validación de fechas.

### Uso de inteligencia artificial

Texto base transparente:

“Utilicé IA como apoyo para analizar la guía, preparar la estructura del proyecto, implementar el CRUD, identificar problemas del código de ejemplo y generar pruebas y documentación. La asistencia también ayudó a revisar la conexión entre Android y Django. Antes de entregar, debo revisar el código, reproducir las operaciones y adaptar este informe a lo que comprendo y pude comprobar. El uso de IA forma parte del proceso y no se presenta como trabajo realizado sin asistencia.”

## 8. Conclusiones

Retrofit permite expresar las operaciones de una API como funciones Kotlin. La separación entre UI, ViewModel y repositorio facilita manejar estados de carga y errores. La validación del cliente mejora la interacción, mientras que la validación del servidor protege la integridad de los datos. La comprobación del CRUD requiere verificar que los cambios se reflejen en el servidor y que la interfaz muestre el resultado correcto.

La entrega debe distinguir el desarrollo implementado, las pruebas efectivamente superadas y las dependencias externas pendientes, como la cuota de Google Books o la subida del repositorio.

## Referencias

1. Guía GLAB-S10-BPAREJA-2026-01 (1).pdf, proporcionada para el laboratorio.
2. Django REST Framework, Generic views: https://www.django-rest-framework.org/api-guide/generic-views/
3. Android Developers, Project: Create a Bookshelf App: https://developer.android.com/codelabs/basic-android-kotlin-compose-bookshelf
4. Retrofit: https://github.com/square/retrofit
