<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Error 500"/>
<c:set var="seccion" value="error"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado.jspf" %>

<main class="acceso">
    <div class="tarjeta" style="text-align: center; align-items: center">
        <span class="etiqueta">Error 500</span>
        <h1>Algo salió mal</h1>
        <p class="texto-suave" style="margin: 0">Tuvimos un problema al procesar tu solicitud. Intenta de nuevo en un momento.</p>
        <a class="btn btn-sol" href="${ctx}/inicio">Volver al inicio</a>
    </div>
</main>

<%@ include file="/WEB-INF/views/plantilla/pie.jspf" %>
