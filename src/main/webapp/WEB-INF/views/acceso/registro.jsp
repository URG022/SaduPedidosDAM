<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Crear cuenta"/>
<c:set var="seccion" value="registro"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado.jspf" %>

<main class="acceso">
    <div class="tarjeta ancha">
        <div class="acceso-cabecera">
            <h1>Crea tu cuenta</h1>
            <p class="texto-suave" style="margin: 0">Así guardamos tus datos para tus próximos pedidos.</p>
        </div>

        <c:if test="${not empty errores}">
            <div class="alerta alerta-error" role="alert">Revisa estos datos:
                <ul><c:forEach var="e" items="${errores}"><li><c:out value="${e}"/></li></c:forEach></ul>
            </div>
        </c:if>

        <form class="formulario" method="post" action="${ctx}/registro">
            <label class="campo">Nombre completo
                <input type="text" name="nombre" required maxlength="120" autocomplete="name" value="<c:out value='${param.nombre}'/>">
            </label>
            <div class="fila-2">
                <label class="campo">Correo electrónico
                    <input type="email" name="correo" required maxlength="160" autocomplete="email" value="<c:out value='${param.correo}'/>">
                </label>
                <label class="campo">Teléfono
                    <input type="tel" name="telefono" required placeholder="7012-3456" autocomplete="tel" value="<c:out value='${param.telefono}'/>">
                </label>
            </div>
            <label class="campo">Dirección (opcional, para pedidos a domicilio)
                <input type="text" name="direccion" maxlength="200" autocomplete="street-address" value="<c:out value='${param.direccion}'/>">
            </label>
            <div class="fila-2">
                <label class="campo">Contraseña
                    <input type="password" name="clave" required minlength="8" autocomplete="new-password">
                    <small>Mínimo 8 caracteres y un número.</small>
                </label>
                <label class="campo">Confirmar contraseña
                    <input type="password" name="confirmacion" required minlength="8" autocomplete="new-password">
                </label>
            </div>
            <button class="btn btn-sol btn-grande btn-bloque" type="submit">Crear cuenta</button>
        </form>

        <p style="margin: 0; text-align: center">¿Ya tienes cuenta? <a href="${ctx}/login"><strong>Inicia sesión</strong></a></p>
    </div>
</main>

<%@ include file="/WEB-INF/views/plantilla/pie.jspf" %>
