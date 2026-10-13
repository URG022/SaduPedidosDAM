<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="titulo" value="Inicio"/>
<c:set var="seccion" value="inicio"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado.jspf" %>

<main class="pagina">
    <div class="contenedor">
        <c:if test="${param.bienvenida == '1' && usuario != null}">
            <p class="alerta alerta-exito">¡Bienvenido a Sadu, <c:out value="${usuario.primerNombre}"/>! Tu cuenta quedó creada.</p>
        </c:if>

        <section class="hero">
            <div class="hero-texto">
                <span class="etiqueta">Pedidos en línea</span>
                <h1>Tu comida favorita, del sol de Sadu a tu mesa</h1>
                <p>Arma tu pedido, elige si lo comes aquí, lo llevas o te lo enviamos, y sigue cada paso desde la cocina hasta tu mesa.</p>
                <div class="hero-botones">
                    <a class="btn btn-sol btn-grande" href="${ctx}/menu">Ordenar ahora</a>
                    <a class="btn btn-azul btn-grande" href="${ctx}/menu">Ver menú</a>
                </div>
            </div>
            <div class="hero-sol" aria-hidden="true">
                <div class="circulo"><img src="${ctx}/resources/img/logo-sadu.png" alt=""></div>
            </div>
        </section>

        <section style="display: flex; flex-direction: column; gap: 20px">
            <div class="seccion-titulo">
                <h2>Destacados de la casa</h2>
                <a href="${ctx}/menu"><strong>Ver todo el menú</strong></a>
            </div>

            <c:if test="${avisoBD}">
                <p class="alerta alerta-info">No pudimos cargar los platillos destacados. Revisa la conexión con la base de datos.</p>
            </c:if>

            <div class="grid-tarjetas" style="grid-template-columns: repeat(auto-fill, minmax(200px, 1fr))">
                <c:forEach var="p" items="${destacados}">
                    <article class="platillo">
                        <div class="platillo-foto" style="background-image: url('${ctx}/resources/img/${p.imagenUrl}')" role="img" aria-label="<c:out value='${p.nombre}'/>"></div>
                        <div class="platillo-cuerpo">
                            <span class="etiqueta" style="font-size: 12px"><c:out value="${p.categoria}"/></span>
                            <h3><c:out value="${p.nombre}"/></h3>
                            <p class="texto-suave" style="margin: 0; font-size: 14px; flex: 1"><c:out value="${p.descripcion}"/></p>
                            <span class="precio">$<fmt:formatNumber value="${p.precio}" pattern="#,##0.00"/></span>
                        </div>
                    </article>
                </c:forEach>
            </div>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/plantilla/pie.jspf" %>
