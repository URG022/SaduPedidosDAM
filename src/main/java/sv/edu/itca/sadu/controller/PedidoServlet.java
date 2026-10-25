package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.model.Carrito;
import sv.edu.itca.sadu.model.Pedido;
import sv.edu.itca.sadu.model.TipoEntrega;
import sv.edu.itca.sadu.model.Usuario;
import sv.edu.itca.sadu.service.PedidoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "PedidoServlet", urlPatterns = {"/cliente/pedidos", "/cliente/pedidos/lista"})
public class PedidoServlet extends HttpServlet {

    private PedidoService pedidoService;

    @Override
    public void init() throws ServletException {
        pedidoService = new PedidoService();
    }

    /**
     * Usuario con sesión iniciada y rol CLIENTE, o null.
     */
    private Usuario obtenerCliente(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuarioAutenticado") instanceof Usuario) {
            Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
            if ("CLIENTE".equals(usuario.getRol())) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = obtenerCliente(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String accion = request.getParameter("accion");

        try {
            // P6 - Detalle del pedido
            if ("detalle".equals(accion)) {
                Long idPedido = Long.parseLong(request.getParameter("id"));

                // Devuelve null si no existe o si el pedido es de otro cliente
                Pedido pedido = pedidoService.obtenerDetalleDeCliente(idPedido, usuario.getIdUsuario());
                if (pedido == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "No encontramos ese pedido.");
                    return;
                }

                request.setAttribute("pedido", pedido);
                request.setAttribute("detalles", pedido.getDetalles());
                request.setAttribute("seguimiento", pedido.getSeguimientos());

                // Cuando Dev 4 entregue ComprobanteDAO, agrega aquí:
                request.setAttribute("comprobante", pedidoService.obtenerComprobante(idPedido));
                request.getRequestDispatcher("/WEB-INF/views/cliente/pedidos/detalle.jsp")
                        .forward(request, response);
                return;
            }

            // P5 - Mis pedidos
            request.setAttribute("pedidos", pedidoService.listarMisPedidos(usuario.getIdUsuario()));
            request.getRequestDispatcher("/WEB-INF/views/cliente/pedidos/lista.jsp")
                    .forward(request, response);

        } catch (IllegalArgumentException e) {
            // Incluye NumberFormatException (id ausente o no numérico)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El identificador del pedido no es válido.");
        }
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
                    usuario.getIdUsuario(),
                    request.getParameter("tipoEntrega"),
                    request.getParameter("direccionEntrega"),
                    request.getParameter("notas"));

            // Solo se vacía si el pedido ya quedó guardado
            session.removeAttribute("carrito");

            response.sendRedirect(request.getContextPath()
                    + "/cliente/pedidos?accion=detalle&id=" + idPedido + "&exito=1");

        } catch (IllegalArgumentException e) {
            // Mensaje de validación: se muestra en el carrito sin perder lo escrito
            request.setAttribute("error", e.getMessage());
            request.setAttribute("tiposEntrega", TipoEntrega.values());
            request.setAttribute("direccionPerfil", pedidoService.obtenerDireccionPerfil(usuario.getIdUsuario()));
            request.getRequestDispatcher("/WEB-INF/views/cliente/carrito.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("No se pudo procesar el pedido.", e);
        }
    }
}
