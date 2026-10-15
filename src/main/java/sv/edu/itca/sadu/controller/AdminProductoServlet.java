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
        if (accion == null) accion = "listar";

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
                    // Elimina y recarga la tabla
                    Long idEliminar = Long.parseLong(request.getParameter("id"));
                    servicioProducto.eliminarProducto(idEliminar);
                    response.sendRedirect(request.getContextPath() + "/admin/productos");
                    break;
                default:
                    // Lista todos los productos en la tabla del dashboard
                    List<Producto> lista = servicioProducto.obtenerTodosLosProductos();
                    request.setAttribute("productos", lista);
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