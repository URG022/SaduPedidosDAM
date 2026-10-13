<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Iniciar sesión"/>
<c:set var="seccion" value="login"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado.jspf" %>

<main class="acceso">
    <div class="tarjeta">
        <div class="acceso-cabecera">
            <h1>¡Qué bueno verte!</h1>
            <p class="texto-suave" style="margin: 0">Inicia sesión para hacer tu pedido.</p>
        </div>

        <c:if test="${param.salida == '1'}"><p class="alerta alerta-info">Cerraste sesión. ¡Vuelve pronto!</p></c:if>
        <c:if test="${not empty error}"><p class="alerta alerta-error"><c:out value="${error}"/></p></c:if>

        <form class="formulario" method="post" action="${ctx}/login">
            <c:set var="volverValor" value="${not empty volver ? volver : param.volver}"/>
            <input type="hidden" name="volver" value="<c:out value='${volverValor}'/>">
            <label class="campo">Correo electrónico
                <input type="email" name="correo" required autocomplete="email" value="<c:out value='${correo}'/>">
            </label>
            <label class="campo">Contraseña
                <input type="password" name="clave" required autocomplete="current-password">
            </label>
            <label class="casilla"><input type="checkbox" name="recordar" ${recordar ? 'checked' : ''}> Recordar mi correo</label>
            <button class="btn btn-sol btn-grande btn-bloque" type="submit">Iniciar sesión</button>
        </form>

        <p style="margin: 0; text-align: center">¿Primera vez en Sadu? <a href="${ctx}/registro"><strong>Crea tu cuenta</strong></a></p>
    </div>
</main>

<%@ include file="/WEB-INF/views/plantilla/pie.jspf" %>
