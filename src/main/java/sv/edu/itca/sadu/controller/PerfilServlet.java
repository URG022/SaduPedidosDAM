package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.model.Cliente;
import sv.edu.itca.sadu.model.Usuario;
import sv.edu.itca.sadu.service.ServicioCliente;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Mi perfil del cliente: ver y editar sus datos, cambiar contraseña y desactivar su cuenta.
 * La ruta /cliente/* ya está protegida por AccesoFilter (solo rol CLIENTE).
 */
@WebServlet(name = "PerfilServlet", urlPatterns = {"/cliente/perfil"})
public class PerfilServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/views/cliente/perfil.jsp";

    private ServicioCliente servicioCliente;

    @Override
    public void init() throws ServletException {
        servicioCliente = new ServicioCliente();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession();
        Usuario usuarioActual = (Usuario) sesion.getAttribute("usuarioAutenticado");
        if (usuarioActual == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if ("desactivar".equals(request.getParameter("accion"))) {
            servicioCliente.desactivarCuenta(usuarioActual.getIdUsuario());
            sesion.invalidate();
            response.sendRedirect(request.getContextPath() + "/inicio?mensaje=cuenta_desactivada");
            return;
        }

        Cliente perfil = servicioCliente.obtenerPerfil(usuarioActual.getIdUsuario());
        if (perfil == null) {
            throw new ServletException("No se pudo cargar el perfil del cliente.");
        }
        request.setAttribute("perfil", perfil);
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession sesion = request.getSession();
        Usuario usuarioActual = (Usuario) sesion.getAttribute("usuarioAutenticado");
        if (usuarioActual == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Cliente c = new Cliente();
        c.setIdUsuario(usuarioActual.getIdUsuario());
        c.setNombreCompleto(request.getParameter("nombreCompleto"));
        c.setCorreo(request.getParameter("correo"));
        c.setTelefono(request.getParameter("telefono"));
        c.setDireccion(request.getParameter("direccion"));

        String claveActual = request.getParameter("claveActual");
        String nuevaClave = request.getParameter("nuevaClave");
        String confirmarClave = request.getParameter("confirmarClave");

        // 1. Se valida TODO antes de guardar: si algo está mal, no se guarda nada
        List<String> errores = servicioCliente.validarPerfil(c);
        errores.addAll(servicioCliente.validarCambioClave(
                usuarioActual.getIdUsuario(), claveActual, nuevaClave, confirmarClave));
        if (!errores.isEmpty()) {
            mostrarErrores(request, response, c, errores);
            return;
        }

        // 2. Datos personales (falla si el correo ya es de otra cuenta)
        if (!servicioCliente.actualizarPerfil(c)) {
            errores.add("No se pudo guardar: ese correo ya está registrado en otra cuenta.");
            mostrarErrores(request, response, c, errores);
            return;
        }

        // 3. Contraseña, solo si la quiso cambiar
        boolean cambioClave = nuevaClave != null && !nuevaClave.trim().isEmpty();
        if (cambioClave) {
            servicioCliente.cambiarClave(usuarioActual.getIdUsuario(), nuevaClave);
        }

        // 4. Se actualiza la sesión para que el saludo muestre el nombre nuevo
        usuarioActual.setNombreCompleto(c.getNombreCompleto());
        usuarioActual.setCorreo(c.getCorreo());
        sesion.setAttribute("usuarioAutenticado", usuarioActual);

        response.sendRedirect(request.getContextPath() + "/cliente/perfil?exito=" + (cambioClave ? "clave" : "1"));
    }

    private void mostrarErrores(HttpServletRequest request, HttpServletResponse response,
            Cliente c, List<String> errores) throws ServletException, IOException {
        request.setAttribute("errores", errores);
        request.setAttribute("perfil", c);   // se conserva lo que escribió
        request.getRequestDispatcher(VISTA).forward(request, response);
    }
}