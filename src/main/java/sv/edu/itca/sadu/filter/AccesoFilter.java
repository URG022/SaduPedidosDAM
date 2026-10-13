package sv.edu.itca.sadu.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sv.edu.itca.sadu.controller.Rutas;
import sv.edu.itca.sadu.model.Usuario;

/**
 * Protege las rutas privadas segun el rol:
 *   /cliente/*  -> CLIENTE
 *   /cocina/*   -> COCINA o ADMINISTRADOR
 *   /admin/*    -> ADMINISTRADOR
 *   /panel      -> COCINA o ADMINISTRADOR
 * Sin sesion: redirige a /login y despues regresa a la pagina pedida.
 * Rol incorrecto: error 403.
 */
@WebFilter(filterName = "AccesoFilter", urlPatterns = {"/cliente/*", "/cocina/*", "/admin/*", "/panel"})
public class AccesoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        Usuario u = Rutas.usuarioActual(request);
        if (u == null) {
            String pedida = request.getRequestURI()
                    + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
            response.sendRedirect(request.getContextPath() + "/login?volver="
                    + URLEncoder.encode(pedida, StandardCharsets.UTF_8));
            return;
        }

        String ruta = request.getServletPath();
        boolean permitido;
        if (ruta.startsWith("/cliente/")) {
            permitido = u.isCliente();
        } else if (ruta.startsWith("/admin/")) {
            permitido = u.isAdministrador();
        } else {   // /cocina/* y /panel
            permitido = u.isCocina() || u.isAdministrador();
        }

        if (!permitido) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        response.setHeader("Cache-Control", "no-store");   // no mostrar paginas privadas con "Atras"
        chain.doFilter(request, response);
    }
}
