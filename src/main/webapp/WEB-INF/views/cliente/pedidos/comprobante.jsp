<%-- 
    Document   : comprobante
    Created on : 25 oct 2026, 10:53:53 a. m.
    Author     : User
--%>

<%-- P6 - Comprobante imprimible. Se abre en otra pestaña desde el detalle del pedido. --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Comprobante <c:out value="${comprobante.numero}"/> · Sadu Restaurant</title>
    <link rel="icon" href="${ctx}/resources/img/logo-sadu.png?v=3">
    <link rel="stylesheet" href="${ctx}/resources/css/estilos.css?v=3">
    <style>
        body { background: var(--sadu-arena); display: block; }
        .comprobante { max-width: 560px; margin: 32px auto; background: var(--sadu-blanco); border-radius: var(--radio);
                       box-shadow: var(--sombra); padding: 32px; display: flex; flex-direction: column; gap: 20px; }
        .comp-cabecera { display: flex; justify-content: space-between; align-items: flex-start; gap: 16px;
                         border-bottom: 3px solid var(--sadu-sol); padding-bottom: 16px; }
        .comp-cabecera img { height: 72px; }
        .comp-numero { text-align: right; }
        .comp-numero strong { display: block; font-family: var(--fuente-titulo); font-size: 22px; color: var(--sadu-azul-oscuro); }
        .comp-datos { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 10px 20px; font-size: 14px; }
        .comp-datos span { display: block; color: var(--sadu-texto-suave); font-weight: 800; font-size: 12px;
                           text-transform: uppercase; letter-spacing: .06em; }
        .comp-tabla { width: 100%; border-collapse: collapse; font-size: 14px; }
        .comp-tabla th, .comp-tabla td { padding: 8px 4px; border-bottom: 1px solid var(--sadu-arena); text-align: left; }
        .comp-tabla .num { text-align: right; }
        .comp-total { display: flex; justify-content: space-between; align-items: baseline;
                      font-family: var(--fuente-titulo); font-size: 26px; }
        .comp-pie { text-align: center; color: var(--sadu-texto-suave); font-size: 13px; }
        .comp-acciones { display: flex; gap: 12px; justify-content: center; flex-wrap: wrap; }
        @media print {
            body { background: #fff; }
            .comprobante { box-shadow: none; margin: 0 auto; }
            .comp-acciones { display: none; }
        }
    </style>
</head>
<body>
<main class="comprobante">
    <div class="comp-cabecera">
        <img src="${ctx}/resources/img/logo-sadu.png?v=3" alt="Sadu Restaurant">
        <div class="comp-numero">
            <span class="etiqueta">Comprobante de pago</span>
            <strong><c:out value="${comprobante.numero}"/></strong>
            <span class="texto-suave"><c:out value="${comprobante.fechaEmisionTexto}"/></span>
        </div>
    </div>

    <div class="comp-datos">
        <div><span>Cliente</span><c:out value="${cliente.nombreCompleto}"/></div>
        <div><span>Pedido</span>#${pedido.idPedido}</div>
        <div><span>Tipo de entrega</span><c:out value="${pedido.tipoEntrega.etiqueta}"/></div>
        <div><span>Método de pago</span><c:out value="${comprobante.metodoPago.etiqueta}"/></div>
        <c:if test="${not empty pedido.direccionEntrega}">
            <div style="grid-column: 1 / -1"><span>Dirección</span><c:out value="${pedido.direccionEntrega}"/></div>
        </c:if>
    </div>

    <table class="comp-tabla">
        <thead>
            <tr><th>Platillo</th><th class="num">Cant.</th><th class="num">Precio</th><th class="num">Subtotal</th></tr>
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
    </table>

    <div class="comp-total">
        <span>Total pagado</span>
        <span>$<fmt:formatNumber value="${comprobante.total}" pattern="#,##0.00"/></span>
    </div>

    <p class="comp-pie">¡Gracias por tu pedido! · Sadu Restaurant<br>
        Comprobante simulado para fines académicos.</p>

    <div class="comp-acciones">
        <button class="btn btn-sol" type="button" onclick="window.print()">Descargar PDF / Imprimir</button>
        <a class="btn btn-borde" href="${ctx}/cliente/pedidos?accion=detalle&id=${pedido.idPedido}">Volver al pedido</a>
    </div>
</main>
</body>
</html>
