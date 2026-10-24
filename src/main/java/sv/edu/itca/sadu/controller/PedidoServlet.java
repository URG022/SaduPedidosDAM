package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.dao.PedidoDAO;
import sv.edu.itca.sadu.dao.PedidoDAOImpl;
import sv.edu.itca.sadu.model.*;
import sv.edu.itca.sadu.model.Pedido;
import sv.edu.itca.sadu.service.PedidoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "PedidoServlet", urlPatterns = {"/pedidos"})
public class PedidoServlet extends HttpServlet {

    private PedidoService pedidoService;

    @Override
    public void init() throws ServletException {
        pedidoService = new PedidoService();
    }

    /**
     * Cliente con sesión iniciada, o null si no hay.
     */
    private Cliente obtenerCliente(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("cliente") instanceof Cliente) {
            return (Cliente) session.getAttribute("cliente");
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Cliente cliente = obtenerCliente(request);
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String accion = request.getParameter("accion");

        try {
            if ("detalle".equals(accion)) {
                Long idPedido = Long.parseLong(
                        request.getParameter("id"));

                // Devuelve null si no existe o si el pedido es de otro cliente
                Pedido pedido = pedidoService.obtenerDetalleDeCliente(idPedido, cliente.getIdUsuario());
                if (pedido == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "No encontramos ese pedido.");
                    return;
                }

                request.setAttribute("pedido",
                        pedidoService.obtenerPedido(idPedido));

                request.setAttribute("detalles",
                        pedidoService.obtenerDetalles(idPedido));

                request.setAttribute("seguimiento",
                        pedidoService.obtenerSeguimiento(idPedido));

                request.getRequestDispatcher(
                        "/WEB-INF/views/pedidos/detalle.jsp")
                        .forward(request, response);
                return;
            }

            // P5 - Mis pedidos
            request.setAttribute("pedidos", pedidoService.listarMisPedidos(cliente.getIdUsuario()));
            request.getRequestDispatcher("/WEB-INF/views/pedidos/lista.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El identificador del pedido no es válido.");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Cliente cliente = obtenerCliente(request);
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!"confirmar".equals(request.getParameter("accion"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "La acción solicitada no es válida.");
            return;
        }

        HttpSession session = request.getSession(false);
        Object guardado = session.getAttribute("carrito");

        if (!(guardado instanceof Carrito) || ((Carrito) guardado).isVacio()) {
            response.sendRedirect(request.getContextPath() + "/carrito?error=vacio");
            return;
        }

        Carrito carrito = (Carrito) guardado;

        try {
            Long idPedido = pedidoService.crearPedido(
                    carrito,
                    cliente.getIdUsuario(),
                    request.getParameter("tipoEntrega"),
                    request.getParameter("direccionEntrega"),
                    request.getParameter("notas"));

            // Solo se vacía si el pedido ya quedó guardado
            session.removeAttribute("carrito");

            response.sendRedirect(request.getContextPath()
                    + "/pedidos?accion=detalle&id=" + idPedido + "&exito=1");
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher(
                    "/WEB-INF/views/carrito.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(
                    "No se pudo procesar el pedido.", e);
        }
    }
}
