<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tickets | ServiceDesk 360</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/estilos.css">
</head>
<body>
    <main class="panel-usuario">
        <h1>Tickets de soporte</h1>

        <c:if test="${not empty mensajeExito}">
            <div class="alerta exito">
                <p><c:out value="${mensajeExito}" /></p>
                <c:if test="${not empty idCreado}">
                    <p>
                        <a href="${pageContext.request.contextPath}/tickets?accion=ver&amp;id=${idCreado}">
                            Ver el ticket registrado
                        </a>
                    </p>
                </c:if>
            </div>
        </c:if>

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
               href="${pageContext.request.contextPath}/tickets?accion=nuevo">
                Nuevo ticket
            </a>
            <a class="boton"
               style="background: #e0e0e0; margin-left: 10px;"
               href="${pageContext.request.contextPath}/panel">
                Volver al panel
            </a>
        </p>

        <form method="get"
              action="${pageContext.request.contextPath}/tickets"
              style="margin-top: 18px;">
            <label for="estado">Estado</label>
            <select id="estado" name="estado">
                <option value="">Todos</option>
                <option value="ABIERTO" ${estado == 'ABIERTO' ? 'selected' : ''}>ABIERTO</option>
                <option value="ASIGNADO" ${estado == 'ASIGNADO' ? 'selected' : ''}>ASIGNADO</option>
                <option value="EN_PROCESO" ${estado == 'EN_PROCESO' ? 'selected' : ''}>EN_PROCESO</option>
                <option value="CERRADO" ${estado == 'CERRADO' ? 'selected' : ''}>CERRADO</option>
            </select>

            <label for="prioridad" style="margin-left: 12px;">Prioridad</label>
            <select id="prioridad" name="prioridad">
                <option value="">Todas</option>
                <option value="BAJA" ${prioridad == 'BAJA' ? 'selected' : ''}>BAJA</option>
                <option value="MEDIA" ${prioridad == 'MEDIA' ? 'selected' : ''}>MEDIA</option>
                <option value="ALTA" ${prioridad == 'ALTA' ? 'selected' : ''}>ALTA</option>
            </select>

            <button type="submit" style="margin-left: 12px;">Filtrar</button>
            <a href="${pageContext.request.contextPath}/tickets"
               style="margin-left: 12px;">Limpiar</a>
        </form>

        <p style="margin-top: 16px;">
            Total de tickets: <strong><c:out value="${total}" /></strong>
            <c:if test="${totalPaginas > 0}">
                | Página <strong><c:out value="${pagina}" /></strong>
                de <strong><c:out value="${totalPaginas}" /></strong>
            </c:if>
        </p>

        <table border="1"
               style="width: 100%; border-collapse: collapse; margin-top: 8px; background: white;">
            <thead>
                <tr style="background: #eee; text-align: left;">
                    <th style="padding: 8px;">ID</th>
                    <th style="padding: 8px;">Cliente</th>
                    <th style="padding: 8px;">Equipo</th>
                    <th style="padding: 8px;">Categoría</th>
                    <th style="padding: 8px;">Técnico</th>
                    <th style="padding: 8px;">Título</th>
                    <th style="padding: 8px;">Prioridad</th>
                    <th style="padding: 8px;">Estado</th>
                    <th style="padding: 8px;">Fecha</th>
                    <th style="padding: 8px;">Acción</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="t" items="${tickets}">
                    <tr>
                        <td style="padding: 8px;"><c:out value="${t.idTicket}" /></td>
                        <td style="padding: 8px;"><c:out value="${t.cliente}" /></td>
                        <td style="padding: 8px;"><c:out value="${t.equipo}" /></td>
                        <td style="padding: 8px;"><c:out value="${t.categoria}" /></td>
                        <td style="padding: 8px;"><c:out value="${t.tecnico}" /></td>
                        <td style="padding: 8px;"><c:out value="${t.titulo}" /></td>
                        <td style="padding: 8px;"><c:out value="${t.prioridad}" /></td>
                        <td style="padding: 8px;"><c:out value="${t.estado}" /></td>
                        <td style="padding: 8px;"><c:out value="${t.fechaCreacion}" /></td>
                        <td style="padding: 8px;">
                            <a href="${pageContext.request.contextPath}/tickets?accion=ver&amp;id=${t.idTicket}">
                                Ver
                            </a>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty tickets}">
                    <tr>
                        <td colspan="10"
                            style="padding: 16px; text-align: center; color: #777;">
                            No hay tickets que coincidan con los filtros.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>

        <c:if test="${totalPaginas > 1}">
            <div style="margin-top: 16px;">
                <c:if test="${pagina > 1}">
                    <c:url var="anterior" value="/tickets">
                        <c:param name="pagina" value="${pagina - 1}" />
                        <c:param name="estado" value="${estado}" />
                        <c:param name="prioridad" value="${prioridad}" />
                    </c:url>
                    <a class="boton" href="${anterior}">Anterior</a>
                </c:if>

                <c:if test="${pagina < totalPaginas}">
                    <c:url var="siguiente" value="/tickets">
                        <c:param name="pagina" value="${pagina + 1}" />
                        <c:param name="estado" value="${estado}" />
                        <c:param name="prioridad" value="${prioridad}" />
                    </c:url>
                    <a class="boton" href="${siguiente}"
                       style="margin-left: 8px;">Siguiente</a>
                </c:if>
            </div>
        </c:if>
    </main>
</body>
</html>
