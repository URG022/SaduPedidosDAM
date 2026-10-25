<%-- 
    Document   : tablero
    Created on : 22 oct 2026, 9:35:09 p. m.
    Author     : Carias
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="titulo" value="Tablero de cocina"/>
<c:set var="seccion" value="cocina"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado-admin.jspf" %>

<div style="display: flex; flex-direction: column; gap: 20px;">
    <!-- Mensaje de error si la regla de negocio rechaza la transición -->
    <c:if test="${not empty error}">
        <div style="background: #FEE2E2; color: #991B1B; padding: 12px 16px; border-radius: 12px; font-weight: 700; border-left: 5px solid #EF4444;">
            ${error}
        </div>
    </c:if>

    <!-- Encabezado con título y filtro por tipo de entrega -->
    <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
            <h1 style="font-size: 26px; margin: 0; color: #1E293B;">Tablero de cocina</h1>
            <p class="texto-suave" style="margin: 4px 0 0">Pedidos activos · se actualiza al cambiar de estado</p>
        </div>
        <div style="display: flex; align-items: center; gap: 10px;">
            <span style="font-weight: 700; font-size: 14px; color: #475569;">Tipo de entrega</span>
            <select id="filtroEntrega" class="campo" style="padding: 8px 14px; border-radius: 10px; background: white; border: 1px solid #CBD5E1; font-weight: 600;" onchange="filtrarPedidos()">
                <option value="TODOS">Todos</option>
                <option value="EN_LOCAL">En local</option>
                <option value="PARA_LLEVAR">Para llevar</option>
                <option value="DOMICILIO">A domicilio</option>
            </select>
        </div>
    </div>

    <!-- Contenedor Kanban de 4 Columnas con Colores Vivos -->
    <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 18px; align-items: start;">
        
        <!-- COLUMNA 1: RECIBIDOS -->
        <div style="background: #EEF2FF; border-radius: 16px; padding: 16px; min-height: 500px; border: 1px solid #C7D2FE;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; padding: 0 4px;">
                <h3 style="font-size: 16px; margin: 0; font-weight: 800; color: #312E81;">Recibidos</h3>
                <span style="background: #4F46E5; color: white; padding: 2px 10px; border-radius: 50px; font-size: 12px; font-weight: bold;" id="count-recibidos">0</span>
            </div>
            
            <div class="columna-pedidos" data-estado="RECIBIDO">
                <c:forEach var="p" items="${pedidos}">
                    <c:if test="${p.estado eq 'RECIBIDO'}">
                        <div class="tarjeta tarjeta-pedido" data-tipo="${p.tipoEntrega}" style="background: white; border-radius: 14px; padding: 16px; margin-bottom: 14px; box-shadow: 0 4px 12px rgba(79, 70, 229, 0.08); border-left: 5px solid #4F46E5;">
                            <div style="display: flex; justify-content: space-between; font-weight: 800; font-size: 16px; margin-bottom: 6px; color: #1E293B;">
                                <span>#${p.idPedido}</span>
                                <span style="font-size: 12px; color: #64748B; font-weight: 600;">${p.fechaCreacionTexto}</span>
                            </div>
                            <h4 style="font-size: 15px; margin: 0 0 6px 0; font-weight: 700; color: #0F172A;">Cliente #${p.idCliente}</h4>
                            <p style="font-size: 13px; color: #475569; margin: 0 0 12px 0; line-height: 1.4;">
                                <c:forEach var="det" items="${p.detalles}" varStatus="status">
                                    ${det.cantidad} ${det.nombreProducto}${!status.last ? ', ' : ''}
                                </c:forEach>
                            </p>
                            <div style="margin-bottom: 14px;">
                                <span style="background: #E0E7FF; color: #3730A3; padding: 4px 10px; border-radius: 20px; font-size: 11px; font-weight: 700;">
                                    ${p.tipoEntrega}
                                </span>
                            </div>
                            <form action="${ctx}/cocina" method="POST">
                                <input type="hidden" name="idPedido" value="${p.idPedido}">
                                <input type="hidden" name="estado" value="EN_PREPARACION">
                                <input type="hidden" name="comentario" value="Inicia preparación en cocina">
                                <button type="submit" class="boton" style="width: 100%; padding: 10px; font-size: 13px; border: none; cursor: pointer; border-radius: 10px; background-color: #4F46E5; color: white; font-weight: 700; box-shadow: 0 2px 6px rgba(79, 70, 229, 0.3);">
                                    Empezar a preparar
                                </button>
                            </form>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>

        <!-- COLUMNA 2: EN PREPARACIÓN -->
        <div style="background: #FEF3C7; border-radius: 16px; padding: 16px; min-height: 500px; border: 1px solid #FDE68A;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; padding: 0 4px;">
                <h3 style="font-size: 16px; margin: 0; font-weight: 800; color: #78350F;">En preparación</h3>
                <span style="background: #D97706; color: white; padding: 2px 10px; border-radius: 50px; font-size: 12px; font-weight: bold;" id="count-preparacion">0</span>
            </div>
            
            <div class="columna-pedidos" data-estado="EN_PREPARACION">
                <c:forEach var="p" items="${pedidos}">
                    <c:if test="${p.estado eq 'EN_PREPARACION'}">
                        <div class="tarjeta tarjeta-pedido" data-tipo="${p.tipoEntrega}" style="background: white; border-radius: 14px; padding: 16px; margin-bottom: 14px; box-shadow: 0 4px 12px rgba(217, 119, 6, 0.08); border-left: 5px solid #D97706;">
                            <div style="display: flex; justify-content: space-between; font-weight: 800; font-size: 16px; margin-bottom: 6px; color: #1E293B;">
                                <span>#${p.idPedido}</span>
                                <span style="font-size: 12px; color: #64748B; font-weight: 600;">${p.fechaCreacionTexto}</span>
                            </div>
                            <h4 style="font-size: 15px; margin: 0 0 6px 0; font-weight: 700; color: #0F172A;">Cliente #${p.idCliente}</h4>
                            <p style="font-size: 13px; color: #475569; margin: 0 0 12px 0; line-height: 1.4;">
                                <c:forEach var="det" items="${p.detalles}" varStatus="status">
                                    ${det.cantidad} ${det.nombreProducto}${!status.last ? ', ' : ''}
                                </c:forEach>
                            </p>
                            <div style="margin-bottom: 14px;">
                                <span style="background: #FEF3C7; color: #92400E; padding: 4px 10px; border-radius: 20px; font-size: 11px; font-weight: 700;">
                                    ${p.tipoEntrega}
                                </span>
                            </div>
                            <form action="${ctx}/cocina" method="POST">
                                <input type="hidden" name="idPedido" value="${p.idPedido}">
                                <input type="hidden" name="estado" value="LISTO">
                                <input type="hidden" name="comentario" value="Pedido listo para entrega">
                                <button type="submit" class="boton" style="width: 100%; padding: 10px; font-size: 13px; border: none; cursor: pointer; border-radius: 10px; background-color: #D97706; color: white; font-weight: 700; box-shadow: 0 2px 6px rgba(217, 119, 6, 0.3);">
                                    Marcar como listo
                                </button>
                            </form>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>

        <!-- COLUMNA 3: LISTOS -->
        <div style="background: #ECFDF5; border-radius: 16px; padding: 16px; min-height: 500px; border: 1px solid #A7F3D0;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; padding: 0 4px;">
                <h3 style="font-size: 16px; margin: 0; font-weight: 800; color: #065F46;">Listos</h3>
                <span style="background: #059669; color: white; padding: 2px 10px; border-radius: 50px; font-size: 12px; font-weight: bold;" id="count-listos">0</span>
            </div>
            
            <div class="columna-pedidos" data-estado="LISTO">
                <c:forEach var="p" items="${pedidos}">
                    <c:if test="${p.estado eq 'LISTO'}">
                        <div class="tarjeta tarjeta-pedido" data-tipo="${p.tipoEntrega}" style="background: white; border-radius: 14px; padding: 16px; margin-bottom: 14px; box-shadow: 0 4px 12px rgba(5, 150, 105, 0.08); border-left: 5px solid #059669;">
                            <div style="display: flex; justify-content: space-between; font-weight: 800; font-size: 16px; margin-bottom: 6px; color: #1E293B;">
                                <span>#${p.idPedido}</span>
                                <span style="font-size: 12px; color: #64748B; font-weight: 600;">${p.fechaCreacionTexto}</span>
                            </div>
                            <h4 style="font-size: 15px; margin: 0 0 6px 0; font-weight: 700; color: #0F172A;">Cliente #${p.idCliente}</h4>
                            <p style="font-size: 13px; color: #475569; margin: 0 0 12px 0; line-height: 1.4;">
                                <c:forEach var="det" items="${p.detalles}" varStatus="status">
                                    ${det.cantidad} ${det.nombreProducto}${!status.last ? ', ' : ''}
                                </c:forEach>
                            </p>
                            <div style="margin-bottom: 14px;">
                                <span style="background: #D1FAE5; color: #065F46; padding: 4px 10px; border-radius: 20px; font-size: 11px; font-weight: 700;">
                                    ${p.tipoEntrega}
                                </span>
                            </div>
                            
                            <form action="${ctx}/cocina" method="POST">
                                <input type="hidden" name="idPedido" value="${p.idPedido}">
                                <c:choose>
                                    <c:when test="${p.tipoEntrega eq 'DOMICILIO'}">
                                        <input type="hidden" name="estado" value="EN_CAMINO">
                                        <input type="hidden" name="comentario" value="Pedido en camino a domicilio">
                                        <button type="submit" class="boton" style="width: 100%; padding: 10px; font-size: 13px; border: none; cursor: pointer; border-radius: 10px; background-color: #2563EB; color: white; font-weight: 700; box-shadow: 0 2px 6px rgba(37, 99, 235, 0.3);">
                                            Enviar en camino
                                        </button>
                                    </c:when>
                                    <c:when test="${not usuario.cocina}">
                                        <!-- Solo cocina entrega y cobra; el admin solo observa -->
                                        <div style="background: #FEF3C7; color: #92400E; padding: 10px; border-radius: 10px; font-size: 13px; font-weight: 700; text-align: center;">Pendiente de cobro en cocina</div>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" name="estado" value="ENTREGADO">
                                        <input type="hidden" name="comentario" value="Pedido entregado en local">
                                        <span style="font-size: 12px; font-weight: 700; color: #475569;">Cobrado con:</span>
                                        <div style="display: flex; gap: 6px; margin-bottom: 10px;">
                                            <select name="metodoPago" class="campo" style="padding: 8px; font-size: 12px; border-radius: 8px; flex: 1; border: 1px solid #CBD5E1; font-weight: 600;" required>
                                                <option value="EFECTIVO">Efectivo</option>
                                                <option value="TARJETA">Tarjeta</option>
                                            </select>
                                        </div>
                                        <button type="submit" class="boton" style="width: 100%; padding: 10px; font-size: 13px; border: none; cursor: pointer; border-radius: 10px; background-color: #059669; color: white; font-weight: 700; box-shadow: 0 2px 6px rgba(5, 150, 105, 0.3);">
                                            Marcar entregado
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>

        <!-- COLUMNA 4: EN CAMINO -->
        <div style="background: #E0F2FE; border-radius: 16px; padding: 16px; min-height: 500px; border: 1px solid #BAE6FD;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; padding: 0 4px;">
                <h3 style="font-size: 16px; margin: 0; font-weight: 800; color: #0369A1;">En camino</h3>
                <span style="background: #0284C7; color: white; padding: 2px 10px; border-radius: 50px; font-size: 12px; font-weight: bold;" id="count-camino">0</span>
            </div>
            
            <div class="columna-pedidos" data-estado="EN_CAMINO">
                <c:forEach var="p" items="${pedidos}">
                    <c:if test="${p.estado eq 'EN_CAMINO'}">
                        <div class="tarjeta tarjeta-pedido" data-tipo="${p.tipoEntrega}" style="background: white; border-radius: 14px; padding: 16px; margin-bottom: 14px; box-shadow: 0 4px 12px rgba(2, 132, 199, 0.08); border-left: 5px solid #0284C7;">
                            <div style="display: flex; justify-content: space-between; font-weight: 800; font-size: 16px; margin-bottom: 6px; color: #1E293B;">
                                <span>#${p.idPedido}</span>
                                <span style="font-size: 12px; color: #64748B; font-weight: 600;">${p.fechaCreacionTexto}</span>
                            </div>
                            <h4 style="font-size: 15px; margin: 0 0 6px 0; font-weight: 700; color: #0F172A;">Cliente #${p.idCliente}</h4>
                            <p style="font-size: 13px; color: #475569; margin: 0 0 12px 0; line-height: 1.4;">
                                <c:forEach var="det" items="${p.detalles}" varStatus="status">
                                    ${det.cantidad} ${det.nombreProducto}${!status.last ? ', ' : ''}
                                </c:forEach>
                            </p>
                            <div style="margin-bottom: 14px;">
                                <span style="background: #E0F2FE; color: #0369A1; padding: 4px 10px; border-radius: 20px; font-size: 11px; font-weight: 700;">DOMICILIO</span>
                            </div>
                            
                            <c:choose>
                            <c:when test="${not usuario.cocina}">
                                <!-- Solo cocina entrega y cobra; el admin solo observa -->
                                <div style="background: #FEF3C7; color: #92400E; padding: 10px; border-radius: 10px; font-size: 13px; font-weight: 700; text-align: center;">Pendiente de cobro en cocina</div>
                            </c:when>
                            <c:otherwise>
                            <form action="${ctx}/cocina" method="POST">
                                <input type="hidden" name="idPedido" value="${p.idPedido}">
                                <input type="hidden" name="estado" value="ENTREGADO">
                                <input type="hidden" name="comentario" value="Pedido entregado a domicilio">
                                <span style="font-size: 12px; font-weight: 700; color: #475569;">El repartidor cobró con:</span>
                                <div style="display: flex; gap: 6px; margin-bottom: 10px;">
                                    <select name="metodoPago" class="campo" style="padding: 8px; font-size: 12px; border-radius: 8px; flex: 1; border: 1px solid #CBD5E1; font-weight: 600;" required>
                                        <option value="EFECTIVO">Efectivo</option>
                                        <option value="TARJETA">Tarjeta</option>
                                    </select>
                                </div>
                                <button type="submit" class="boton" style="width: 100%; padding: 10px; font-size: 13px; border: none; cursor: pointer; border-radius: 10px; background-color: #0284C7; color: white; font-weight: 700; box-shadow: 0 2px 6px rgba(2, 132, 199, 0.3);">
                                    Confirmar entrega
                                </button>
                            </form>
                            </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>

    </div>
