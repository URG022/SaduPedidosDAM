<%-- 
    Document   : dashboard
    Created on : 24 oct 2026, 4:19:32 p. m.
    Author     : serva
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="titulo" value="Dashboard"/>
<c:set var="seccion" value="dashboard"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado-admin.jspf" %>

<!-- Cargar Chart.js desde CDN -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<div style="display: flex; flex-direction: column; gap: 20px;">
    <div>
        <h1 style="font-size: 26px; margin: 0;">Dashboard Gerencial</h1>
        <p class="texto-suave" style="margin: 4px 0 0">Resumen general de ventas, pedidos activos y platillos más vendidos.</p>
    </div>

    <!-- Tarjetas KPI -->
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px;">
        <div class="tarjeta">
            <span class="texto-suave" style="font-size: 13px; font-weight: 700;">VENTAS ENTREGADAS</span>
            <h3 style="font-size: 28px; margin: 6px 0;">$${reporte.ventasTotales}</h3>
            <span class="texto-suave" style="font-size: 12px;">${reporte.totalPedidosEntregados} pedidos</span>
        </div>
        <div class="tarjeta">
            <span class="texto-suave" style="font-size: 13px; font-weight: 700;">TICKET PROMEDIO</span>
            <h3 style="font-size: 28px; margin: 6px 0;">
                <fmt:formatNumber value="${reporte.ticketPromedio}" type="currency" currencySymbol="$"/>
            </h3>
            <span class="texto-suave" style="font-size: 12px;">por pedido entregado</span>
        </div>
        <div class="tarjeta">
            <span class="texto-suave" style="font-size: 13px; font-weight: 700;">PEDIDOS ACTIVOS</span>
            <h3 style="font-size: 28px; margin: 6px 0;">${reporte.pedidosActivos}</h3>
            <span class="texto-suave" style="font-size: 12px;">en cocina o en camino</span>
        </div>
        <div class="tarjeta">
            <span class="texto-suave" style="font-size: 13px; font-weight: 700;">HORA PICO</span>
            <h3 style="font-size: 28px; margin: 6px 0;">${reporte.horaPico}</h3>
            <span class="texto-suave" style="font-size: 12px;">mayor afluencia</span>
        </div>
    </div>

    <!-- Sección de Gráficos -->
    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
        <!-- Gráfico de Pedidos por Estado -->
        <div class="tarjeta">
            <h3 style="font-size: 18px; margin-top: 0;">Pedidos por Estado</h3>
            <div style="position: relative; height: 260px; width: 100%;">
                <canvas id="graficoEstados"></canvas>
            </div>
        </div>
        <!-- Gráfico de Top 5 Platillos -->
        <div class="tarjeta">
            <h3 style="font-size: 18px; margin-top: 0;">Top 5 Platillos Más Vendidos</h3>
            <div style="position: relative; height: 260px; width: 100%;">
                <canvas id="graficoTopPlatillos"></canvas>
            </div>
        </div>
    </div>
</div>

<script>
    // Gráfico de Pedidos por Estado (Barras)
    const ctxEstados = document.getElementById('graficoEstados').getContext('2d');
    new Chart(ctxEstados, {
        type: 'bar',
        data: {
            labels: [
                <c:forEach var="entry" items="${reporte.pedidosPorEstado}">
                    '${entry.key}',
                </c:forEach>
            ],
            datasets: [{
                label: 'Cantidad de Pedidos',
                data: [
                    <c:forEach var="entry" items="${reporte.pedidosPorEstado}">
                        ${entry.value},
                    </c:forEach>
                ],
                backgroundColor: '#5E6CB3',
                borderRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true } }
        }
    });

    // Gráfico Top 5 Platillos (Pastel / Doughnut)
    const ctxTop = document.getElementById('graficoTopPlatillos').getContext('2d');
    new Chart(ctxTop, {
        type: 'doughnut',
        data: {
            labels: [
                <c:forEach var="item" items="${reporte.topPlatillos}">
                    '${item.key}',
                </c:forEach>
            ],
            datasets: [{
                data: [
                    <c:forEach var="item" items="${reporte.topPlatillos}">
                        ${item.value},
                    </c:forEach>
                ],
                backgroundColor: ['#5E6CB3', '#F5C74D', '#46528F', '#2B2D42', '#A3A9D0']
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });
</script>

<%@ include file="/WEB-INF/views/plantilla/pie-admin.jspf" %>