<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="titulo" value="Mi carrito"/>
<c:set var="seccion" value="carrito"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado.jspf" %>
<link rel="stylesheet" href="${ctx}/resources/css/pedidos.css">

<main class="pagina">
    <div class="contenedor">

        <div class="menu-titulo">
            <span class="etiqueta">Tu pedido</span>
            <h1>Mi carrito</h1>
        </div>

        <c:if test="${not empty error}">
            <div class="alerta alerta-error"><c:out value="${error}"/></div>
        </c:if>
        <c:if test="${param.error eq 'vacio' and empty error}">
            <div class="alerta alerta-error">Tu carrito está vacío. Agrega algún platillo para continuar.</div>
        </c:if>

        <c:choose>

            <%-- ========== Carrito vacío ========== --%>
            <c:when test="${carrito.vacio}">
                <div class="tarjeta carrito-vacio">
                    <h2>Tu carrito está vacío</h2>
                    <p class="texto-suave">Todavía no has agregado ningún platillo.</p>
                    <a class="btn btn-sol" href="${ctx}/cliente/productos">Ver el menú</a>
                </div>
            </c:when>

            <%-- ========== Carrito con platillos ========== --%>
            <c:otherwise>
                <div class="carrito-layout">

                    <!-- Lista de platillos -->
                    <section class="tarjeta carrito-lista">
                        <c:forEach var="item" items="${carrito.items}">
                            <article class="carrito-item">

                                <div class="carrito-item-info">
                                    <h3><c:out value="${item.nombre}"/></h3>
                                    <span class="texto-suave">$<fmt:formatNumber value="${item.precio}" pattern="#,##0.00"/> c/u</span>
                                </div>

                                <div class="cantidad-control">
                                    <form class="form-inline" method="post" action="${ctx}/carrito">
                                        <input type="hidden" name="accion" value="restar">
                                        <input type="hidden" name="idProducto" value="${item.idProducto}">
                                        <button type="submit" class="btn btn-borde btn-chico btn-cantidad" aria-label="Quitar una unidad">−</button>
                                    </form>

                                    <span class="cantidad">${item.cantidad}</span>

                                    <form class="form-inline" method="post" action="${ctx}/carrito">
                                        <input type="hidden" name="accion" value="sumar">
                                        <input type="hidden" name="idProducto" value="${item.idProducto}">
                                        <button type="submit" class="btn btn-borde btn-chico btn-cantidad" aria-label="Agregar una unidad">+</button>
                                    </form>
                                </div>

                                <strong class="carrito-subtotal">$<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/></strong>

                                <form class="form-inline" method="post" action="${ctx}/carrito">
                                    <input type="hidden" name="accion" value="eliminar">
                                    <input type="hidden" name="idProducto" value="${item.idProducto}">
                                    <button type="submit" class="btn btn-peligro btn-chico">Quitar</button>
                                </form>

                            </article>
                        </c:forEach>

                        <div class="carrito-acciones">
                            <a class="btn btn-borde btn-chico" href="${ctx}/cliente/productos">Seguir pidiendo</a>
                            <form class="form-inline" method="post" action="${ctx}/carrito">
                                <input type="hidden" name="accion" value="vaciar">
                                <button type="submit" class="btn btn-peligro btn-chico">Vaciar carrito</button>
                            </form>
                        </div>
                    </section>

                    <!-- Resumen y datos del pedido -->
                    <aside class="tarjeta resumen">
                        <h2>Resumen</h2>

                        <div class="resumen-fila">
                            <span class="texto-suave">Platillos</span>
                            <strong>${carrito.cantidadTotal}</strong>
                        </div>
                        <div class="resumen-fila resumen-total">
                            <span>Total</span>
                            <span>$<fmt:formatNumber value="${carrito.total}" pattern="#,##0.00"/></span>
                        </div>

                        <form id="form-pedido" class="formulario" method="post" action="${ctx}/cliente/pedidos">
                            <input type="hidden" name="accion" value="confirmar">

                            <label class="campo">Tipo de entrega
                                <select id="tipoEntrega" name="tipoEntrega" required>
                                    <c:forEach var="t" items="${tiposEntrega}">
                                        <c:set var="tNombre">${t}</c:set>
                                        <option value="${tNombre}" <c:if test="${param.tipoEntrega eq tNombre}">selected</c:if>>${t.etiqueta}</option>
                                    </c:forEach>
                                </select>
                            </label>

                            <label class="campo" id="campo-direccion">Dirección de entrega
                                <input type="text" id="direccionEntrega" name="direccionEntrega" maxlength="200"
                                       placeholder="Colonia, calle y número de casa"
                                       value="<c:out value='${not empty param.direccionEntrega ? param.direccionEntrega : cliente.direccion}'/>">
                                <small>Obligatoria para pedidos a domicilio.</small>
                            </label>

                            <label class="campo">Notas para la cocina (opcional)
                                <textarea name="notas" rows="3" maxlength="300" placeholder="Ej. sin cebolla"><c:out value="${param.notas}"/></textarea>
                            </label>

                            <button type="submit" id="btn-confirmar" class="btn btn-sol btn-grande btn-bloque">Confirmar pedido</button>
                        </form>
                    </aside>

                </div>
            </c:otherwise>

        </c:choose>
    </div>
</main>

<c:if test="${not carrito.vacio}">
<script>
    (function () {
        var form = document.getElementById('form-pedido');
        var tipo = document.getElementById('tipoEntrega');
        var campo = document.getElementById('campo-direccion');
        var direccion = document.getElementById('direccionEntrega');
        var boton = document.getElementById('btn-confirmar');

        // La dirección solo se pide (y es obligatoria) cuando es a domicilio
        function actualizar() {
            var domicilio = tipo.value === 'DOMICILIO';
            campo.hidden = !domicilio;
            direccion.required = domicilio;
        }
        tipo.addEventListener('change', actualizar);
        actualizar();

        // Evita crear el pedido dos veces por doble clic
        form.addEventListener('submit', function () {
            boton.disabled = true;
            boton.textContent = 'Enviando…';
        });
    })();
</script>
</c:if>

<%@ include file="/WEB-INF/views/plantilla/pie.jspf" %>