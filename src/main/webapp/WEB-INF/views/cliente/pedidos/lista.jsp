<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="titulo" value="Mis pedidos"/>
<c:set var="seccion" value="pedidos"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado.jspf" %>
<link rel="stylesheet" href="${ctx}/resources/css/pedidos.css">

<main class="pagina">
    <div class="contenedor">

        <div class="pedidos-titulo">
            <span class="etiqueta">Tu historial</span>
            <h1>Mis pedidos</h1>
        </div>

        <c:choose>

            <%-- ========== Sin pedidos todavía ========== --%>
            <c:when test="${empty pedidos}">
                <div class="tarjeta pedidos-vacio">
                    <h2>Aún no has hecho ningún pedido</h2>
                    <p class="texto-suave">Cuando hagas tu primer pedido aparecerá aquí con su estado.</p>
                    <a class="btn btn-sol" href="${ctx}/cliente/productos">Ver el menú</a>
                </div>
            </c:when>

            <%-- ========== Lista de pedidos (el más reciente primero) ========== --%>
            <c:otherwise>
                <div class="tabla-caja">
                    <table class="tabla">
                        <thead>
                            <tr>
                                <th>Pedido</th>
                                <th>Fecha</th>
                                <th class="col-entrega">Entrega</th>
                                <th>Total</th>
                                <th>Estado</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${pedidos}">
                                <tr>
                                    <td class="col-numero">#${p.idPedido}</td>
                                    <td><c:out value="${p.fechaCreacionTexto}"/></td>
                                    <td class="col-entrega"><c:out value="${p.tipoEntrega.etiqueta}"/></td>
                                    <td class="col-total">$<fmt:formatNumber value="${p.total}" pattern="#,##0.00"/></td>
                                    <td><span class="badge badge-${p.estado}"><c:out value="${p.estado.etiqueta}"/></span></td>
                                    <td class="col-accion">
                                        <a class="btn btn-borde btn-chico"
                                           href="${ctx}/cliente/pedidos?accion=detalle&id=${p.idPedido}">Ver detalle</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>

        </c:choose>
    </div>
</main>

<%@ include file="/WEB-INF/views/plantilla/pie.jspf" %>