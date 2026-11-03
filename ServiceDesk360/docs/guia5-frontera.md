# Guía 5 - Frontera con la Guía 6

## Clases temporales o exploratorias

Las siguientes clases se crearon para practicar JDBC y no forman todavía el
DAO definitivo:

- `persistence/ConexionBD.java`: abre conexiones a partir del archivo externo
  `~/.servicedesk360/db.properties`.
- `persistence/ConsultasLaboratorioJDBC.java`: contiene las consultas y la
  transacción de laboratorio.
- `persistence/ClienteRegistro.java` y `persistence/TicketVista.java`:
  estructuras de lectura temporales.
- `persistence/PruebaConexion.java` y `persistence/PruebaLaboratorio.java`:
  clases ejecutables de comprobación.

La aplicación web sigue trabajando con `DirectorioTicketsEnMemoria`; la
integración JDBC con los controladores y servicios se realizará en la Guía 6.

## Contratos de la Guía 4 que deben cambiar

- `RegistradorTickets.guardar(...)` devuelve `void` y asigna el identificador
  con `siguienteId()`. Con MySQL la clave la genera la base de datos, por lo
  que el contrato debe devolver el `id` generado o recibirlo después de la
  inserción.
- `BuscadorTickets.listarTodos()` devuelve `TicketSoporte` armados en memoria.
  En la Guía 6 deberá devolver datos provenientes de un DAO, incluyendo los
  nombres de cliente y categoría obtenidos mediante JOIN.
- El servicio ya no debe mantener una secuencia propia de identificadores.

## Operaciones que deben pasar a interfaces DAO

- Búsqueda de clientes por correo e inserción de clientes.
- Registro transaccional de ticket y seguimiento.
- Listado de tickets con cliente y categoría.
- Conteos y consultas de verificación.
- En general, toda sentencia SQL debe quedar fuera de los servlets y del
  servicio de negocio.

## Entidad seleccionada para el primer CRUD completo

`TicketSoporte` (tabla `tickets`), porque es la entidad central del caso y ya
cuenta con listado y registro desde la interfaz web.

## Manejo de excepciones

- La capa de persistencia conserva el `SQLException` o lo traduce a una
  excepción de persistencia con contexto, sin incluir credenciales.
- Los detalles técnicos se registran con `java.util.logging` para diagnóstico.
- El servicio recibe el fallo y lo convierte en un mensaje genérico para la
  vista, sin exponer la causa SQL ni la contraseña.
- La conexión, los `PreparedStatement` y los `ResultSet` se cierran siempre con
  try-with-resources; no se usan conexiones estáticas globales.

## Comprobaciones de error y limpieza

- Credenciales inválidas: `ConexionBD.abrir()` propaga un `SQLException`
  controlado y registra el fallo sin mostrar la contraseña.
- Servidor detenido: `PruebaConexion` termina con error de conexión; al
  reiniciar MySQL vuelve a funcionar sin cambiar código.
- Recursos: todas las operaciones de `ConsultasLaboratorioJDBC` usan
  try-with-resources y abren una conexión por operación.
