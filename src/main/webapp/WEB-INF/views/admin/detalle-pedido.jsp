<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="titulo" value="Detalle Pedido #${pedido.idPedido}"/>
<c:set var="seccion" value="historial"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado-admin.jspf" %>
<link rel="stylesheet" href="${ctx}/resources/css/pedidos.css">

<main class="pagina" style="padding: 0;">
    <div class="contenedor" style="max-width: 100%; padding: 0;">

        <!-- Cabecera -->
        <div class="detalle-cabecera" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <div class="pedidos-titulo">
                <a class="texto-suave" href="${ctx}/admin/historial-pedidos" style="text-decoration: none; font-weight: 700;">← Volver al Historial de Ventas</a>
                <h1 style="margin-top: 5px;">Pedido #${pedido.idPedido}</h1>
            </div>
            <span class="badge badge-${pedido.estado} badge-grande"><c:out value="${pedido.estado.etiqueta}"/></span>
        </div>

        <div class="detalle-layout" style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">

            <!-- Columna izquierda: datos y platillos -->
            <div class="detalle-columna" style="display: flex; flex-direction: column; gap: 20px;">

               <section class="tarjeta">
                    <h2 class="detalle-subtitulo" style="font-size: 18px; margin-bottom: 12px;">Datos del pedido</h2>
                    <dl class="info-pedido" style="display: grid; grid-template-columns: 140px 1fr; gap: 8px;">

                        <dt style="font-weight: 700; color: var(--sadu-texto-suave);">Cliente</dt>
                        <dd style="margin: 0; font-weight: 600; color: var(--sadu-azul-oscuro);"><c:out value="${pedido.nombreCliente}"/></dd>

                        <dt style="font-weight: 700; color: var(--sadu-texto-suave);">Fecha</dt>
                        <dd style="margin: 0;"><c:out value="${pedido.fechaCreacionTexto}"/></dd>

                        <dt style="font-weight: 700; color: var(--sadu-texto-suave);">Tipo de entrega</dt>
                        <dd style="margin: 0;"><c:out value="${pedido.tipoEntrega.etiqueta}"/></dd>

                        <c:if test="${not empty pedido.direccionEntrega}">
                            <dt style="font-weight: 700; color: var(--sadu-texto-suave);">Dirección</dt>
                            <dd style="margin: 0;"><c:out value="${pedido.direccionEntrega}"/></dd>
                        </c:if>

                        <c:if test="${not empty pedido.notas}">
                            <dt style="font-weight: 700; color: var(--sadu-texto-suave);">Notas</dt>
                            <dd style="margin: 0;"><c:out value="${pedido.notas}"/></dd>
                        </c:if>
                    </dl>
                </section>

                <section>
                    <h2 class="detalle-subtitulo" style="font-size: 18px; margin-bottom: 12px;">Platillos</h2>
                    <div class="tabla-caja">
                        <table class="tabla">
                            <thead>
                                <tr>
                                    <th>Platillo</th>
                                    <th class="num" style="text-align: right;">Cant.</th>
                                    <th class="num" style="text-align: right;">Precio</th>
                                    <th class="num" style="text-align: right;">Subtotal</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="d" items="${detalles}">
                                    <tr>
                                        <td><c:out value="${d.nombreProducto}"/></td>
                                        <td class="num" style="text-align: right;">${d.cantidad}</td>
                                        <td class="num" style="text-align: right;">$<fmt:formatNumber value="${d.precioUnitario}" pattern="#,##0.00"/></td>
                                        <td class="num" style="text-align: right;">$<fmt:formatNumber value="${d.subtotal}" pattern="#,##0.00"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <tfoot>
                                <tr style="font-weight: bold; background: #fbf8f2;">
                                    <td colspan="3" style="text-align: right;">Total</td>
                                    <td class="num" style="text-align: right;">$<fmt:formatNumber value="${pedido.total}" pattern="#,##0.00"/></td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </section>
            </div>

            <!-- Columna derecha: línea de tiempo y comprobante -->
            <div class="detalle-columna" style="display: flex; flex-direction: column; gap: 20px;">

                <section class="tarjeta">
                    <h2 class="detalle-subtitulo" style="font-size: 18px; margin-bottom: 12px;">Seguimiento</h2>
                    <c:choose>
                        <c:when test="${empty seguimiento}">
                            <p class="texto-suave">Todavía no hay movimientos registrados.</p>
                        </c:when>
                        <c:otherwise>
                            <ol class="linea-tiempo">
                                <c:forEach var="s" items="${seguimiento}" varStatus="estadoFila">
                                    <li class="paso paso-${s.estado} ${estadoFila.last ? 'paso-actual' : ''}">
                                        <span class="paso-punto" aria-hidden="true"></span>
                                        <div class="paso-contenido">
                                            <strong><c:out value="${s.estado.etiqueta}"/></strong>
                                            <span class="texto-suave paso-fecha"><c:out value="${s.fechaRegistroTexto}"/></span>
                                            <c:if test="${not empty s.comentario}">
                                                <span class="paso-comentario"><c:out value="${s.comentario}"/></span>
                                            </c:if>
                                        </div>
                                    </li>
                                </c:forEach>
                            </ol>
                        </c:otherwise>
                    </c:choose>
                </section>

                <section class="tarjeta">
                    <h2 class="detalle-subtitulo" style="font-size: 18px; margin-bottom: 12px;">Comprobante de Venta</h2>
                    <c:choose>
                        <c:when test="${not empty comprobante}">
                            <dl class="info-pedido" style="display: grid; grid-template-columns: 140px 1fr; gap: 8px;">
                                <dt style="font-weight: 700; color: var(--sadu-texto-suave);">Número</dt>
                                <dd style="margin: 0;"><strong><c:out value="${comprobante.numero}"/></strong></dd>

                                <dt style="font-weight: 700; color: var(--sadu-texto-suave);">Método de pago</dt>
                                <dd style="margin: 0;"><c:out value="${comprobante.metodoPago.etiqueta}"/></dd>

                                <dt style="font-weight: 700; color: var(--sadu-texto-suave);">Total cobrado</dt>
                                <dd style="margin: 0;">$<fmt:formatNumber value="${comprobante.total}" pattern="#,##0.00"/></dd>
                            </dl>
                        </c:when>
                        <c:otherwise>
                            <p class="texto-suave">Este pedido no cuenta con comprobante emitido.</p>
                        </c:otherwise>
                    </c:choose>
                </section>
            </div>

        </div>
    </div>
                                
</main>

<%@ include file="/WEB-INF/views/plantilla/pie-admin.jspf" %>