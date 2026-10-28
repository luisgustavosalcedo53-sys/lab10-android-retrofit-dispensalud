# Qué entregar y cómo sustentarlo

## Lista de revisión

- Proyecto Android abierto desde `Lab10/android` y servidor Django activo.
- Repositorio remoto con el historial de avances subido; copiar su enlace al informe.
- Video corto del CRUD de series: listar, agregar, editar la fecha, cancelar una eliminación y eliminar.
- Video del ejercicio Dispensalud: registrar un medicamento ficticio, modificar la cantidad y eliminarlo.
- Capturas donde se lean los datos y la respuesta de la app.
- Descripción del flujo Android → Retrofit → Django → SQLite.
- Respuesta personal sobre la parte más desafiante y explicación honesta del uso de IA.
- Bookshelf: video con libros y portadas reales cuando Google Books permita la consulta. Un error 429 no demuestra la carga correcta.
- Datos del alumno, grupo, ciclo y fecha. Revisar ortografía y enlaces antes de enviar.

## Guion sugerido para explicar series

1. Mostrar el servidor activo en `http://127.0.0.1:8000/` y el listado en `/serie`.
2. Explicar que el emulador usa `10.0.2.2` para llegar a la misma PC.
3. Abrir la app y entrar en Series. El listado se obtiene con GET.
4. Agregar un registro de prueba. Explicar que POST envía JSON y el servidor asigna el ID.
5. Editar la fecha. Explicar que PUT actualiza el registro identificado en la URL.
6. Cancelar la eliminación para mostrar que todavía existe.
7. Confirmar Eliminar. Explicar que DELETE devuelve 204 y se recarga la lista.

Para mostrar el procedimiento del PDF desde el navegador, el servidor ofrece la interfaz navegable de Django REST Framework. Abrir `/serie/2`, cambiar `release_date` con PUT y luego usar DELETE. Hacerlo únicamente sobre datos de demostración. Si se elimina una serie inicial, `python manage.py seed_demo` puede volver a crearla; su ID nuevo no tiene por qué ser 2.

## Guion sugerido para Dispensalud

Presentar los campos de la tabla y explicar que se adaptaron del proyecto DispensaludCRUD. Mostrar la creación de un dato ficticio, la edición de cantidad y su eliminación. Señalar que el almacenamiento está en la API del laboratorio y que no se está usando Room para estas operaciones.

## Preguntas que conviene poder responder

- ¿Para qué sirve `@SerializedName`? Para mapear un nombre JSON a una propiedad Kotlin.
- ¿Por qué se usa `suspend`? Para suspender la corutina mientras Retrofit espera la respuesta, sin bloquear la interfaz.
- ¿Por qué comprobar `isSuccessful`? Porque recibir una respuesta HTTP no implica que la operación haya tenido éxito.
- ¿Por qué DELETE usa `Unit`? Porque una respuesta 204 no contiene un objeto JSON.
- ¿Por qué validar también en Django? Porque el servidor puede recibir solicitudes de otros clientes y debe proteger sus datos.
- ¿Qué pasa si Django está apagado? La app muestra un error de conexión y permite reintentar; no anuncia un guardado exitoso.
- ¿Por qué tener un repositorio? Para separar el acceso a datos de la pantalla y facilitar las pruebas con dependencias sustituidas.

## Sobre las evidencias generadas

Las capturas y grabaciones automáticas, si están presentes en la carpeta de evidencias, muestran la app real ejecutándose contra Django. No incluyen narración personal. Puedes usarlas como apoyo o volver a grabar explicando el procedimiento con tus palabras. Consultar `Lab10/VERIFICACION.md` antes de afirmar que una prueba o una subida fue realizada.
