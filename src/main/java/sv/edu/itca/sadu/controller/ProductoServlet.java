package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.model.Producto;
import sv.edu.itca.sadu.service.ServicioProducto;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductoServlet", urlPatterns = {"/cliente/productos"})
public class ProductoServlet extends HttpServlet {

    private ServicioProducto servicioProducto;

    @Override
    public void init() throws ServletException {
        servicioProducto = new ServicioProducto();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Producto> lista = servicioProducto.obtenerTodosLosProductos();
            request.setAttribute("productos", lista);
        } catch (Exception e) {
            request.setAttribute("error", "No se pudo cargar el menú. Intenta de nuevo.");
        }
        request.getRequestDispatcher("/WEB-INF/views/cliente/productos.jsp").forward(request, response);
    }
}