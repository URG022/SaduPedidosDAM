<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Administración de Menú"/>
<c:set var="seccion" value="productos"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado-admin.jspf" %>
<div class="layout-admin">
    

    <!-- Contenido Principal -->
    <main class="contenido-admin" style="justify-content: center; align-items: center;">
        
        <div style="display: flex; align-items: center; gap: 16px;">
            <h1>${empty producto ? 'Registrar Nuevo Platillo' : 'Editar Platillo'}</h1>
        </div>

        <c:if test="${not empty error}">
            <p class="alerta alerta-error"><c:out value="${error}"/></p>
        </c:if>

        <div class="tarjeta" style="max-width: 700px;">
            <form action="${ctx}/admin/productos" method="POST" class="formulario">
                
                <!-- Campo oculto para el ID: Si está vacío, el Servlet sabe que es un registro nuevo -->
                <input type="hidden" name="idProducto" value="${producto.idProducto}">
                
                <div class="fila-2">
                    <div class="campo">
                        <label for="nombre">Nombre del Platillo</label>
                        <input type="text" id="nombre" name="nombre" value="${producto.nombre}" required maxlength="100">
                    </div>
                    <div class="campo">
                        <label for="idCategoria">Categoría</label>
                        <select id="idCategoria" name="idCategoria" required>
                            <option value="" disabled ${empty producto ? 'selected' : ''}>Seleccione una...</option>
                            <option value="1" ${producto.idCategoria == 1 ? 'selected' : ''}>Desayunos</option>
                            <option value="2" ${producto.idCategoria == 2 ? 'selected' : ''}>Platos fuertes</option>
                            <option value="3" ${producto.idCategoria == 3 ? 'selected' : ''}>Bebidas</option>
                            <option value="4" ${producto.idCategoria == 4 ? 'selected' : ''}>Postres</option>
                        </select>
                    </div>
                </div>

                <div class="fila-2">
                    <div class="campo">
                        <label for="precio">Precio ($)</label>
                        <input type="number" id="precio" name="precio" value="${producto.precio}" step="0.01" min="0" required>
                    </div>
                    <div class="campo">
                        <label for="imagenUrl">Nombre de Imagen (Ej. cafe.jpg)</label>
                        <input type="text" id="imagenUrl" name="imagenUrl" value="${producto.imagenUrl}" placeholder="Opcional">
                    </div>
                </div>

                <div class="campo">
                    <label for="descripcion">Descripción corta</label>
                    <textarea id="descripcion" name="descripcion" rows="3" required maxlength="255">${producto.descripcion}</textarea>
                </div>

                <!-- Casillas de verificación para booleanos -->
                <div class="fila-2" style="margin-top: 10px;">
                    <label class="casilla">
                        <input type="checkbox" name="disponible" value="true" ${empty producto || producto.disponible ? 'checked' : ''}>
                        Disponible (Mostrar en el menú)
                    </label>
                    <label class="casilla">
                        <input type="checkbox" name="destacado" value="true" ${producto.destacado ? 'checked' : ''}>
                        Destacado (Para promociones)
                    </label>
                </div>

                <div style="display: flex; gap: 12px; margin-top: 24px;">
                    <button type="submit" class="btn btn-sol btn-grande">Guardar Platillo</button>
                    <a href="${ctx}/admin/productos" class="btn btn-borde btn-grande">Cancelar</a>
                </div>
            </form>
        </div>

    </main>
</div>

<%@ include file="/WEB-INF/views/plantilla/pie-admin.jspf" %>