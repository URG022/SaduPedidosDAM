<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Administración de Clientes"/>
<!-- Variable para encender la opción "Clientes" en el menú -->
<c:set var="seccion" value="clientes"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado-admin.jspf" %>

<div class="layout-admin">

    <!-- Contenido Principal -->
    <main class="contenido-admin">

        <!-- Título -->
        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px;">
            <h1>Directorio de Clientes</h1>
        </div>

        <!-- Alertas de Error/Éxito -->
        <c:if test="${not empty error}">
            <p class="alerta alerta-error"><c:out value="${error}"/></p>
        </c:if>
        <c:if test="${param.exito == '1'}">
            <p class="alerta alerta-exito">La cuenta ha sido desactivada con éxito.</p>
        </c:if>
        <c:if test="${param.exito == '2'}">
            <p class="alerta alerta-exito">La cuenta ha sido reactivada con éxito.</p>
        </c:if>

        <!-- Tabla de Datos -->
        <div class="tabla-caja">
            <table class="tabla">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre Completo</th>
                        <th>Correo</th>
                        <th>Teléfono</th>
                        <th>Estado</th>
                        <th style="width: 120px;">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty clientes}">
                            <tr>
                                <td colspan="6" style="text-align: center; padding: 30px;" class="texto-suave">
                                    No hay clientes registrados aún.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="c" items="${clientes}">
                                <tr>
                                    <td>${c.idUsuario}</td>
                                    <td><strong><c:out value="${c.nombreCompleto}"/></strong></td>
                                    <td><c:out value="${c.correo}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty c.telefono}"><c:out value="${c.telefono}"/></c:when>
                                            <c:otherwise><span class="texto-suave">-</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${c.activo}">
                                                <span class="badge badge-ENTREGADO">Activo</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-CANCELADO">Inactivo</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${c.activo}">
                                                <!-- Botón para abrir el modal de desactivar -->
                                                <button onclick="confirmarDesactivacion(${c.idUsuario})" class="btn btn-peligro btn-chico">Desactivar</button>
                                            </c:when>
                                            <c:otherwise>
                                                <!-- Botón para abrir el modal de activar -->
                                                <button onclick="confirmarActivacion(${c.idUsuario})" class="btn btn-verde-suave btn-chico">Activar</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

    </main>
</div>

<!-- Modal de Confirmación de Desactivación -->
<div class="modal-fondo" id="modalDesactivar">
    <div class="tarjeta modal-caja">
        <h3>¿Desactivar Cliente?</h3>
        <p class="texto-suave" style="margin-bottom: 24px;">El cliente ya no podrá iniciar sesión en la plataforma. ¿Estás seguro de que deseas continuar?</p>
        <div style="display: flex; gap: 12px; justify-content: center;">
            <button type="button" class="btn btn-borde" onclick="cerrarModal()">Cancelar</button>
            <a href="#" id="btnConfirmarDesactivar" class="btn btn-peligro">Sí, desactivar</a>
        </div>
    </div>
</div>


<!-- Modal de Confirmación de Activación -->
<div class="modal-fondo" id="modalActivar">
    <div class="tarjeta modal-caja">
        <h3 style="color: var(--sadu-exito);">¿Reactivar Cliente?</h3>
        <p class="texto-suave" style="margin-bottom: 24px;">El cliente recuperará el acceso a su cuenta y podrá volver a realizar pedidos.</p>
        <div style="display: flex; gap: 12px; justify-content: center;">
            <button type="button" class="btn btn-borde" onclick="cerrarModalActivar()">Cancelar</button>
            <a href="#" id="btnConfirmarActivar" class="btn btn-verde-suave">Sí, reactivar</a>
        </div>
    </div>
</div>

<script>
    //DESACTIVAR
    function confirmarDesactivacion(idUsuario) {
        document.getElementById('btnConfirmarDesactivar').href = '${ctx}/admin/clientes?accion=desactivar&id=' + idUsuario;
        document.getElementById('modalDesactivar').classList.add('activo');
    }
    function cerrarModal() {
        document.getElementById('modalDesactivar').classList.remove('activo');
    }

    // ACTIVAR
    function confirmarActivacion(idUsuario) {
        document.getElementById('btnConfirmarActivar').href = '${ctx}/admin/clientes?accion=activar&id=' + idUsuario;
        document.getElementById('modalActivar').classList.add('activo');
    }
    function cerrarModalActivar() {
        document.getElementById('modalActivar').classList.remove('activo');
    }
</script>

<%@ include file="/WEB-INF/views/plantilla/pie-admin.jspf" %>