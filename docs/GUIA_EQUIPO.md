# Guía para el equipo — cómo construir tu módulo

Lo que ya está listo (Dev 1): plantilla común, estilos "Sol de Sadu", conexión a MySQL,
inicio, login, registro, cerrar sesión, panel interno, filtro por rol y páginas de error.
**Tú solo construyes las pantallas de tu módulo** usando estas piezas.

## 1. Preparar tu computadora (una sola vez)

1. Clona el repo y abre la carpeta en NetBeans.
2. Crea la base en MySQL Workbench ejecutando en orden `sql/00`, `sql/01` y `sql/02`.
3. Copia `src/main/resources/db.properties.example` en la misma carpeta con el nombre
   **`db.properties`** y pon tu clave de MySQL. Este archivo NO se sube a GitHub.
4. Clean and Build → Run. Abre `http://localhost:8084/SaduPedidosDam/`.
5. Usuarios de prueba (clave `Sadu2026`): `admin@sadu.local`, `cocina@sadu.local`, `ana.martinez@demo.local`.

## 2. Rutas de cada módulo (respétalas para que los menús funcionen)

| Ruta | Pantalla | Dueño | Quién puede entrar |
| --- | --- | --- | --- |
| `/inicio`, `/login`, `/registro`, `/salir`, `/panel` | Inicio, acceso, panel | Dev 1 | listo ✅ |
| `/menu` | P3 Menú | Dev 2 | todos |
| `/admin/productos`, `/admin/categorias` | P8 Mantenimiento del menú | Dev 2 | administrador |
| `/admin/clientes` | P9 Clientes | Dev 2 | administrador |
| `/cliente/carrito` | P4 Carrito / nuevo pedido | Dev 3 | cliente |
| `/cliente/pedidos` | P5 Mis pedidos | Dev 3 | cliente |
| `/cocina/tablero` | P10 Tablero de cocina | Dev 3 | cocina y administrador |
| `/cliente/pedido?id=…` | P6 Detalle y comprobante | Dev 4 | cliente |
| `/admin/dashboard` | P7 Dashboard | Dev 4 | administrador |
| `/admin/reportes` | P11 Reportes | Dev 4 | administrador |

El filtro `AccesoFilter` ya protege `/cliente/*`, `/cocina/*`, `/admin/*`: **no tienes que validar
la sesión en tu Servlet**. Mientras una ruta no exista, el menú muestra la página 404 de Sadu.

## 3. Crear una pantalla (ejemplo: Dev 2, menú)

**Servlet** en `sv.edu.itca.sadu.controller`:

```java
@WebServlet(name = "MenuServlet", urlPatterns = {"/menu"})
public class MenuServlet extends HttpServlet {
    private final ProductoDAO productoDAO = new ProductoDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("productos", productoDAO.listarDisponibles());  // método que tú agregas al DAO
        request.getRequestDispatcher("/WEB-INF/views/menu/menu.jsp").forward(request, response);
    }
}
```

**Vista** en `src/main/webapp/WEB-INF/views/menu/menu.jsp`:

```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Menú"/>
<c:set var="seccion" value="menu"/>
<%@ include file="/WEB-INF/views/plantilla/encabezado.jspf" %>

<main class="pagina">
  <div class="contenedor">
    <h1>¿Qué se te antoja hoy?</h1>
    <div class="grid-tarjetas">
      <c:forEach var="p" items="${productos}">
        <article class="platillo">
          <div class="platillo-foto" style="background-image: url('${ctx}/resources/img/${p.imagenUrl}')"></div>
          <div class="platillo-cuerpo">
            <h3><c:out value="${p.nombre}"/></h3>
            <span class="precio">$${p.precio}</span>
            <button class="btn btn-sol">Agregar</button>
          </div>
        </article>
      </c:forEach>
    </div>
  </div>
</main>

<%@ include file="/WEB-INF/views/plantilla/pie.jspf" %>
```

Pantallas del **administrador o cocina**: usa `encabezado-admin.jspf` y `pie-admin.jspf`
(barra lateral azul) y pon tu contenido directo, sin `<main>`. Valores de `seccion`:
`dashboard`, `cocina`, `productos`, `clientes`, `reportes`.

## 4. Lo que la plantilla te da

- `${ctx}`: la ruta de la aplicación para tus enlaces (`${ctx}/menu`).
- `${usuario}`: el usuario con sesión (`usuario.nombreCompleto`, `usuario.rol`,
  `usuario.idCliente`, `usuario.cliente`, `usuario.administrador`, `usuario.cocina`).
- En Java: `Usuario u = Rutas.usuarioActual(request);` → `u.getIdCliente()` para guardar pedidos.

## 5. Clases de estilo listas (no escribas colores sueltos)

| Necesitas | Clase |
| --- | --- |
| Botón principal (una por pantalla) | `btn btn-sol` |
| Botón secundario | `btn btn-azul` o `btn btn-borde` |
| Desactivar / eliminar | `btn btn-peligro btn-chico` |
| Tarjeta blanca | `tarjeta` |
| Rejilla de tarjetas | `grid-tarjetas` + `platillo`, `platillo-foto`, `platillo-cuerpo`, `precio` |
| Formulario | `formulario` + `campo` (label que envuelve el input), `fila-2`, `casilla` |
| Tabla | `tabla-caja` > `tabla` |
| Mensajes | `alerta alerta-exito`, `alerta alerta-error`, `alerta alerta-info` |
| Estado del pedido | `badge badge-${pedido.estado}` (RECIBIDO, EN_PREPARACION, LISTO, EN_CAMINO, ENTREGADO, CANCELADO) |

Estilos propios de tu módulo: crea `resources/css/menu.css`, `pedidos.css` o `gerencia.css`
y agrégalo en tu JSP después del encabezado. **No edites `estilos.css` ni la plantilla**: pídeselo a Dev 1.

## 6. Archivos compartidos

- `model/Producto` y `dao/ProductoDAO`: los creó Dev 1 para el inicio; **Dev 2 es el dueño** y les
  agrega lo que falte. Dev 3 y Dev 4 los reutilizan para leer productos.
- `dao/DAOException`: úsala en tus DAO para envolver `SQLException`.
- `util/ConexionBD.obtenerConexion()`: la única forma de conectarse. Nunca escribas usuario o clave en el código.
- Clase `Cliente` y `ClienteDAO`: los crea Dev 2.
