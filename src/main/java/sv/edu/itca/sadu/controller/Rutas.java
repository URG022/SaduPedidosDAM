package sv.edu.itca.sadu.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import sv.edu.itca.sadu.model.Usuario;

/** Utilidades de navegacion compartidas por los Servlets y filtros. */
public final class Rutas {

    private Rutas() {
    }

    public static Usuario usuarioActual(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        return s == null ? null : (Usuario) s.getAttribute(LoginServlet.SESION_USUARIO);
    }

    /** A donde va cada rol despues de iniciar sesion. */
    public static String destinoSegunRol(HttpServletRequest request, Usuario u) {
        String ctx = request.getContextPath();
        if (u.isCliente()) {
            return ctx + "/inicio";
        }
        return ctx + "/panel";   // administrador y cocina
    }

    /** Evita redirecciones a otros sitios: solo rutas de esta aplicacion. */
    public static boolean esRutaInternaSegura(HttpServletRequest request, String ruta) {
        String ctx = request.getContextPath() + "/";
        return ruta != null && ruta.startsWith(ctx) && !ruta.startsWith("//") && !ruta.contains("://")
                && !ruta.contains("\\") && !ruta.contains("\r") && !ruta.contains("\n");
    }
}
