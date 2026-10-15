/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

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

@WebServlet(name = "ProductoServlet", urlPatterns = {"/cliente/productos"})
public class ProductoServlet extends HttpServlet {

    private ServicioProducto servicioProducto;

    @Override
    public void init() throws ServletException {
        // Inicializamos el servicio al arrancar el Servlet
        servicioProducto = new ServicioProducto();
    }

    // doGet maneja la lectura de datos (Listar y Eliminar/Desactivar)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        try {
            if (accion == null || accion.isEmpty() || accion.equals("listar")) {
                List<Producto> lista = servicioProducto.obtenerTodosLosProductos();
                request.setAttribute("productos", lista);
                request.getRequestDispatcher("/WEB-INF/views/cliente/productos.jsp").forward(request, response);
            } else if (accion.equals("eliminar")) {
                Long id = Long.parseLong(request.getParameter("id"));
                servicioProducto.eliminarProducto(id);
                response.sendRedirect("productos");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Ocurrió un error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/cliente/productos.jsp").forward(request, response);
        }
    }

    // doPost maneja la escritura de datos (Insertar y Actualizar)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String idParam = request.getParameter("idProducto");
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        String idCategoriaParam = request.getParameter("idCategoria");
        String precioParam = request.getParameter("precio");
        String imagenUrl = request.getParameter("imagenUrl");
        String destacadoParam = request.getParameter("destacado");

        try {
            Producto p = new Producto();
            p.setNombre(nombre);
            p.setDescripcion(descripcion);
            p.setIdCategoria(Long.parseLong(idCategoriaParam));
            p.setPrecio(new BigDecimal(precioParam));
            p.setImagenUrl(imagenUrl);
            p.setDestacado(destacadoParam != null); // Si el checkbox está marcado, no es nulo
            p.setDisponible(true);

            if (idParam == null || idParam.isEmpty()) {
                // Si no hay ID, es un producto nuevo
                servicioProducto.registrarProducto(p);
            } else {
                // Si hay ID, estamos editando un producto existente
                p.setIdProducto(Long.parseLong(idParam));
                servicioProducto.actualizarProducto(p);
            }
            
            // Recargamos la lista
            response.sendRedirect("productos");
            
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/cliente/productos.jsp").forward(request, response);
        }
    }
}