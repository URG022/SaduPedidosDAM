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
        if (accion == null) {
            accion = "listar";
        }

        try {
            switch (accion) {
                case "editar":
                    Long idEditar = Long.parseLong(request.getParameter("id"));
                    Cliente cEditar = servicioCliente.obtenerPerfil(idEditar);
                    request.setAttribute("cliente", cEditar);
                    request.getRequestDispatcher("/WEB-INF/views/admin/form_cliente.jsp").forward(request, response);
                    break;
                case "desactivar":
                    Long idDesactivar = Long.parseLong(request.getParameter("id"));
                    servicioCliente.desactivarCuenta(idDesactivar);
                    response.sendRedirect(request.getContextPath() + "/admin/clientes?exito=1");
                    break;
                case "activar":
                    Long idActivar = Long.parseLong(request.getParameter("id"));
                    servicioCliente.activarCuenta(idActivar);
                    response.sendRedirect(request.getContextPath() + "/admin/clientes?exito=2");
                    break;
                default:
                    List<Cliente> lista = servicioCliente.obtenerTodosLosClientes();
                    request.setAttribute("clientes", lista);
                    request.getRequestDispatcher("/WEB-INF/views/admin/clientes.jsp").forward(request, response);
                    break;
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error procesando la solicitud: " + e.getMessage());
            List<Cliente> lista = servicioCliente.obtenerTodosLosClientes();
            request.setAttribute("clientes", lista);
            request.getRequestDispatcher("/WEB-INF/views/admin/clientes.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        try {
            Cliente c = new Cliente();
            c.setIdUsuario(Long.parseLong(request.getParameter("idUsuario")));
            c.setNombreCompleto(request.getParameter("nombreCompleto"));
            c.setCorreo(request.getParameter("correo"));
            c.setTelefono(request.getParameter("telefono"));
            c.setDireccion(request.getParameter("direccion"));
            boolean exito = servicioCliente.actualizarPerfil(c);

            if (exito) {
                response.sendRedirect(request.getContextPath() + "/admin/clientes?exito=3");
            } else {
                request.setAttribute("error", "No se pudo actualizar la información del cliente.");
                request.setAttribute("cliente", c);
                request.getRequestDispatcher("/WEB-INF/views/admin/form_cliente.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar los datos: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/form_cliente.jsp").forward(request, response);
        }
    }
}