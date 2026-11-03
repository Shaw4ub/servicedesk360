<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty cliente.idCliente ? 'Nuevo' : 'Editar'} cliente | ServiceDesk 360</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/estilos.css">
</head>
<body>
    <main class="contenedor-formulario">
        <h1>${empty cliente.idCliente ? 'Nuevo cliente' : 'Editar cliente'}</h1>

        <c:if test="${not empty error}">
            <div class="alerta error">
                <p><c:out value="${error}" /></p>
            </div>
        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/clientes">
            <input type="hidden" name="accion" value="guardar">
            <input type="hidden" name="idCliente"
                   value="${cliente.idCliente}">

            <div class="grupo-campo">
                <label for="nombre">Nombre</label>
                <input type="text" id="nombre" name="nombre"
                       value="${fn:escapeXml(cliente.nombre)}"
                       required maxlength="120">
            </div>

            <div class="grupo-campo">
                <label for="correo">Correo</label>
                <input type="email" id="correo" name="correo"
                       value="${fn:escapeXml(cliente.correo)}"
                       required maxlength="160">
            </div>

            <div class="acciones-formulario">
                <button type="submit">Guardar</button>
                <a class="boton"
                   style="background: #e0e0e0;"
                   href="${pageContext.request.contextPath}/clientes">
                    Cancelar
                </a>
            </div>
        </form>
    </main>
</body>
</html>
