<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="titulo" value="Historial de Ventas"/>
<c:set var="seccion" value="historial"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado-admin.jspf" %>

<div style="display: flex; flex-direction: column; gap: 20px;">
    
    <!-- Encabezado de la sección -->
    <div style="display: flex; justify-content: space-between; align-items: flex-end;">
        <div>
            <h1 style="font-size: 28px; margin: 0; color: #1E293B;">Historial de Ventas</h1>
            <p class="texto-suave" style="margin: 4px 0 0;">Pedidos finalizados y entregados</p>
        </div>
    </div>

    <!-- FORMULARIO DE FILTROS -->
    <div class="tarjeta" style="padding: 12px; background: white; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.04);">
        <form method="GET" action="${ctx}/admin/historial-pedidos" style="display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end;">
            
            <!-- Buscar por nombre -->
            <div style="flex: 1; min-width: 220px;">
                <label style="display: block; font-size: 13px; font-weight: 700; color: #475569;">Cliente</label>
                <input type="text" name="busqueda" value="${param.busqueda}" placeholder="Nombre del cliente..."  style="width: 100%; padding: 8px 12px; border: 1px solid #CBD5E1; border-radius: 8px;">
            </div>

            <!-- Fecha Inicio -->
            <div style="width: 170px;">
                <label style="display: block; font-size: 13px; font-weight: 700; color: #475569;">Fecha inicio</label>
                <input type="date" name="fechaInicio" value="${param.fechaInicio}" class="campo" style="width: 100%; padding: 8px 12px; border: 1px solid #CBD5E1; border-radius: 8px;">
            </div>

            <!-- Fecha Fin -->
            <div style="width: 170px;">
                <label style="display: block; font-size: 13px; font-weight: 700; color: #475569;">Fecha fin</label>
                <input type="date" name="fechaFin" value="${param.fechaFin}" class="campo" style="width: 100%; padding: 8px 12px; border: 1px solid #CBD5E1; border-radius: 8px;">
            </div>

            <!-- Botones de Acción -->
            <div style="display: flex; gap: 8px;">
                <button type="submit" class="btn btn-sol" style="padding: 9px 16px; font-weight: 700; cursor: pointer; border-radius: 8px;">Filtrar</button>
                <a href="${ctx}/admin/historial-pedidos" class="btn btn-borde" style="padding: 9px 16px; font-weight: 700; text-decoration: none; border-radius: 8px; display: inline-flex; align-items: center;">Limpiar</a>
            </div>
        </form>
    </div>

    <!-- Controles de error -->
    <c:if test="${not empty error}">
        <div class="alerta alerta-error"><c:out value="${error}"/></div>
    </c:if>

    <c:choose>
        <%-- ========== Sin pedidos ========== --%>
        <c:when test="${empty historialEntregados}">
            <div class="tarjeta" style="text-align: center; padding: 40px 20px;">
                <h2 style="color: #475569; margin-bottom: 8px;">No se encontraron registros</h2>
                <p class="texto-suave">No hay pedidos entregados que coincidan con los filtros seleccionados.</p>
            </div>
        </c:when>

        <%-- ========== Lista del historial ========== --%>
        <c:otherwise>
            <div class="tabla-caja">
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>Pedido</th>
                            <th>Fecha</th>
                            <th>Cliente</th>
                            <th>Entrega</th>
                            <th>Total</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${historialEntregados}">
                            <tr>
                                <td style="font-weight: 800; color: var(--sadu-azul-oscuro);">#${p.idPedido}</td>
                                <td><c:out value="${p.fechaCreacionTexto}"/></td>
                                <td style="font-weight: 600;"><c:out value="${p.nombreCliente}"/></td>
                                <td><c:out value="${p.tipoEntrega.etiqueta}"/></td>
                                <td style="font-weight: 700; color: #15803d;">
                                    $<fmt:formatNumber value="${p.total}" pattern="#,##0.00"/>
                                </td>
                                <td class="col-accion">
                                    <a class="btn btn-borde btn-chico" 
                                       href="${ctx}/admin/historial-pedidos?accion=detalle&id=${p.idPedido}">
                                       Ver detalle
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Paginación -->
            <c:if test="${totalPaginas > 1}">
                <div style="display: flex; gap: 8px; justify-content: center;">

                    <!-- Botón Anterior -->
                    <c:if test="${paginaActual > 1}">
                        <a href="${ctx}/admin/historial-pedidos?pagina=${paginaActual - 1}&busqueda=${param.busqueda}&fechaInicio=${param.fechaInicio}&fechaFin=${param.fechaFin}" class="btn btn-borde btn-chico">Anterior</a>
                    </c:if>

                    <!-- Números de Página -->
                    <c:forEach begin="1" end="${totalPaginas}" var="i">
                        <a href="${ctx}/admin/historial-pedidos?pagina=${i}&busqueda=${param.busqueda}&fechaInicio=${param.fechaInicio}&fechaFin=${param.fechaFin}" 
                           class="btn btn-chico ${paginaActual == i ? 'btn-azul' : 'btn-borde'}">
                            ${i}
                        </a>
                    </c:forEach>

                    <!-- Botón Siguiente -->
                    <c:if test="${paginaActual < totalPaginas}">
                        <a href="${ctx}/admin/historial-pedidos?pagina=${paginaActual + 1}&busqueda=${param.busqueda}&fechaInicio=${param.fechaInicio}&fechaFin=${param.fechaFin}" class="btn btn-borde btn-chico">Siguiente</a>
                    </c:if>

                </div>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/views/plantilla/pie-admin.jspf" %>