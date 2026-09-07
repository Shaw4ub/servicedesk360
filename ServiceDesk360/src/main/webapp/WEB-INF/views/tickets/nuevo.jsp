<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Nuevo ticket</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/estilos.css">
</head>
<body>
    <main class="contenedor-formulario">
        <h1>Registrar ticket de soporte</h1>
        <c:if test="${not empty errores}">
            <div class="alerta error">
                <ul>
                    <c:forEach var="error" items="${errores}">
                        <li><c:out value="${error}" /></li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        <form method="post"
              action="${pageContext.request.contextPath}/tickets/nuevo">
            <div class="grupo-campo">
                <label>Título</label>
                <input type="text" name="titulo" maxlength="100"
                       value="${tituloAnterior}" required>
            </div>
            <div class="grupo-campo">
                <label>Descripción</label>
                <textarea name="descripcion" rows="6" style="width: 100%; padding: 11px 12px; border: 1px solid #b9b9b9; border-radius: 8px;" required><c:out value="${descripcionAnterior}" /></textarea>
            </div>
            <div class="grupo-campo">
                <label>Prioridad</label>
                <select name="prioridad" required>
                    <option value="">Seleccione</option>
                    <option value="BAJA" ${prioridadAnterior == 'BAJA' ? 'selected' : ''}>Baja</option>
                    <option value="MEDIA" ${prioridadAnterior == 'MEDIA' ? 'selected' : ''}>Media</option>
                    <option value="ALTA" ${prioridadAnterior == 'ALTA' ? 'selected' : ''}>Alta</option>
                    <option value="CRITICA" ${prioridadAnterior == 'CRITICA' ? 'selected' : ''}>Crítica</option>
                </select>
            </div>
            <div class="acciones-formulario">
                <button type="submit">Registrar ticket</button>
            </div>
        </form>
        <p style="margin-top: 16px;"><a href="${pageContext.request.contextPath}/tickets">Volver al listado</a></p>
    </main>
</body>
</html>
