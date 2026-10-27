<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Mi Perfil"/>
<c:set var="seccion" value="perfil"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado.jspf" %>

<main class="pagina">
    <div class="contenedor" style="display: flex; flex-direction: column; align-items: center;">
        
        <div class="hero" style="padding: 16px 0 24px; text-align: center;">
            <div class="hero-texto" style="align-items: center;">
                <h1>Mi Perfil</h1>
                <p class="texto-suave">Actualiza tu información personal y datos de entrega.</p>
            </div>
        </div>

        <!-- Alertas -->
        <c:if test="${not empty error}">
            <p class="alerta alerta-error" style="width: 100%; max-width: 600px; margin-bottom: 20px;"><c:out value="${error}"/></p>
        </c:if>
        <c:if test="${not empty param.exito}">
            <p class="alerta alerta-exito" style="width: 100%; max-width: 600px; margin-bottom: 20px;">Tu perfil ha sido actualizado con éxito.</p>
        </c:if>

        <!-- Formulario -->
        <div class="tarjeta" style="width: 100%; max-width: 600px;">
            <form action="${ctx}/cliente/perfil" method="POST" class="formulario">
                
                <div class="campo">
                    <label for="nombreCompleto">Nombre Completo</label>
                    <input type="text" id="nombreCompleto" name="nombreCompleto" value="${perfil.nombreCompleto}" required maxlength="120">
                </div>

                <div class="campo">
                    <label for="correo">Correo Electrónico</label>
                    <input type="email" id="correo" name="correo" value="${perfil.correo}" required maxlength="160">
                </div>

                <div class="campo">
                    <label for="telefono">Teléfono</label>
                    <input type="tel" id="telefono" name="telefono" value="${perfil.telefono}" required maxlength="15" placeholder="Ej. 7777-7777">
                </div>

                <div class="campo">
                    <label for="direccion">Dirección de Entrega</label>
                    <textarea id="direccion" name="direccion" rows="3" maxlength="200" placeholder="Ej. Colonia Escalón, Calle Principal, Casa #123...">${perfil.direccion}</textarea>
                </div>

                     <!-- Sección de seguridad -->
                <hr style="margin: 30px 0 20px; border: 0; border-top: 1px solid #e0e0e0;">
                <h3 style="margin-bottom: 5px;">Seguridad</h3>
                <p class="texto-suave" style="font-size: 0.9em; margin-bottom: 20px;">Deja estos campos en blanco si no deseas cambiar tu contraseña.</p>

                <div class="campo">
                    <label for="nuevaClave">Nueva Contraseña</label>
                    <input type="password" id="nuevaClave" name="nuevaClave" placeholder="Ingresa una nueva contraseña (opcional)">
                </div>

                <div class="campo">
                    <label for="confirmarClave">Confirmar Nueva Contraseña</label>
                    <input type="password" id="confirmarClave" name="confirmarClave" placeholder="Repite la nueva contraseña">
                </div>
                
                <div style="display: flex; gap: 12px; margin-top: 24px; justify-content: space-between; align-items: center; flex-wrap: wrap;">
                    <button type="submit" class="btn btn-sol btn-grande">Guardar Cambios</button>
                    <!-- Botón para abrir modal de desactivación -->
                    <button type="button" class="btn btn-peligro" onclick="abrirModalDesactivar()">Desactivar Cuenta</button>
                </div>
            </form>
        </div>
        
    </div>
</main>

<!-- Modal de Confirmación de Desactivación -->
<div class="modal-fondo" id="modalDesactivar">
    <div class="tarjeta modal-caja">
        <h3>¿Desactivar Cuenta?</h3>
        <p class="texto-suave" style="margin-bottom: 24px;">Ya no podrás iniciar sesión ni realizar pedidos en Sadu. ¿Estás seguro de que deseas continuar?</p>
        <div style="display: flex; gap: 12px; justify-content: center;">
            <button type="button" class="btn btn-borde" onclick="cerrarModal()">Cancelar</button>
            <a href="${ctx}/cliente/perfil?accion=desactivar" class="btn btn-peligro">Sí, desactivar</a>
        </div>
    </div>
</div>

<script>
    function abrirModalDesactivar() {
        document.getElementById('modalDesactivar').classList.add('activo');
    }
    function cerrarModal() {
        document.getElementById('modalDesactivar').classList.remove('activo');
    }
</script>

<%@ include file="/WEB-INF/views/plantilla/pie.jspf" %>