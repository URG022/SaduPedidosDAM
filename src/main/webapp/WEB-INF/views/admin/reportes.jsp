<%-- 
    Document   : reportes
    Created on : 24 oct 2026, 4:26:14 p. m.
    Author     : serva
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="titulo" value="Reportes"/>
<c:set var="seccion" value="reportes"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado-admin.jspf" %>

<div style="display: flex; flex-direction: column; gap: 20px;" class="print-container">
    <div style="display: flex; justify-content: space-between; align-items: center;" class="no-print">
        <div>
            <h1 style="font-size: 26px; margin: 0;">Reportes del Sistema</h1>
            <p class="texto-suave" style="margin: 4px 0 0">Analítica detallada de ventas por rango de fechas.</p>
        </div>
        <button onclick="window.print()" class="boton boton-principal" style="border: none; cursor: pointer;">Descargar PDF / Imprimir</button>
    </div>

    <!-- Formulario de filtro por rango de fechas -->
    <div class="tarjeta no-print">
        <form action="${pageContext.request.contextPath}/admin/reportes" method="GET" style="display: flex; gap: 15px; align-items: flex-end;">
            <div style="flex: 1;">
                <label style="display: block; font-weight: 700; margin-bottom: 5px;">Fecha Inicio</label>
                <input type="date" name="fechaInicio" class="campo" value="${param.fechaInicio}">
            </div>
            <div style="flex: 1;">
                <label style="display: block; font-weight: 700; margin-bottom: 5px;">Fecha Fin</label>
                <input type="date" name="fechaFin" class="campo" value="${param.fechaFin}">
            </div>
            <div>
                <button type="submit" class="boton boton-principal" style="border: none; cursor: pointer; height: 42px;">Filtrar</button>
            </div>
        </form>
    </div>

    <!-- Resultados del Reporte Filtrado (Diseño Vistoso y Ejecutivo) -->
    <div class="tarjeta reporte-impresion">
        <!-- Encabezado exclusivo para impresión -->
        <div class="print-header-empresa" style="display: none;">
            <h2 style="margin: 0; color: #46528F; font-size: 24px;">SADU RESTAURANT</h2>
            <p style="margin: 2px 0 15px 0; font-size: 13px; color: #666;">Reporte Ejecutivo de Ventas y Rendimiento</p>
            <hr style="border: none; border-top: 2px solid #5E6CB3; margin-bottom: 20px;">
        </div>

        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h3 style="margin: 0; font-size: 20px; color: #2B2D42;">Resumen del Periodo</h3>
            <span class="badge-fecha" style="background: #F4EFE6; padding: 6px 12px; border-radius: 8px; font-size: 13px; font-weight: bold;">
                <c:choose>
                    <c:when test="${not empty param.fechaInicio and not empty param.fechaFin}">
                        Del ${param.fechaInicio} al ${param.fechaFin}
                    </c:when>
                    <c:otherwise>
                        Histórico General
                    </c:otherwise>
                </c:choose>
            </span>
        </div>

        <!-- Tarjetas internas de métricas -->
        <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; margin-top: 15px;">
            <div style="background: #FAF6F0; padding: 15px; border-radius: 12px; border-left: 4px solid #5E6CB3;">
                <span style="font-size: 12px; color: #666; font-weight: bold; text-transform: uppercase;">Total Ventas</span>
                <h4 style="margin: 5px 0 0 0; font-size: 22px; color: #46528F;">$${reporte.ventasTotales}</h4>
            </div>
            <div style="background: #FAF6F0; padding: 15px; border-radius: 12px; border-left: 4px solid #F5C74D;">
                <span style="font-size: 12px; color: #666; font-weight: bold; text-transform: uppercase;">Pedidos Entregados</span>
                <h4 style="margin: 5px 0 0 0; font-size: 22px; color: #2B2D42;">${reporte.totalPedidosEntregados}</h4>
            </div>
            <div style="background: #FAF6F0; padding: 15px; border-radius: 12px; border-left: 4px solid #46528F;">
                <span style="font-size: 12px; color: #666; font-weight: bold; text-transform: uppercase;">Ticket Promedio</span>
                <h4 style="margin: 5px 0 0 0; font-size: 22px; color: #46528F;">
                    <fmt:formatNumber value="${reporte.ticketPromedio}" type="currency" currencySymbol="$"/>
                </h4>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/plantilla/pie-admin.jspf" %>

<style>
    @media print {
        /* 1. Ocultar todo excepto el contenedor del reporte */
        body * {
            visibility: hidden !important;
        }

        .reporte-impresion, 
        .reporte-impresion * {
            visibility: visible !important;
        }

        /* 2. Fijar la tarjeta arriba y expandirla a todo el ancho */
        .reporte-impresion {
            position: absolute !important;
            left: 0 !important;
            top: 0 !important;
            width: 100% !important;
            border: 1px solid #ddd !important;
            box-shadow: none !important;
            margin: 0 !important;
            padding: 30px !important;
            background-color: #ffffff !important;
        }

        /* 3. Mostrar el encabezado ejecutivo al imprimir */
        .print-header-empresa {
            display: block !important;
        }

        /* 4. Asegurar que los bloques internos conserven su diseño con cuadrícula */
        .reporte-impresion div {
            visibility: visible !important;
        }

        body, html {
            background-color: #ffffff !important;
            margin: 0 !important;
            padding: 0 !important;
        }
    }
</style>