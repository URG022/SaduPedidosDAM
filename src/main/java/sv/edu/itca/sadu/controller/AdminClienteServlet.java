package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.model.Cliente;
import sv.edu.itca.sadu.service.ServicioCliente;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminClienteServlet", urlPatterns = {"/admin/clientes"})
public class AdminClienteServlet extends HttpServlet {

    private ServicioCliente servicioCliente;

    @Override
    public void init() throws ServletException {
        servicioCliente = new ServicioCliente();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");

        try {
            if ("desactivar".equals(accion)) {
                // Acción: Desactivar cuenta
                Long idDesactivar = Long.parseLong(request.getParameter("id"));
                servicioCliente.desactivarCuenta(idDesactivar);
                response.sendRedirect(request.getContextPath() + "/admin/clientes?exito=1");
                return;
            }
            
            if ("activar".equals(accion)) {
                // Acción: Reactivar cuenta
                Long idActivar = Long.parseLong(request.getParameter("id"));
                servicioCliente.activarCuenta(idActivar);
                response.sendRedirect(request.getContextPath() + "/admin/clientes?exito=2");
                return;
            }

            // Acción por defecto: Listar todos los clientes
            List<Cliente> lista = servicioCliente.obtenerTodosLosClientes();
            request.setAttribute("clientes", lista);
            request.getRequestDispatcher("/WEB-INF/views/admin/clientes.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Error procesando la solicitud: " + e.getMessage());
            
            // Recargar la lista en caso de error
            List<Cliente> lista = servicioCliente.obtenerTodosLosClientes();
            request.setAttribute("clientes", lista);
            request.getRequestDispatcher("/WEB-INF/views/admin/clientes.jsp").forward(request, response);
        }
    }
}