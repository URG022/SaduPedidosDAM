package sv.edu.itca.sadu.controller;

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

@WebServlet(name = "AdminPedidosHistorialServlet", urlPatterns = {"/admin/historial-pedidos"})
public class AdminPedidosHistorialServlet extends HttpServlet {

    private PedidoService pedidoService;

    @Override
    public void init() throws ServletException {
        super.init();
        pedidoService = new PedidoService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioAutenticado") : null;
        
        if (usuario == null || !usuario.isAdministrador()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado.");
            return;
        }

        String accion = request.getParameter("accion");

        try {
            if ("detalle".equals(accion)) {
                Long idPedido = Long.parseLong(request.getParameter("id"));
                Pedido pedido = pedidoService.obtenerDetalleParaAdmin(idPedido);
                if (pedido == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/historial-pedidos");
                    return;
                }

                request.setAttribute("pedido", pedido);
                request.setAttribute("detalles", pedidoService.obtenerDetalles(idPedido));
                request.setAttribute("seguimiento", pedidoService.obtenerSeguimiento(idPedido));
                request.setAttribute("comprobante", pedidoService.obtenerComprobante(idPedido));

                request.getRequestDispatcher("/WEB-INF/views/admin/detalle-pedido.jsp").forward(request, response);
                return;
            }

            // --- CAPTURA DE FILTROS ---
            String busqueda = request.getParameter("busqueda");
            String fechaInicio = request.getParameter("fechaInicio");
            String fechaFin = request.getParameter("fechaFin");

            // --- LÓGICA DE PAGINACIÓN ---
            int pagina = 1;
            int registrosPorPagina = 10; // 10 registros por página

            String paginaStr = request.getParameter("pagina");
            if (paginaStr != null && !paginaStr.isEmpty()) {
                try {
                    pagina = Integer.parseInt(paginaStr);
                    if (pagina < 1) pagina = 1;
                } catch (NumberFormatException e) {
                    pagina = 1;
                }
            }

            int totalRegistros = pedidoService.contarPedidosEntregadosFiltrados(busqueda, fechaInicio, fechaFin);
            int totalPaginas = (int) Math.ceil((double) totalRegistros / registrosPorPagina);
            if (totalPaginas == 0) totalPaginas = 1;
            if (pagina > totalPaginas) pagina = totalPaginas;

            List<Pedido> historialEntregados = pedidoService.listarPedidosEntregadosFiltradosPaginados(busqueda, fechaInicio, fechaFin, pagina, registrosPorPagina);

            request.setAttribute("historialEntregados", historialEntregados);
            request.setAttribute("paginaActual", pagina);
            request.setAttribute("totalPaginas", totalPaginas);
            
            // Devolver valores al formulario para mantenerlos al recargar/paginar
            request.setAttribute("busqueda", busqueda);
            request.setAttribute("fechaInicio", fechaInicio);
            request.setAttribute("fechaFin", fechaFin);

            request.getRequestDispatcher("/WEB-INF/views/admin/historial-pedidos.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/historial-pedidos");
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // En caso de recibir un POST por error (ej. al recargar un formulario), lo redirigimos al GET
        doGet(request, response);
    }
}