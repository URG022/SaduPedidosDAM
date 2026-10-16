<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Administración de Menú"/>
<c:set var="seccion" value="productos"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado-admin.jspf" %>
<div class="layout-admin">

    <!-- Contenido Principal -->
    <main class="contenido-admin">

        <!-- Título y Botón Superior -->
        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px;">
            <h1>Gestión de Menú</h1>
            <a href="${ctx}/admin/productos?accion=nuevo" class="btn btn-sol">Nuevo Platillo</a>
        </div>

        <!-- Alertas de Error/Éxito -->
        <c:if test="${not empty error}">
            <p class="alerta alerta-error"><c:out value="${error}"/></p>
        </c:if>
        <c:if test="${not empty param.exito}">
            <p class="alerta alerta-exito">Operación realizada con éxito.</p>
        </c:if>

        <!-- Tabla de Datos -->
        <div class="tabla-caja">
            <table class="tabla">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Categoría</th>
                        <th>Precio</th>
                        <th>Estado</th>
                        <th style="width: 180px;">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty productos}">
                            <tr>
                                <td colspan="6" style="text-align: center; padding: 30px;" class="texto-suave">
                                    No hay platillos registrados aún.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="p" items="${productos}">
                                <tr>
                                    <td>${p.idProducto}</td>
                                    <td><strong><c:out value="${p.nombre}"/></strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.idCategoria == 1}">Desayunos</c:when>
                                            <c:when test="${p.idCategoria == 2}">Platos fuertes</c:when>
                                            <c:when test="${p.idCategoria == 3}">Bebidas</c:when>
                                            <c:when test="${p.idCategoria == 4}">Postres</c:when>
                                            <c:otherwise>Otra</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>$${p.precio}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.disponible}">
                                                <span class="badge badge-ENTREGADO">Activo</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-CANCELADO">Inactivo</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div style="display: flex; gap: 8px;">
                                            <a href="${ctx}/admin/productos?accion=editar&id=${p.idProducto}" class="btn btn-borde btn-chico">Editar</a>

                                            <c:choose>
                                                <c:when test="${p.disponible}">
                                                    <!-- Botón de Eliminar (Desactivar) -->
                                                    <button onclick="confirmarEliminacion(${p.idProducto})" class="btn btn-peligro btn-chico">Eliminar</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <!-- Botón de Activar -->
                                                    <button onclick="confirmarActivacion(${p.idProducto})" class="btn btn-verde-suave btn-chico">Activar</button>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
        <!-- Paginación -->
        <c:if test="${totalPaginas > 1}">
            <div style="display: flex; gap: 8px; justify-content: center; margin-top: 24px;">

                <!-- Botón Anterior -->
                <c:if test="${paginaActual > 1}">
                    <a href="${ctx}/admin/productos?pagina=${paginaActual - 1}" class="btn btn-borde btn-chico">Anterior</a>
                </c:if>

                <!-- Números de Página -->
                <c:forEach begin="1" end="${totalPaginas}" var="i">
                    <a href="${ctx}/admin/productos?pagina=${i}" 
                       class="btn btn-chico ${paginaActual == i ? 'btn-azul' : 'btn-borde'}">
                        ${i}
                    </a>
                </c:forEach>

                <!-- Botón Siguiente -->
                <c:if test="${paginaActual < totalPaginas}">
                    <a href="${ctx}/admin/productos?pagina=${paginaActual + 1}" class="btn btn-borde btn-chico">Siguiente</a>
                </c:if>

            </div>
        </c:if>

    </main>
</div>

<!-- Modal de Confirmación de Eliminación -->
<div class="modal-fondo" id="modalEliminar">
    <div class="tarjeta modal-caja">
        <h3>¿Desactivar Platillo?</h3>
        <p class="texto-suave" style="margin-bottom: 24px;">El platillo ya no será visible para los clientes en el menú. ¿Estás seguro de que deseas continuar?</p>
        <div style="display: flex; gap: 12px; justify-content: center;">
            <button type="button" class="btn btn-borde" onclick="cerrarModal()">Cancelar</button>
            <a href="#" id="btnConfirmarEliminar" class="btn btn-peligro">Sí, eliminar</a>
        </div>
    </div>
</div>

<!-- Modal de Confirmación de Activación -->
<div class="modal-fondo" id="modalActivar">
    <div class="tarjeta modal-caja">
        <h3 style="color: #2e7d32;">¿Reactivar Platillo?</h3>
        <p class="texto-suave" style="margin-bottom: 24px;">El platillo volverá a ser visible para los clientes en el menú principal. ¿Deseas continuar?</p>
        <div style="display: flex; gap: 12px; justify-content: center;">
            <button type="button" class="btn btn-borde" onclick="cerrarModalActivar()">Cancelar</button>
            <a href="#" id="btnConfirmarActivar" class="btn btn-verde-suave">Sí, reactivar</a>
        </div>
    </div>
</div>

<script>
    //DESACTIVAR
    function confirmarEliminacion(idProducto) {
        document.getElementById('btnConfirmarEliminar').href = '${ctx}/admin/productos?accion=eliminar&id=' + idProducto;
        document.getElementById('modalEliminar').classList.add('activo');
    }
    function cerrarModal() {
        document.getElementById('modalEliminar').classList.remove('activo');
    }

    //ACTIVAR
    function confirmarActivacion(idProducto) {
        document.getElementById('btnConfirmarActivar').href = '${ctx}/admin/productos?accion=activar&id=' + idProducto;
        document.getElementById('modalActivar').classList.add('activo');
    }
    function cerrarModalActivar() {
        document.getElementById('modalActivar').classList.remove('activo');
    }
</script>
<%@ include file="/WEB-INF/views/plantilla/pie-admin.jspf" %>