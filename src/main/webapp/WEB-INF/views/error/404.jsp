<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Error 404"/>
<c:set var="seccion" value="error"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado.jspf" %>

<main class="acceso">
    <div class="tarjeta" style="text-align: center; align-items: center">
        <span class="etiqueta">Error 404</span>
        <h1>No encontramos esta página</h1>
        <p class="texto-suave" style="margin: 0">Puede que el enlace esté mal escrito o que este módulo todavía se esté construyendo.</p>
        <a class="btn btn-sol" href="${ctx}/inicio">Volver al inicio</a>
    </div>
</main>

<%@ include file="/WEB-INF/views/plantilla/pie.jspf" %>
