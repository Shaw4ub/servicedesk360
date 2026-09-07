<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tickets</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/estilos.css">
</head>
<body>
    <main class="panel-usuario">
        <h1>Tickets de soporte</h1>
        <c:if test="${not empty mensajeExito}">
            <div class="alerta exito"><p><c:out value="${mensajeExito}" /></p></div>
        </c:if>
        <p>
            <a class="boton" href="${pageContext.request.contextPath}/tickets/nuevo">Abrir nuevo ticket</a> 
            <a class="boton" style="background: #e0e0e0; margin-left: 10px;" href="${pageContext.request.contextPath}/panel">Volver al panel</a>
        </p>
        <table border="1" style="width: 100%; border-collapse: collapse; margin-top: 16px; background: white;">
            <thead>
                <tr style="background: #eee; text-align: left;">
                    <th style="padding: 10px;">ID</th>
                    <th style="padding: 10px;">Título</th>
                    <th style="padding: 10px;">Solicitante</th>
                    <th style="padding: 10px;">Prioridad</th>
                    <th style="padding: 10px;">Estado</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="ticket" items="${tickets}">
                    <tr>
                        <td style="padding: 10px;"><c:out value="${ticket.id}" /></td>
                        <td style="padding: 10px;"><c:out value="${ticket.titulo}" /></td>
                        <td style="padding: 10px;"><c:out value="${ticket.solicitante.nombreCompleto}" /></td>
                        <td style="padding: 10px;"><c:out value="${ticket.prioridad}" /></td>
                        <td style="padding: 10px;"><c:out value="${ticket.estado}" /></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty tickets}">
                    <tr>
                        <td colspan="5" style="padding: 16px; text-align: center; color: #777;">No hay tickets registrados todavía.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </main>
</body>
</html>
