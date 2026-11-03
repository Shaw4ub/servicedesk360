<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Clientes | ServiceDesk 360</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/estilos.css">
</head>
<body>
    <main class="panel-usuario">
        <h1>Clientes</h1>

        <c:if test="${not empty sessionScope.flash}">
            <div class="alerta exito">
                <p><c:out value="${sessionScope.flash}" /></p>
            </div>
            <c:remove var="flash" scope="session" />
        </c:if>

        <c:if test="${not empty sessionScope.flashError}">
            <div class="alerta error">
                <p><c:out value="${sessionScope.flashError}" /></p>
            </div>
            <c:remove var="flashError" scope="session" />
        </c:if>

        <p>
            <a class="boton"
               href="${pageContext.request.contextPath}/clientes?accion=nuevo">
                Nuevo cliente
            </a>
            <a class="boton"
               style="background: #e0e0e0; margin-left: 10px;"
               href="${pageContext.request.contextPath}/panel">
                Volver al panel
            </a>
        </p>

        <table border="1"
               style="width: 100%; border-collapse: collapse; margin-top: 16px; background: white;">
            <thead>
                <tr style="background: #eee; text-align: left;">
                    <th style="padding: 10px;">ID</th>
                    <th style="padding: 10px;">Nombre</th>
                    <th style="padding: 10px;">Correo</th>
                    <th style="padding: 10px;">Estado</th>
                    <th style="padding: 10px;">Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${clientes}">
                    <tr>
                        <td style="padding: 10px;">
                            <c:out value="${c.idCliente}" />
                        </td>
                        <td style="padding: 10px;">
                            <c:out value="${c.nombre}" />
                        </td>
                        <td style="padding: 10px;">
                            <c:out value="${c.correo}" />
                        </td>
                        <td style="padding: 10px;">
                            <c:out value="${c.activo ? 'Activo' : 'Inactivo'}" />
                        </td>
                        <td style="padding: 10px;">
                            <a href="${pageContext.request.contextPath}/clientes?accion=editar&amp;id=${c.idCliente}">
                                Editar
                            </a>

                            <form method="post"
                                  action="${pageContext.request.contextPath}/clientes"
                                  style="display: inline;">
                                <input type="hidden" name="accion" value="estado">
                                <input type="hidden" name="id" value="${c.idCliente}">
                                <input type="hidden" name="activo" value="${!c.activo}">
                                <button type="submit">
                                    <c:out value="${c.activo ? 'Desactivar' : 'Activar'}" />
                                </button>
                            </form>

                            <form method="post"
                                  action="${pageContext.request.contextPath}/clientes"
                                  style="display: inline;"
                                  onsubmit="return confirm('¿Eliminar definitivamente este cliente? Use esta opción solo con registros de laboratorio sin historial.');">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${c.idCliente}">
                                <button type="submit"
                                        style="background: #b33b26; color: white;">
                                    Eliminar
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty clientes}">
                    <tr>
                        <td colspan="5"
                            style="padding: 16px; text-align: center; color: #777;">
                            No hay clientes registrados todavía.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </main>
</body>
</html>
