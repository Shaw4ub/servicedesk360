<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nuevo ticket | ServiceDesk 360</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/estilos.css">
</head>
<body>
    <main class="contenedor-formulario">
        <h1>Nuevo ticket</h1>

        <c:if test="${not empty error}">
            <div class="alerta error">
                <p><c:out value="${error}" /></p>
            </div>
        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/tickets"
              autocomplete="off">
            <input type="hidden" name="accion" value="guardar">

            <div class="grupo-campo">
                <label for="idCliente">Cliente</label>
                <select id="idCliente" name="idCliente" required
                        onchange="cargarEquipos()">
                    <option value="">Seleccione...</option>
                    <c:forEach var="c" items="${clientes}">
                        <option value="${c.idCliente}"
                                ${param.idCliente == c.idCliente ? 'selected' : ''}>
                            <c:out value="${c.nombre}" />
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="grupo-campo">
                <label for="idEquipo">Equipo</label>
                <select id="idEquipo" name="idEquipo" required
                        data-seleccionado="${fn:escapeXml(param.idEquipo)}">
                    <option value="">Seleccione primero el cliente...</option>
                    <c:forEach var="e" items="${equipos}">
                        <option value="${e.idEquipo}">
                            <c:out value="${e.descripcion}" />
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="grupo-campo">
                <label for="idCategoria">Categoría</label>
                <select id="idCategoria" name="idCategoria" required>
                    <option value="">Seleccione...</option>
                    <c:forEach var="cat" items="${categorias}">
                        <option value="${cat.idCategoria}"
                                ${param.idCategoria == cat.idCategoria ? 'selected' : ''}>
                            <c:out value="${cat.nombre}" />
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="grupo-campo">
                <label for="idTecnico">Técnico</label>
                <select id="idTecnico" name="idTecnico" required>
                    <option value="">Seleccione...</option>
                    <c:forEach var="tec" items="${tecnicos}">
                        <option value="${tec.idTecnico}"
                                ${param.idTecnico == tec.idTecnico ? 'selected' : ''}>
                            <c:out value="${tec.nombre}" />
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="grupo-campo">
                <label for="titulo">Título</label>
                <input type="text" id="titulo" name="titulo"
                       value="${fn:escapeXml(param.titulo)}"
                       minlength="5" maxlength="150" required>
            </div>

            <div class="grupo-campo">
                <label for="descripcion">Descripción</label>
                <textarea id="descripcion" name="descripcion"
                          rows="4" maxlength="1000" required><c:out value="${param.descripcion}" /></textarea>
            </div>

            <div class="grupo-campo">
                <label for="prioridad">Prioridad</label>
                <select id="prioridad" name="prioridad" required>
                    <option value="BAJA" ${param.prioridad == 'BAJA' ? 'selected' : ''}>BAJA</option>
                    <option value="MEDIA" ${param.prioridad == 'MEDIA' ? 'selected' : ''}>MEDIA</option>
                    <option value="ALTA" ${param.prioridad == 'ALTA' ? 'selected' : ''}>ALTA</option>
                </select>
            </div>

            <div class="grupo-campo">
                <label for="detalleInicial">Seguimiento inicial</label>
                <textarea id="detalleInicial" name="detalleInicial"
                          rows="3" maxlength="800" required><c:out value="${param.detalleInicial}" /></textarea>
            </div>

            <div class="acciones-formulario">
                <button type="submit">Registrar ticket</button>
                <a class="boton"
                   style="background: #e0e0e0;"
                   href="${pageContext.request.contextPath}/tickets">
                    Cancelar
                </a>
            </div>
        </form>
    </main>

    <script>
        function cargarEquipos() {
            var cliente = document.getElementById('idCliente').value;
            var select = document.getElementById('idEquipo');
            var seleccionado = select.getAttribute('data-seleccionado');

            select.innerHTML = '<option value="">Seleccione...</option>';
            if (!cliente) {
                return;
            }

            fetch('${pageContext.request.contextPath}/tickets?accion=equipos&idCliente='
                    + encodeURIComponent(cliente))
                .then(function (respuesta) {
                    return respuesta.json();
                })
                .then(function (equipos) {
                    equipos.forEach(function (equipo) {
                        var opcion = document.createElement('option');
                        opcion.value = equipo.idEquipo;
                        opcion.textContent = equipo.descripcion;
                        select.appendChild(opcion);
                    });
                    if (seleccionado) {
                        select.value = seleccionado;
                    }
                })
                .catch(function () {
                    // Si falla la carga, el usuario puede reintentar.
                });
        }

        document.addEventListener('DOMContentLoaded', function () {
            if (document.getElementById('idCliente').value) {
                cargarEquipos();
            }
        });
    </script>
</body>
</html>