</div>

<script>
    function filtrarPedidos() {
        const tipoSeleccionado = document.getElementById('filtroEntrega').value;
        const tarjetas = document.querySelectorAll('.tarjeta-pedido');
        
        let cRecibidos = 0, cPreparacion = 0, cListos = 0, cCamino = 0;

        tarjetas.forEach(tarjeta => {
            const tipoPedido = tarjeta.getAttribute('data-tipo');
            const estadoColumna = tarjeta.closest('.columna-pedidos').getAttribute('data-estado');
            
            const coincide = (tipoSeleccionado === 'TODOS' || tipoPedido === tipoSeleccionado);
            
            if (coincide) {
                tarjeta.style.display = 'block';
                if (estadoColumna === 'RECIBIDO') cRecibidos++;
                if (estadoColumna === 'EN_PREPARACION') cPreparacion++;
                if (estadoColumna === 'LISTO') cListos++;
                if (estadoColumna === 'EN_CAMINO') cCamino++;
            } else {
                tarjeta.style.display = 'none';
            }
        });

        document.getElementById('count-recibidos').innerText = cRecibidos;
        document.getElementById('count-preparacion').innerText = cPreparacion;
        document.getElementById('count-listos').innerText = cListos;
        document.getElementById('count-camino').innerText = cCamino;
    }

    window.onload = function() {
        filtrarPedidos();
    };
</script>

<%@ include file="/WEB-INF/views/plantilla/pie-admin.jspf" %>