package sv.edu.itca.sadu.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import sv.edu.itca.sadu.dao.DAOException;
import sv.edu.itca.sadu.dao.UsuarioDAOImpl;
import sv.edu.itca.sadu.model.Usuario;
import sv.edu.itca.sadu.service.ServicioRegistro;

/** Crear cuenta de cliente. */
@WebServlet(name = "RegistroServlet", urlPatterns = {"/registro"})
public class RegistroServlet extends HttpServlet {

    private final ServicioRegistro servicio = new ServicioRegistro(new UsuarioDAOImpl());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/acceso/registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
        String clave = request.getParameter("clave");
        String confirmacion = request.getParameter("confirmacion");

        try {
            List<String> errores = servicio.validar(nombre, correo, telefono, clave, confirmacion);
            if (!errores.isEmpty()) {
                request.setAttribute("errores", errores);
                request.getRequestDispatcher("/WEB-INF/views/acceso/registro.jsp").forward(request, response);
                return;
            }
            Usuario nuevo = servicio.registrar(nombre, correo, telefono, direccion, clave);

            // Queda con la sesion iniciada
            HttpSession anterior = request.getSession(false);
            if (anterior != null) {
                anterior.invalidate();
            }
            request.getSession(true).setAttribute(LoginServlet.SESION_USUARIO, nuevo);
            response.sendRedirect(request.getContextPath() + "/inicio?bienvenida=1");   // PRG
        } catch (DAOException e) {
            log("Error de base de datos en registro", e);
            request.setAttribute("errores",
                    List.of("No se pudo crear la cuenta. Revisa la conexión con la base de datos."));
            request.getRequestDispatcher("/WEB-INF/views/acceso/registro.jsp").forward(request, response);
        }
    }
}
