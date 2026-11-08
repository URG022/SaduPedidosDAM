<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Categorías del menú"/>
<c:set var="seccion" value="productos"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado-admin.jspf" %>
<div class="layout-admin">
    <main class="contenido-admin">

        <!-- Título y acciones -->
        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px;">
            <div>
                <a href="${ctx}/admin/productos" class="texto-suave" style="text-decoration: none;">&larr; Volver a platillos</a>
                <h1 style="margin-top: 6px;">Categorías del menú</h1>
                <p class="texto-suave" style="margin: 0;">El orden define cómo aparecen los botones de filtro en el menú del cliente.</p>
            </div>
            <a href="${ctx}/admin/categorias?accion=nuevo" class="btn btn-sol">Nueva categoría</a>
        </div>

        <!-- Alertas -->
        <c:if test="${param.error == '1'}">
            <p class="alerta alerta-error">No se encontró la categoría indicada.</p>
        </c:if>
        <c:if test="${param.exito == 'guardada'}">
            <p class="alerta alerta-exito">La categoría se guardó correctamente.</p>
        </c:if>
        <c:if test="${param.exito == 'desactivar'}">
            <p class="alerta alerta-exito">Categoría desactivada: sus platillos ya no se muestran en el menú del cliente.</p>
        </c:if>
        <c:if test="${param.exito == 'activar'}">
            <p class="alerta alerta-exito">Categoría reactivada.</p>
        </c:if>

        <!-- Tabla -->
        <div class="tabla-caja">
            <table class="tabla">
                <thead>
                    <tr>
                        <th>Orden</th>
                        <th>Nombre</th>
                        <th>Descripción</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="cat" items="${categorias}">
                        <tr>
                            <td>${cat.orden}</td>
                            <td><strong><c:out value="${cat.nombre}"/></strong></td>
                            <td class="texto-suave"><c:out value="${cat.descripcion}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${cat.activa}"><span class="badge badge-ENTREGADO">Activa</span></c:when>
                                    <c:otherwise><span class="badge badge-CANCELADO">Inactiva</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div style="display: flex; gap: 8px;">
                                    <a href="${ctx}/admin/categorias?accion=editar&id=${cat.idCategoria}" class="btn btn-borde btn-chico">Editar</a>
                                    <c:choose>
                                        <c:when test="${cat.activa}">
                                            <a href="${ctx}/admin/categorias?accion=desactivar&id=${cat.idCategoria}" class="btn btn-peligro btn-chico">Desactivar</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${ctx}/admin/categorias?accion=activar&id=${cat.idCategoria}" class="btn btn-verde-suave btn-chico">Activar</a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty categorias}">
                        <tr><td colspan="5" class="texto-suave" style="text-align: center; padding: 30px;">No hay categorías registradas.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

    </main>
</div>
<%@ include file="/WEB-INF/views/plantilla/pie-admin.jspf" %>