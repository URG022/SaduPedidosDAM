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

@WebServlet(name = "CocinaServlet", urlPatterns = {"/cocina"})
public class CocinaServlet extends HttpServlet {

    private PedidoService pedidoService;

    @Override
    public void init() throws ServletException {
        PedidoDAO pedidoDAO = new PedidoDAOImpl();
        pedidoService = new PedidoService(pedidoDAO);
    }

    private boolean tienePermiso(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return false;
        }

        Object objeto = session.getAttribute("usuario");

        if (!(objeto instanceof Usuario)) {
            return false;
        }

        Usuario usuario = (Usuario) objeto;
        String rol = usuario.getRol();

        return "ADMIN".equals(rol) || "COCINA".equals(rol);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        if (!tienePermiso(request)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "No tienes permiso para acceder a cocina.");
            return;
        }

        try {
            String estadoParametro = request.getParameter("estado");
            List<Pedido> pedidos;

            if (estadoParametro == null
                    || estadoParametro.trim().isEmpty()) {

                pedidos = pedidoService.obtenerTodosLosPedidos();

            } else {
                EstadoPedido estado =
                        EstadoPedido.valueOf(estadoParametro);

                pedidos = pedidoService.obtenerPedidosPorEstado(estado);
            }

            request.setAttribute("pedidos", pedidos);
            request.setAttribute(
                    "estados", EstadoPedido.values());

            request.getRequestDispatcher(
                    "/WEB-INF/views/cocina/tablero.jsp")
                    .forward(request, response);

        } catch (IllegalArgumentException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El estado indicado no es válido.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!tienePermiso(request)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "No tienes permiso para modificar pedidos.");
            return;
        }

        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            Long idPedido = Long.parseLong(
                    request.getParameter("idPedido"));

            EstadoPedido nuevoEstado = EstadoPedido.valueOf(
                    request.getParameter("estado"));

            String comentario = request.getParameter("comentario");

            boolean actualizado = pedidoService.cambiarEstado(
                    idPedido,
                    nuevoEstado,
                    comentario,
                    usuario.getIdUsuario()
            );

            if (!actualizado) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "No se encontró el pedido.");
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + "/cocina?exito=1");

        } catch (Exception e) {
            throw new ServletException(
                    "No se pudo actualizar el pedido.", e);
        }
    }
}
