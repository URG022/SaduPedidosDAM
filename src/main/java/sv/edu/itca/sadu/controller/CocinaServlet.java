package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.dao.PedidoDAO;
import sv.edu.itca.sadu.dao.PedidoDAOImpl;
import sv.edu.itca.sadu.model.EstadoPedido;
import sv.edu.itca.sadu.model.Pedido;
import sv.edu.itca.sadu.model.Usuario;
import sv.edu.itca.sadu.service.PedidoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CocinaServlet", urlPatterns = {"/cocina", "/cocina/tablero"})
public class CocinaServlet extends HttpServlet {

    private PedidoService pedidoService;

    @Override
    public void init() throws ServletException {
        pedidoService = new PedidoService();
    }

    /**
     * Usuario con sesión iniciada, o null si no hay.
     */
    private Usuario obtenerUsuario(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuarioAutenticado") instanceof Usuario) {
            return (Usuario) session.getAttribute("usuarioAutenticado");
        }
        return null;
    }

    private boolean tienePermiso(Usuario usuario) {
        String rol = usuario.getRol();
        return "ADMINISTRADOR".equals(rol) || "COCINA".equals(rol);
    }

    private EstadoPedido parsearEstado(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Debes indicar el estado.");
        }
        try {
            return EstadoPedido.valueOf(texto.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El estado indicado no es válido.");
        }
    }

    /**
     * Carga el tablero. filtro: vacío = pedidos activos, TODOS = historial
     * completo, o el nombre de un estado.
     */
    private void cargarTablero(HttpServletRequest request, HttpServletResponse response, String filtro)
            throws ServletException, IOException {

        List<Pedido> pedidos;
        EstadoPedido estadoFiltro = null;

        if (filtro == null || filtro.trim().isEmpty()) {
            // Carga todos los pedidos activos para distribuirlos en las columnas del Kanban
            pedidos = pedidoService.obtenerTodosLosPedidos();
        } else if ("TODOS".equalsIgnoreCase(filtro.trim())) {
            pedidos = pedidoService.obtenerTodosLosPedidos();
        } else {
            estadoFiltro = parsearEstado(filtro);
            pedidos = pedidoService.obtenerPedidosPorEstado(estadoFiltro);
        }

        // Cocina necesita ver los platillos de cada pedido
        for (Pedido p : pedidos) {
            p.setDetalles(pedidoService.obtenerDetalles(p.getIdPedido()));
        }

        request.setAttribute("pedidos", pedidos);
        request.setAttribute("estados", EstadoPedido.values());
        request.setAttribute("estadoFiltro", estadoFiltro);

        request.getRequestDispatcher("/WEB-INF/views/cocina/tablero.jsp")
                .forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = obtenerUsuario(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!tienePermiso(usuario)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para acceder a cocina.");
            return;
        }

        try {
            cargarTablero(request, response, request.getParameter("estado"));
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Usuario usuario = obtenerUsuario(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!tienePermiso(usuario)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para modificar pedidos.");
            return;
        }

        try {
            Long idPedido = Long.parseLong(request.getParameter("idPedido"));
            EstadoPedido nuevoEstado = parsearEstado(request.getParameter("estado"));
            String comentario = request.getParameter("comentario");
            String metodoPago = request.getParameter("metodoPago");

            if (nuevoEstado == EstadoPedido.ENTREGADO && !usuario.isCocina()) {
                request.setAttribute("error", "Solo el personal de cocina puede entregar y cobrar un pedido.");
                cargarTablero(request, response, null);
                return;
            }

            boolean actualizado = pedidoService.cambiarEstado(
                    idPedido, nuevoEstado, comentario, usuario.getIdUsuario(), metodoPago);

            if (!actualizado) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No se encontró el pedido.");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/cocina?exito=1");

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El identificador del pedido no es válido.");

        } catch (IllegalArgumentException e) {
            // Regla de negocio (transición no permitida, comentario largo...): se muestra en el tablero
            request.setAttribute("error", e.getMessage());
            cargarTablero(request, response, null);

        } catch (Exception e) {
            throw new ServletException("No se pudo actualizar el pedido.", e);
        }
    }
}
