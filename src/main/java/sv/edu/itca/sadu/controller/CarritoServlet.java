package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.dao.ProductoDAO;
import sv.edu.itca.sadu.dao.ProductoDAOImpl;
import sv.edu.itca.sadu.model.Carrito;
import sv.edu.itca.sadu.model.Cliente;
import sv.edu.itca.sadu.model.ItemCarrito;
import sv.edu.itca.sadu.model.Producto;
import sv.edu.itca.sadu.model.TipoEntrega;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "CarritoServlet", urlPatterns = {"/carrito"})
public class CarritoServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/views/carrito.jsp";
    private static final String CARRITO = "carrito";

    private ProductoDAO productoDAO;

    @Override
    public void init() throws ServletException {
        productoDAO = new ProductoDAOImpl();
    }

    /** Devuelve el carrito de la sesión; si no existe (o es de una versión vieja), crea uno nuevo. */
    private Carrito obtenerCarrito(HttpSession session) {
        Object guardado = session.getAttribute(CARRITO);
        if (guardado instanceof Carrito) {
            return (Carrito) guardado;
        }
        Carrito nuevo = new Carrito();
        session.setAttribute(CARRITO, nuevo);
        return nuevo;
    }

    private boolean haySesionCliente(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("cliente") instanceof Cliente;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!haySesionCliente(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        obtenerCarrito(request.getSession());
        request.setAttribute("tiposEntrega", TipoEntrega.values());
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!haySesionCliente(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Carrito carrito = obtenerCarrito(request.getSession());
        String accion = request.getParameter("accion");

        try {
            if ("agregar".equals(accion)) {

                Long idProducto = Long.parseLong(request.getParameter("idProducto"));

                // La cantidad es opcional (el menú puede mandar solo el id)
                int cantidad = 1;
                String cantidadTexto = request.getParameter("cantidad");
                if (cantidadTexto != null && !cantidadTexto.trim().isEmpty()) {
                    cantidad = Integer.parseInt(cantidadTexto.trim());
                }
                if (cantidad <= 0) {
                    throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
                }

                // Nombre y precio salen de la BD, nunca del formulario
                Producto p = productoDAO.obtenerPorId(idProducto);
                if (p == null || !p.isDisponible()) {
                    throw new IllegalArgumentException("Ese platillo no está disponible.");
                }

                carrito.agregar(new ItemCarrito(p.getIdProducto(), p.getNombre(), p.getPrecio(), cantidad));

            } else if ("sumar".equals(accion)) {
                carrito.sumar(Long.parseLong(request.getParameter("idProducto")));

            } else if ("restar".equals(accion)) {
                carrito.restar(Long.parseLong(request.getParameter("idProducto")));

            } else if ("eliminar".equals(accion)) {
                carrito.quitar(Long.parseLong(request.getParameter("idProducto")));

            } else if ("vaciar".equals(accion)) {
                carrito.vaciar();

            } else {
                throw new IllegalArgumentException("La acción solicitada no es válida.");
            }

            response.sendRedirect(request.getContextPath() + "/carrito");

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Los datos enviados no son válidos.");

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("tiposEntrega", TipoEntrega.values());
            request.getRequestDispatcher(VISTA).forward(request, response);
        }
    }
}