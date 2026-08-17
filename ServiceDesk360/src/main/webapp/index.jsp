<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>ServiceDesk 360</title>
        <link rel="stylesheet"
              href="resources/estilos.css">
    </head>
    <body>
        <header class="encabezado">
            <h1>ServiceDesk 360</h1>
            <p>Plataforma empresarial de gestión de soporte técnico</p>
        </header>
        <main class="contenedor">
            <h2>Primera aplicación web desplegada</h2>
            <p>
                Esta página confirma que el proyecto JSP fue publicado
                correctamente en Apache Tomcat.
            </p>
            <h3>Objetivos de la semana 1</h3>
            <ul>
                <li>Configurar el entorno Java Web.</li>
                <li>Reconocer la arquitectura cliente-servidor.</li>
                <li>Desplegar una aplicación inicial.</li>
                <li>Documentar el proyecto integrador.</li>
            </ul>
            <a class="boton"
               href="${pageContext.request.contextPath}/diagnostico">
                Ver diagnóstico del sistema
            </a>

            <div class="acciones-formulario">
                <a href="${pageContext.request.contextPath}/registro">
                    Crear cuenta temporal
                </a>
                <a href="${pageContext.request.contextPath}/acceso">
                    Iniciar acceso
                </a>
            </div>
        </main>
    </body>
</html>