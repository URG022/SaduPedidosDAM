<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Editar Cliente"/>
<c:set var="seccion" value="clientes"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado-admin.jspf" %>

<div class="layout-admin">
    <main class="contenido-admin">
        
        <div style="margin-bottom: 20px;">
            <a href="${ctx}/admin/clientes" class="texto-suave" style="text-decoration: none;">&larr; Volver al directorio</a>
            <h2 style="margin-top: 10px;">Editar Cliente</h2>
        </div>

        <c:if test="${not empty error}">
            <p class="alerta alerta-error"><c:out value="${error}"/></p>
        </c:if>

        <div class="tarjeta" style="max-width: 600px;">
            <form action="${ctx}/admin/clientes" method="POST" class="formulario">
                
                <!-- ID Oculto necesario para el UPDATE -->
                <input type="hidden" name="idUsuario" value="${cliente.idUsuario}">

                <div class="campo">
                    <label for="nombreCompleto">Nombre Completo</label>
                    <input type="text" id="nombreCompleto" name="nombreCompleto" value="${cliente.nombreCompleto}" required maxlength="120">
                </div>

                <div class="campo">
                    <label for="correo">Correo Electrónico</label>
                    <input type="email" id="correo" name="correo" value="${cliente.correo}" required maxlength="160">
                </div>

                <div class="campo">
                    <label for="telefono">Teléfono</label>
                    <input type="tel" id="telefono" name="telefono" value="${cliente.telefono}" required maxlength="15">
                </div>

                <div class="campo">
                    <label for="direccion">Dirección</label>
                    <textarea id="direccion" name="direccion" rows="3" maxlength="200">${cliente.direccion}</textarea>
                </div>

                <div style="display: flex; gap: 12px; margin-top: 24px;">
                    <button type="submit" class="btn btn-sol">Guardar Cambios</button>
                    <a href="${ctx}/admin/clientes" class="btn btn-borde">Cancelar</a>
                </div>
            </form>
        </div>

    </main>
</div>

<%@ include file="/WEB-INF/views/plantilla/pie-admin.jspf" %>