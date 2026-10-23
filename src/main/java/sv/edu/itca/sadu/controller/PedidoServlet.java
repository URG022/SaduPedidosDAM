package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.dao.PedidoDAO;
import sv.edu.itca.sadu.dao.PedidoDAOImpl;
import sv.edu.itca.sadu.model.Cliente;
import sv.edu.itca.sadu.model.DetallePedido;
import sv.edu.itca.sadu.model.EstadoPedido;
import sv.edu.itca.sadu.model.ItemCarrito;
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
        PedidoDAO pedidoDAO = new PedidoDAOImpl();
        pedidoService = new PedidoService(pedidoDAO);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("cliente") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        String accion = request.getParameter("accion");

        try {
            if ("detalle".equals(accion)) {
                Long idPedido = Long.parseLong(
                        request.getParameter("id"));

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

            // Aquí puedes agregar el listado de pedidos del cliente
            // cuando el DAO tenga una consulta filtrada por id_cliente.
            request.getRequestDispatcher(
                    "/WEB-INF/views/pedidos/lista.jsp")
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

        HttpSession session = request.getSession(false);

        if (session == null
                || !(session.getAttribute("cliente") instanceof Cliente)) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        Cliente cliente = (Cliente) session.getAttribute("cliente");
        String accion = request.getParameter("accion");

        if (!"confirmar".equals(accion)) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "La acción solicitada no es válida.");
            return;
        }

        List<ItemCarrito> carrito =
                (List<ItemCarrito>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            response.sendRedirect(
                    request.getContextPath() + "/carrito?error=vacio");
            return;
        }

        try {
            Pedido pedido = new Pedido();

            pedido.setIdCliente(cliente.getIdCliente());
            pedido.setTipoEntrega(
                    request.getParameter("tipoEntrega"));
            pedido.setDireccionEntrega(
                    request.getParameter("direccionEntrega"));
            pedido.setNotas(request.getParameter("notas"));

            List<DetallePedido> detalles = new ArrayList<>();

            for (ItemCarrito item : carrito) {
                DetallePedido detalle = new DetallePedido();

                detalle.setIdProducto(item.getIdProducto());
                detalle.setCantidad(item.getCantidad());

                // Solo para integrar el modelo actual.
                // PedidoService debe obtener el precio confiable
                // desde la base de datos antes de confirmar.
                detalle.setPrecioUnitario(item.getPrecio());

                detalles.add(detalle);
            }

            Long idPedido = pedidoService.crearPedido(
                    pedido,
                    detalles,
                    cliente.getIdUsuario()
            );

            session.removeAttribute("carrito");

            response.sendRedirect(
                    request.getContextPath()
                    + "/pedidos?accion=detalle&id=" + idPedido
                    + "&exito=1");

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
