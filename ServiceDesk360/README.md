# ServiceDesk 360

Aplicación web Java para la gestión de soporte técnico. El proyecto integra
las guías 5, 6, 7 y 8: persistencia JDBC, CRUD de clientes con Servlet/JSP,
gestión de tickets y una primera migración a JSF con Managed Beans.

## Tecnologías

- Java 8 como nivel de compilación (`maven.compiler.source/target=1.8`).
- Apache Tomcat 9.0.120.
- MySQL 8.0.37 y MySQL Connector/J 8.0.33.
- Maven, JSP, JSTL y Servlet 4.0.
- JSF 2.3 con Mojarra 2.3.21 (`javax.faces.*`).
- CDI 2.0 con Weld Servlet 3.1.9 (`@Named`, `@ViewScoped`).
- `jakarta.jakartaee-api:8.0.0` en alcance `provided` para compilar las APIs
  `javax.*`; el servidor aporta Servlet/JSP y el WAR aporta Mojarra y Weld.

> Importante: Tomcat 9 implementa las APIs `javax.*`. Por eso este proyecto
> usa Mojarra 2.3.x y Facelets 2.3, no JSF 4 (`jakarta.faces.*`). Si se migra
> a Tomcat 10.1 o superior, se deben cambiar paquetes, namespaces y
> dependencias a Jakarta EE 9+.

## Requisitos

- JDK 25 instalado (Tomcat se ejecuta actualmente con
  `C:\Program Files\Java\jdk-25`).
- `JAVA_HOME` definido para ejecutar los scripts de Tomcat.
- MySQL con la base `servicedesk360_db`.
- Archivo de configuración local:
  `C:\Users\shaw4\.servicedesk360\db.properties`.

## Compilar y desplegar

```powershell
cd C:\servicedesk360\ServiceDesk360
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
& "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" clean package -DskipTests
```

Tomcat ya tiene un contexto que apunta al artefacto explotado:

```xml
<Context docBase="C:\servicedesk360\ServiceDesk360\target\ServiceDesk360-1.0-SNAPSHOT"
         path="/ServiceDesk360"/>
```

Reiniciar Tomcat si se cambió `web.xml`, `faces-config.xml`, `beans.xml` o se
reemplazó el contenido de `target`:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
& "C:\apache-tomcat-9.0.120\bin\shutdown.bat"
& "C:\apache-tomcat-9.0.120\bin\startup.bat"
```

## Rutas principales

| Módulo | URL | Tecnología |
|---|---|---|
| Inicio anterior | `/ServiceDesk360/` | JSP |
| Portada JSF | `/ServiceDesk360/index.xhtml` | JSF/Facelets |
| Clientes JSF | `/ServiceDesk360/views/clientes/lista.xhtml` | JSF + CDI |
| Formulario JSF | `/ServiceDesk360/views/clientes/formulario.xhtml` | JSF + CDI |
| Tickets JSF (solo lectura) | `/ServiceDesk360/views/tickets/lista.xhtml` | JSF + CDI |
| Clientes JSP/Servlet | `/ServiceDesk360/clientes` | Servlet/JSP |
| Tickets JSP/Servlet | `/ServiceDesk360/tickets` | Servlet/JSP |
| Panel autenticado | `/ServiceDesk360/panel` | Servlet/JSP |

El módulo JSP/Servlet de clientes se conserva temporalmente para comparar el
enfoque tradicional con JSF. Ambos módulos reutilizan `ClienteDAOImpl` y
`Cliente`.

## Módulo JSF de la Guía 8

- `ClienteBean`: bean `@Named("clienteBean")` y `@ViewScoped`; lista, crea,
  edita, desactiva y reactiva clientes. No contiene SQL.
- `TicketBean`: bean de solo lectura que reutiliza
  `TicketDAO.listarDetalle(...)` y `TicketDetalleDTO`.
- `index.xhtml`: portada con acceso a clientes y tickets.
- `views/clientes/lista.xhtml`: `h:dataTable`, acciones por fila y mensajes.
- `views/clientes/formulario.xhtml`: alta/edición con `f:viewParam` y
  `f:viewAction`; usa `required` y validación de correo único.
- `views/tickets/lista.xhtml`: tabla de tickets alimentada por el JOIN de la
  Guía 7.
- `WEB-INF/faces-config.xml`: navegación explícita desde la portada.
- `WEB-INF/beans.xml`: descubrimiento CDI en modo `annotated`.
- `WEB-INF/web.xml`: `FacesServlet` mapeado a `*.xhtml`.

### Decisión sobre la ubicación de las vistas

La guía propone `WEB-INF/views`. En Tomcat, un postback de JSF usa como
`action` la ruta de la vista actual; si la vista está bajo `WEB-INF`, el
navegador intenta enviar el formulario a `/WEB-INF/...` y Tomcat responde
404. Por eso las vistas XHTML de este proyecto se publican en
`/views` y la navegación usa `faces-redirect=true`. Los JSP que deben quedar
fuera del acceso directo continúan en `WEB-INF/vistas`.

Esta decisión está explicada con más detalle en
[`docs/guia8-jsf-managed-beans.md`](docs/guia8-jsf-managed-beans.md).

## Datos de prueba de la Guía 8

Se registró el cliente de prueba:

| id_cliente | nombre | correo | activo |
|---:|---|---|---:|
| 7 | Cliente JSF Demo Editado | cliente.jsf.demo@demo.local | 1 |

La edición y la persistencia se comprobaron después de reiniciar Tomcat.

## Documentación

- [`docs/guia7.docx`](docs/guia7.docx): documentación de la Guía 7.
- [`docs/guia7-datos-prueba.sql`](docs/guia7-datos-prueba.sql): datos de
  prueba del módulo de tickets.
- [`docs/guia8-jsf-managed-beans.md`](docs/guia8-jsf-managed-beans.md):
  migración a JSF, decisiones técnicas, comparación Servlet/JSP vs JSF,
  pruebas y guion de capturas.
- [`docs/guia8-jsf-managed-beans.docx`](docs/guia8-jsf-managed-beans.docx):
  versión Word con las capturas integradas, lista para el informe.
- [`docs/capturas/`](docs/capturas/): capturas generadas durante la
  validación.

## Limitaciones conocidas

- La prioridad `CRÍTICA` sigue sin estar permitida por la restricción
  `tickets.chk_ticket_prioridad` de MySQL; el usuario `servicedesk_app` no
  tiene privilegios `ALTER` para modificarla.
- Al detener Tomcat puede aparecer un aviso de cierre de Mojarra/Weld. No
  afecta el arranque ni el funcionamiento de la aplicación; es un problema de
  orden de destrucción de listeners.
