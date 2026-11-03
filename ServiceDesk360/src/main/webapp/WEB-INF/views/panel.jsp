<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Panel | ServiceDesk 360</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/resources/estilos.css">
    </head>
    <body>
        <main class="panel-usuario">
            <h1>Bienvenido, <c:out value="${sessionScope.usuarioNombre}" /></h1>
            <p>Correo: <c:out value="${sessionScope.usuarioCorreo}" /></p>
            <p>Rol: <c:out value="${sessionScope.usuarioRolDescripcion}" /></p>
            <div class="tarjetas-panel">
                <section class="tarjeta-panel">
                    <h2>Solicitudes y Tickets</h2>
                    <p>Módulo para apertura y seguimiento de incidencias de soporte.</p>
                    <p><a class="boton" href="${pageContext.request.contextPath}/tickets">Gestionar tickets</a></p>
                </section>
                <section class="tarjeta-panel">
                    <h2>Clientes</h2>
                    <p>Registro y administración de clientes de soporte.</p>
                    <p><a class="boton" href="${pageContext.request.contextPath}/clientes">Gestionar clientes</a></p>
                </section>
                <section class="tarjeta-panel">
                    <h2>Equipos</h2>
                    <p>Registro y seguimiento de activos tecnológicos.</p>
                </section>
                <section class="tarjeta-panel">
                    <h2>Reportes</h2>
                    <p>Se incorporarán durante la Unidad 5.</p>
                </section>
            </div>
            <form action="${pageContext.request.contextPath}/cerrar-sesion"
                  method="post" style="margin-top: 24px;">
                <button type="submit">Cerrar sesión</button>
            </form>
        </main>
    </body>
</html>
