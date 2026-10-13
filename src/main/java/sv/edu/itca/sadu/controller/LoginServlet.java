package sv.edu.itca.sadu.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import sv.edu.itca.sadu.dao.DAOException;
import sv.edu.itca.sadu.dao.UsuarioDAOImpl;
import sv.edu.itca.sadu.model.Usuario;
import sv.edu.itca.sadu.service.ServicioAutenticacion;

/**Iniciar sesion (GET muestra el formulario, POST valida). */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    public static final String SESION_USUARIO = "usuarioAutenticado";
    private static final String COOKIE_CORREO = "saduCorreo";

    private final ServicioAutenticacion servicio = new ServicioAutenticacion(new UsuarioDAOImpl());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (Rutas.usuarioActual(request) != null) {
            response.sendRedirect(Rutas.destinoSegunRol(request, Rutas.usuarioActual(request)));
            return;
        }
        request.setAttribute("correo", leerCookie(request));
        request.setAttribute("recordar", leerCookie(request) != null);
        request.getRequestDispatcher("/WEB-INF/views/acceso/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String correo = valor(request, "correo");
        String clave = request.getParameter("clave");
        boolean recordar = request.getParameter("recordar") != null;
        String volver = request.getParameter("volver");

        Usuario usuario;
        try {
            usuario = servicio.autenticar(correo, clave);
        } catch (DAOException e) {
            log("Error de base de datos en login", e);
            mostrarError(request, response, correo, recordar, volver,
                    "No hay conexión con la base de datos. Revisa que MySQL esté encendido.");
            return;
        }

        if (usuario == null) {
            mostrarError(request, response, correo, recordar, volver, "Correo o contraseña incorrectos.");
            return;
        }

        // Sesion nueva para evitar fijacion de sesion
        HttpSession anterior = request.getSession(false);
        if (anterior != null) {
            anterior.invalidate();
        }
        HttpSession sesion = request.getSession(true);
        sesion.setAttribute(SESION_USUARIO, usuario);
        sesion.setMaxInactiveInterval(30 * 60);

        guardarCookie(request, response, recordar ? usuario.getCorreo() : null);

        String destino = Rutas.esRutaInternaSegura(request, volver)
                ? volver : Rutas.destinoSegunRol(request, usuario);
        response.sendRedirect(destino);
    }

    private void mostrarError(HttpServletRequest request, HttpServletResponse response, String correo,
            boolean recordar, String volver, String mensaje) throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        request.setAttribute("correo", correo);
        request.setAttribute("recordar", recordar);
        request.setAttribute("volver", volver);
        request.getRequestDispatcher("/WEB-INF/views/acceso/login.jsp").forward(request, response);
    }

    private String leerCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (COOKIE_CORREO.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    private void guardarCookie(HttpServletRequest request, HttpServletResponse response, String correo) {
        Cookie c = new Cookie(COOKIE_CORREO, correo == null ? "" : correo);
        c.setHttpOnly(true);
        c.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        c.setMaxAge(correo == null ? 0 : 60 * 60 * 24 * 30);   // 30 dias, o borrar
        response.addCookie(c);
    }

    private String valor(HttpServletRequest r, String nombre) {
        String v = r.getParameter(nombre);
        return v == null ? "" : v.trim();
    }
}
