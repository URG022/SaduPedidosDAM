<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="titulo" value="Nuestro Menú"/>
<c:set var="seccion" value="menu"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado.jspf" %>

<style>
    .menu-cabecera {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        align-items: flex-end;
        gap: 20px;
        margin-bottom: 24px;
    }
    .menu-titulo {
        display: flex;
        flex-direction: column;
        gap: 4px;
    }
    .menu-buscador {
        width: 100%;
        max-width: 320px;
    }
    .menu-filtros {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        margin-bottom: 32px;
    }
    .platillo-foto {
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 800;
        color: var(--sadu-texto-suave);
    }
    .platillo-pie {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: auto;
    }
</style>

<main class="pagina">
    <div class="contenedor">
        
        <!-- Cabecera y Buscador -->
        <div class="menu-cabecera">
            <div class="menu-titulo">
                <span class="etiqueta">Nuestro menú</span>
                <h1>¿Qué se te antoja hoy?</h1>
            </div>
            <div class="menu-buscador campo">
                <label for="buscar">Buscar platillo</label>
                <input type="text" id="buscar" placeholder="Ej. pollo, horchata...">
            </div>
        </div>

        <!-- Filtros -->
       <div class="menu-filtros" id="filtros-categoria">
            <button class="btn btn-azul btn-chico" data-cat="Todos">Todos</button>
            <button class="btn btn-borde btn-chico" data-cat="Desayunos">Desayunos</button>
            <button class="btn btn-borde btn-chico" data-cat="Platos fuertes">Platos fuertes</button>
            <button class="btn btn-borde btn-chico" data-cat="Bebidas">Bebidas</button>
            <button class="btn btn-borde btn-chico" data-cat="Postres">Postres</button>
        </div>

        <!-- Grid de Platillos utilizando las clases globales -->
        <!-- Grid de Platillos -->
        <div class="grid-tarjetas">
            <c:if test="${empty productos}">
                <p class="texto-suave">No hay productos registrados en el menú por ahora.</p>
            </c:if>

            <c:forEach var="p" items="${productos}">
                <!-- Definimos la categoría en una variable para usarla en el HTML y en el data-atributo -->
                <c:choose>
                    <c:when test="${p.idCategoria == 1}"><c:set var="nombreCat" value="Desayunos"/></c:when>
                    <c:when test="${p.idCategoria == 2}"><c:set var="nombreCat" value="Platos fuertes"/></c:when>
                    <c:when test="${p.idCategoria == 3}"><c:set var="nombreCat" value="Bebidas"/></c:when>
                    <c:when test="${p.idCategoria == 4}"><c:set var="nombreCat" value="Postres"/></c:when>
                    <c:otherwise><c:set var="nombreCat" value="Categoría"/></c:otherwise>
                </c:choose>

                <!-- Agregamos data-categoria a la tarjeta -->
                <article class="platillo" data-categoria="${nombreCat}">
                    <div class="platillo-foto" 
                         <c:if test="${not empty p.imagenUrl}">style="background-image: url('${ctx}/resources/img/${p.imagenUrl}')"</c:if>>
                        <c:if test="${empty p.imagenUrl}">[Foto]</c:if>
                    </div>
                    
                    <div class="platillo-cuerpo">
                        <span class="etiqueta" style="font-size: 11px;"><c:out value="${nombreCat}"/></span>
                        <h3><c:out value="${p.nombre}"/></h3>
                        <p class="texto-suave" style="flex: 1; font-size: 14px;"><c:out value="${p.descripcion}"/></p>
                        
                        <div class="platillo-pie">
                            <span class="precio">$<fmt:formatNumber value="${p.precio}" pattern="#,##0.00"/></span>
                            <button class="btn btn-sol btn-chico">Agregar</button>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </div>
        
    </div>
</main>

<script src="${ctx}/resources/js/menu.js"></script>
<%@ include file="/WEB-INF/views/plantilla/pie.jspf" %>