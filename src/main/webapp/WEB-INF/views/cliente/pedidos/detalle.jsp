<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="titulo" value="Pedido #${pedido.idPedido}"/>
<c:set var="seccion" value="pedidos"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado.jspf" %>
<link rel="stylesheet" href="${ctx}/resources/css/pedidos.css">

<main class="pagina">
    <div class="contenedor">

        <!-- Cabecera -->
        <div class="detalle-cabecera">
            <div class="pedidos-titulo">
                <a class="texto-suave" href="${ctx}/cliente/pedidos">← Mis pedidos</a>
                <h1>Pedido #${pedido.idPedido}</h1>
            </div>
            <span class="badge badge-${pedido.estado} badge-grande"><c:out value="${pedido.estado.etiqueta}"/></span>
        </div>

        <c:if test="${param.exito eq '1'}">
            <div class="alerta alerta-exito">¡Recibimos tu pedido! Aquí puedes ver cómo va.</div>
        </c:if>

        <div class="detalle-layout">

            <!-- Columna izquierda: datos y platillos -->
            <div class="detalle-columna">

                <section class="tarjeta spacex">
                    <h2 class="detalle-subtitulo">Datos del pedido</h2>
                    <dl class="info-pedido">
                        <dt>Fecha</dt>
                        <dd><c:out value="${pedido.fechaCreacionTexto}"/></dd>

                        <dt>Tipo de entrega</dt>
                        <dd><c:out value="${pedido.tipoEntrega.etiqueta}"/></dd>

                        <c:if test="${not empty pedido.direccionEntrega}">
                            <dt>Dirección</dt>
                            <dd><c:out value="${pedido.direccionEntrega}"/></dd>
                        </c:if>

                        <c:if test="${not empty pedido.notas}">
                            <dt>Notas</dt>
                            <dd><c:out value="${pedido.notas}"/></dd>
                        </c:if>
                    </dl>
                </section>

                <section class="spacex">
                    <h2 class="detalle-subtitulo">Platillos</h2>
                    <div class="tabla-caja">
                        <table class="tabla">
                            <thead>
                                <tr>
                                    <th>Platillo</th>
                                    <th class="num">Cant.</th>
                                    <th class="num">Precio</th>
                                    <th class="num">Subtotal</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="d" items="${detalles}">
                                    <tr>
                                        <td><c:out value="${d.nombreProducto}"/></td>
                                        <td class="num">${d.cantidad}</td>
                                        <td class="num">$<fmt:formatNumber value="${d.precioUnitario}" pattern="#,##0.00"/></td>
                                        <td class="num">$<fmt:formatNumber value="${d.subtotal}" pattern="#,##0.00"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <tfoot>
                                <tr>
                                    <td colspan="3">Total</td>
                                    <td class="num">$<fmt:formatNumber value="${pedido.total}" pattern="#,##0.00"/></td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </section>
            </div>

            <!-- Columna derecha: línea de tiempo y comprobante -->
            <div class="detalle-columna">

                <section class="tarjeta spacex">
                    <h2 class="detalle-subtitulo">Seguimiento</h2>

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

                <section class="tarjeta spacex">
                    <h2 class="detalle-subtitulo">Comprobante</h2>

                    <c:choose>
                        <c:when test="${not empty comprobante}">
                            <dl class="info-pedido">
                                <dt>Número</dt>
                                <dd><strong><c:out value="${comprobante.numero}"/></strong></dd>

                                <dt>Método de pago</dt>
                                <dd><c:out value="${comprobante.metodoPago.etiqueta}"/></dd>

                                <dt>Total pagado</dt>
                                <dd>$<fmt:formatNumber value="${comprobante.total}" pattern="#,##0.00"/></dd>
                            </dl>
                        </c:when>
                        <c:otherwise>
                            <p class="texto-suave">
                                Tu comprobante estará disponible cuando el pedido sea entregado.
                            </p>
                        </c:otherwise>
                    </c:choose>
                </section>
            </div>

        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/plantilla/pie.jspf" %>