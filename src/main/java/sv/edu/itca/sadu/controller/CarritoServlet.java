package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.dao.ProductoDAO;
import sv.edu.itca.sadu.dao.ProductoDAOImpl;
import sv.edu.itca.sadu.model.*;
import sv.edu.itca.sadu.service.PedidoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "CarritoServlet", urlPatterns = {"/cliente/carrito", "/carrito"})
public class CarritoServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/views/cliente/carrito.jsp";
    private static final String CARRITO = "carrito";

    private ProductoDAO productoDAO;
    private PedidoService pedidoService;

    @Override
    public void init() throws ServletException {
        productoDAO = new ProductoDAOImpl();
        pedidoService = new PedidoService();
    }

    /**
     * Usuario con sesión iniciada y rol CLIENTE, o null.
     */
    private Usuario obtenerCliente(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        HttpSession s = request.getSession(false);
        Object o = (s == null) ? null : s.getAttribute("usuarioAutenticado");
        System.out.println("[CARRITO] sesion=" + (s != null)
                + " | usuarioAutenticado=" + (o == null ? "null" : o.getClass().getName()));

        if (session != null && session.getAttribute("usuarioAutenticado") instanceof Usuario) {
            Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
            if ("CLIENTE".equals(usuario.getRol())) {
                return usuario;
            }
        }
        return null;
    }

    /**
     * Devuelve el carrito de la sesión; si no existe (o es de una versión
     * vieja), crea uno nuevo.
     */
    private Carrito obtenerCarrito(HttpSession session) {
        Object guardado = session.getAttribute(CARRITO);
        if (guardado instanceof Carrito) {
            return (Carrito) guardado;
        }
        Carrito nuevo = new Carrito();
        session.setAttribute(CARRITO, nuevo);
        return nuevo;
    }

    /**
     * Datos que necesita carrito.jsp además del carrito.
     */
    private void prepararVista(HttpServletRequest request, Usuario usuario) {
        request.setAttribute("tiposEntrega", TipoEntrega.values());
        request.setAttribute("direccionPerfil", pedidoService.obtenerDireccionPerfil(usuario.getIdUsuario()));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = obtenerCliente(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        obtenerCarrito(request.getSession());
        prepararVista(request, usuario);
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Usuario usuario = obtenerCliente(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Carrito carrito = obtenerCarrito(request.getSession());
        String accion = request.getParameter("accion");

        try {
            if ("agregar".equals(accion)) {

                Long idProducto = Long.parseLong(request.getParameter("idProducto"));

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
                
                // Si se agregó desde el menú, regresamos al menú con un aviso
                if ("menu".equals(request.getParameter("volver"))) {
                    request.getSession().setAttribute("avisoCarrito", p.getNombre());
                    response.sendRedirect(request.getContextPath() + "/cliente/productos#p-" + p.getIdProducto());
                    return;
                }

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
            prepararVista(request, usuario);
            request.getRequestDispatcher(VISTA).forward(request, response);
        }
    }
}
