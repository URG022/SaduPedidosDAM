package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.model.Categoria;
import sv.edu.itca.sadu.model.Producto;
import sv.edu.itca.sadu.service.ServicioCategoria;
import sv.edu.itca.sadu.service.ServicioProducto;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * P3 - Menú del cliente: SOLO lectura.
 * Crear, editar y desactivar platillos se hace en /admin/productos (AdminProductoServlet).
 */
@WebServlet(name = "ProductoServlet", urlPatterns = {"/cliente/productos"})
public class ProductoServlet extends HttpServlet {

    private ServicioProducto servicioProducto;
    private ServicioCategoria servicioCategoria;

    @Override
    public void init() throws ServletException {
        servicioProducto = new ServicioProducto();
        servicioCategoria = new ServicioCategoria();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Producto> lista = servicioProducto.obtenerTodosLosProductos();
            request.setAttribute("productos", lista);

            // Solo categorías activas: sus botones de filtro y sus platillos son los que se muestran
            List<Categoria> categorias = servicioCategoria.listarActivas();
            request.setAttribute("categorias", categorias);
            request.setAttribute("nombresCategoria", servicioCategoria.mapaNombres(categorias));
        } catch (Exception e) {
            request.setAttribute("error", "No se pudo cargar el menú. Intenta de nuevo.");
        }
        request.getRequestDispatcher("/WEB-INF/views/cliente/productos.jsp").forward(request, response);
    }
}