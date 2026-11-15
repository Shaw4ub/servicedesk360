<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalle del ticket | ServiceDesk 360</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/estilos.css">
</head>
<body>
    <main class="panel-usuario">
        <h1>Ticket #<c:out value="${ticket.idTicket}" /></h1>

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

        <table border="1"
               style="width: 100%; border-collapse: collapse; background: white;">
            <tbody>
                <tr>
                    <th style="padding: 8px; text-align: left; width: 180px;">Cliente</th>
                    <td style="padding: 8px;"><c:out value="${ticket.cliente}" /></td>
                </tr>
                <tr>
                    <th style="padding: 8px; text-align: left;">Equipo</th>
                    <td style="padding: 8px;"><c:out value="${ticket.equipo}" /></td>
                </tr>
                <tr>
                    <th style="padding: 8px; text-align: left;">Categoría</th>
                    <td style="padding: 8px;"><c:out value="${ticket.categoria}" /></td>
                </tr>
                <tr>
                    <th style="padding: 8px; text-align: left;">Técnico</th>
                    <td style="padding: 8px;"><c:out value="${ticket.tecnico}" /></td>
                </tr>
                <tr>
                    <th style="padding: 8px; text-align: left;">Título</th>
                    <td style="padding: 8px;"><c:out value="${ticket.titulo}" /></td>
                </tr>
                <tr>
                    <th style="padding: 8px; text-align: left;">Prioridad</th>
                    <td style="padding: 8px;"><c:out value="${ticket.prioridad}" /></td>
                </tr>
                <tr>
                    <th style="padding: 8px; text-align: left;">Estado</th>
                    <td style="padding: 8px;"><c:out value="${ticket.estado}" /></td>
                </tr>
                <tr>
                    <th style="padding: 8px; text-align: left;">Fecha de creación</th>
                    <td style="padding: 8px;"><c:out value="${ticket.fechaCreacion}" /></td>
                </tr>
            </tbody>
        </table>

        <h2 style="margin-top: 28px;">Seguimientos</h2>
        <c:choose>
            <c:when test="${empty seguimientos}">
                <p>No hay seguimientos registrados.</p>
            </c:when>
            <c:otherwise>
                <table border="1"
                       style="width: 100%; border-collapse: collapse; background: white;">
                    <thead>
                        <tr style="background: #eee; text-align: left;">
                            <th style="padding: 8px;">ID</th>
                            <th style="padding: 8px;">Detalle</th>
                            <th style="padding: 8px;">Fecha</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="s" items="${seguimientos}">
                            <tr>
                                <td style="padding: 8px;"><c:out value="${s.idSeguimiento}" /></td>
                                <td style="padding: 8px;"><c:out value="${s.detalle}" /></td>
                                <td style="padding: 8px;"><c:out value="${s.fechaRegistro}" /></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <h2 style="margin-top: 28px;">Cambiar estado</h2>
        <p>
            El estado esperado se envía como control de concurrencia.
            Si otra operación ya modificó el ticket, se mostrará un aviso.
        </p>
        <form method="post"
              action="${pageContext.request.contextPath}/tickets"
              style="margin-top: 8px;">
            <input type="hidden" name="accion" value="cambiarEstado">
            <input type="hidden" name="idTicket" value="${ticket.idTicket}">
            <input type="hidden" name="estadoEsperado" value="${ticket.estado}">

            <label for="nuevoEstado">Nuevo estado</label>
            <select id="nuevoEstado" name="nuevoEstado" required>
                <option value="ABIERTO">ABIERTO</option>
                <option value="ASIGNADO">ASIGNADO</option>
                <option value="EN_PROCESO">EN_PROCESO</option>
                <option value="CERRADO">CERRADO</option>
            </select>
            <button type="submit">Actualizar estado</button>
        </form>

        <p style="margin-top: 24px;">
            <a class="boton"
               href="${pageContext.request.contextPath}/tickets">
                Volver al listado
            </a>
            <a class="boton"
               style="background: #e0e0e0; margin-left: 10px;"
               href="${pageContext.request.contextPath}/panel">
                Volver al panel
            </a>
        </p>
    </main>
</body>
</html>
