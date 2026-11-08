<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="titulo" value="${empty categoria.idCategoria ? 'Nueva categoría' : 'Editar categoría'}"/>
<c:set var="seccion" value="productos"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado-admin.jspf" %>
<div class="layout-admin">
    <main class="contenido-admin">

        <div style="margin-bottom: 20px;">
            <a href="${ctx}/admin/categorias" class="texto-suave" style="text-decoration: none;">&larr; Volver a categorías</a>
            <h2 style="margin-top: 10px;">${empty categoria.idCategoria ? 'Nueva categoría' : 'Editar categoría'}</h2>
        </div>

        <c:if test="${not empty error}">
            <p class="alerta alerta-error"><c:out value="${error}"/></p>
        </c:if>

        <div class="tarjeta" style="max-width: 600px;">
            <form action="${ctx}/admin/categorias" method="POST" class="formulario">
                <!-- Vacío = categoría nueva; con valor = edición -->
                <input type="hidden" name="idCategoria" value="${categoria.idCategoria}">

                <div class="campo">
                    <label for="nombre">Nombre</label>
                    <input type="text" id="nombre" name="nombre" value="${fn:escapeXml(categoria.nombre)}" required maxlength="80">
                </div>

                <div class="campo">
                    <label for="descripcion">Descripción (opcional)</label>
                    <textarea id="descripcion" name="descripcion" rows="2" maxlength="200">${fn:escapeXml(categoria.descripcion)}</textarea>
                </div>

                <div class="campo">
                    <label for="orden">Orden en el menú</label>
                    <input type="number" id="orden" name="orden" min="0" step="1" required
                           value="${empty categoria.orden ? 0 : categoria.orden}">
                </div>

                <label style="display: flex; align-items: center; gap: 8px; font-weight: 700;">
                    <input type="checkbox" name="activa" ${empty categoria or categoria.activa ? 'checked' : ''}>
                    Activa (se muestra en el menú del cliente)
                </label>

                <div style="display: flex; gap: 12px; margin-top: 24px;">
                    <button type="submit" class="btn btn-sol">Guardar categoría</button>
                    <a href="${ctx}/admin/categorias" class="btn btn-borde">Cancelar</a>
                </div>
            </form>
        </div>

    </main>
</div>
<%@ include file="/WEB-INF/views/plantilla/pie-admin.jspf" %>