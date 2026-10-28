# Verificación del laboratorio

## Comprobado

| Componente | Resultado |
|---|---|
| APK Android | `assembleDebug`: compilación correcta; APK instalado en Pixel 7 |
| Backend | 5 pruebas Django aprobadas |
| Contratos Android | 5 pruebas JVM aprobadas: series, medicamentos, validación, fallo HTTP y Bookshelf |
| Análisis Android | `lintDebug`: sin errores; avisos sobre versiones de dependencias y compatibilidad |
| API real | 13 solicitudes verificadas; creación, consulta, actualización, eliminación y 404 posterior |
| Interfaz de series | 1 prueba completa aprobada contra Django; incluye validación y cancelar eliminación |
| Interfaz de Dispensalud | 1 prueba completa aprobada contra Django; cantidad actualizada a 35 y eliminación |
| Evidencias | Videos de ambos CRUD y capturas de la app real |

Las pruebas de interfaz se ejecutaron en el emulador Pixel 7 con Android 37.1. Fue necesario iniciar sin restaurar snapshot, asignar 4096 MB de RAM, usar Espresso 3.7.0 y conceder `ACCESS_LOCAL_NETWORK`. Las primeras ejecuciones fallidas ayudaron a detectar estas condiciones y no se presentan como resultados aprobados.

## Evidencias incluidas en el repositorio

En `evidencias/`:

- `01-CRUD-series.mp4`: recorrido real por el CRUD de series.
- `02-CRUD-dispensalud.mp4`: recorrido real por el CRUD de medicamentos.
- `capturas/`: listado, formulario, actualización y confirmaciones.
- `api-crud-verificado.json`: peticiones y respuestas de la API local.
- `pruebas-contrato-android.xml`: reporte de las cinco pruebas JVM.
- `prueba-interfaz-series.txt` y `prueba-interfaz-dispensalud.txt`: resultados de instrumentación.
- `lint-android.html`: reporte de análisis estático.

Los videos no tienen narración y se recortaron para retirar la espera de arranque y la pantalla de inicio del sistema. No se sustituyeron pantallas ni se simularon respuestas exitosas.

## Pendientes de entrega

1. **Acceso docente:** repositorio creado en https://github.com/luisgustavosalcedo53-sys/lab10-android-retrofit-dispensalud. Es privado: conceder acceso al docente antes de entregar el enlace. La publicación del historial se verifica comparando el commit local con `origin/main`.
2. **Bookshelf con datos reales:** implementado y probado con un servicio de prueba inyectado. La API pública devolvió HTTP 429 en las consultas reales. La carga de libros y portadas y su video adicional siguen pendientes de una respuesta exitosa del servicio.
3. **Informe personal:** el borrador separado contiene explicación técnica, reflexión propuesta y uso de IA. Completar datos personales, adaptar la observación y añadir enlaces definitivos.
4. **Puntualidad y calificación:** dependen del plazo y de la evaluación docente; no se pueden garantizar desde el código.

No se modificó el proyecto Dispensalud original. La API creada para este laboratorio trabaja con datos ficticios y una base independiente.
