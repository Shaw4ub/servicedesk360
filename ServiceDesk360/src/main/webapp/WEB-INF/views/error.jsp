<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error controlado</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/estilos.css">
</head>
<body>
    <main class="contenedor-formulario">
        <h1>No fue posible completar la operación</h1>
        <p><c:out value="${mensajeError}" /></p>
        <p><a href="${pageContext.request.contextPath}/panel">Volver al panel</a></p>
    </main>
</body>
</html>
