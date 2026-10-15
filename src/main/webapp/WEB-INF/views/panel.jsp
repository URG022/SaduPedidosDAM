<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Panel"/>
<c:set var="seccion" value="panel"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado-admin.jspf" %>

<div>
    <h1>Hola, <c:out value="${usuario.primerNombre}"/></h1>
    <p class="texto-suave" style="margin: 4px 0 0">Elige un módulo para empezar.</p>
</div>

<div class="grid-tarjetas">
    <c:if test="${usuario.administrador}">
        <a class="tarjeta" href="${ctx}/admin/dashboard" style="text-decoration: none; color: inherit; display: flex; flex-direction: column; gap: 6px">
            <h2 style="font-size: 22px">Dashboard</h2><span class="texto-suave">Ventas, pedidos por estado y platillos más vendidos.</span>
        </a>
    </c:if>
    <a class="tarjeta" href="${ctx}/cocina/tablero" style="text-decoration: none; color: inherit; display: flex; flex-direction: column; gap: 6px">
        <h2 style="font-size: 22px">Tablero de cocina</h2><span class="texto-suave">Pedidos por estado y cambio de estado.</span>
    </a>
    <c:if test="${usuario.administrador}">
        <a class="tarjeta" href="${ctx}/admin/admin_productos" style="text-decoration: none; color: inherit; display: flex; flex-direction: column; gap: 6px">
            <h2 style="font-size: 22px">Menú</h2><span class="texto-suave">Platillos y categorías.</span>
        </a>
        <a class="tarjeta" href="${ctx}/admin/clientes" style="text-decoration: none; color: inherit; display: flex; flex-direction: column; gap: 6px">
            <h2 style="font-size: 22px">Clientes</h2><span class="texto-suave">Activar o desactivar clientes.</span>
        </a>
        <a class="tarjeta" href="${ctx}/admin/reportes" style="text-decoration: none; color: inherit; display: flex; flex-direction: column; gap: 6px">
            <h2 style="font-size: 22px">Reportes</h2><span class="texto-suave">Ventas por fecha y horas pico.</span>
        </a>
    </c:if>
</div>

<%@ include file="/WEB-INF/views/plantilla/pie-admin.jspf" %>
