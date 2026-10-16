package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.model.Producto;
import sv.edu.itca.sadu.service.ServicioProducto;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminProductoServlet", urlPatterns = {"/admin/productos"})
public class AdminProductoServlet extends HttpServlet {

    private ServicioProducto servicioProducto;

    @Override
    public void init() throws ServletException {
        servicioProducto = new ServicioProducto();
    }

   @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        try {
            switch (accion) {
                case "nuevo":
                    // Muestra el formulario vacío para crear
                    request.getRequestDispatcher("/WEB-INF/views/admin/form_producto.jsp").forward(request, response);
                    break;
                case "editar":
                    // Busca el producto y muestra el formulario lleno para editar
                    Long idEditar = Long.parseLong(request.getParameter("id"));
                    Producto p = servicioProducto.obtenerProductoPorId(idEditar);
                    request.setAttribute("producto", p);
                    request.getRequestDispatcher("/WEB-INF/views/admin/form_producto.jsp").forward(request, response);
                    break;
                case "eliminar":
                    // Elimina (desactiva) y recarga la tabla con mensaje de éxito 1
                    Long idEliminar = Long.parseLong(request.getParameter("id"));
                    servicioProducto.eliminarProducto(idEliminar);
                    response.sendRedirect(request.getContextPath() + "/admin/productos?exito=1");
                    break;
                    
                // Reactiva la cuenta
                case "activar":
                    // Activa y recarga la tabla con mensaje de éxito 2
                    Long idActivar = Long.parseLong(request.getParameter("id"));
                    servicioProducto.activarProducto(idActivar);
                    response.sendRedirect(request.getContextPath() + "/admin/productos?exito=2");
                    break;

                default:
                    // Configuración de la paginación
                    int limite = 15;
                    int paginaActual = 1;

                    // Si la URL trae el parámetro "pagina", lo leemos
                    String paginaParam = request.getParameter("pagina");
                    if (paginaParam != null && !paginaParam.isEmpty()) {
                        paginaActual = Integer.parseInt(paginaParam);
                    }

                    // Calculamos desde qué registro empezar (ej. Pag 1 = 0, Pag 2 = 15)
                    int offset = (paginaActual - 1) * limite;

                    // Obtenemos los datos de la base de datos
                    List<Producto> lista = servicioProducto.obtenerProductosPaginados(limite, offset);
                    int totalProductos = servicioProducto.contarTotalProductos();

                    // Calculamos cuántas páginas habrá en total
                    int totalPaginas = (int) Math.ceil((double) totalProductos / limite);

                    // Enviamos todo a la vista
                    request.setAttribute("productos", lista);
                    request.setAttribute("paginaActual", paginaActual);
                    request.setAttribute("totalPaginas", totalPaginas);

                    request.getRequestDispatcher("/WEB-INF/views/admin/productos.jsp").forward(request, response);
                    break;
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error procesando la solicitud: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/productos.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("idProducto");

        try {
            Producto p = new Producto();
            p.setNombre(request.getParameter("nombre"));
            p.setDescripcion(request.getParameter("descripcion"));
            p.setIdCategoria(Long.parseLong(request.getParameter("idCategoria")));
            p.setPrecio(new BigDecimal(request.getParameter("precio")));
            p.setImagenUrl(request.getParameter("imagenUrl"));
            p.setDestacado(request.getParameter("destacado") != null);
            p.setDisponible(request.getParameter("disponible") != null);

            if (idParam == null || idParam.isEmpty()) {
                servicioProducto.registrarProducto(p); // Nuevo
            } else {
                p.setIdProducto(Long.parseLong(idParam));
                servicioProducto.actualizarProducto(p); // Edición
            }

            // Redirige al listado principal del admin tras guardar
            response.sendRedirect(request.getContextPath() + "/admin/productos");

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            // Si hay error, regresamos los datos al formulario para no perder lo escrito
            request.getRequestDispatcher("/WEB-INF/views/admin/form_producto.jsp").forward(request, response);
        }
    }
}
