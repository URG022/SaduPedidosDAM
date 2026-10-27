/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.model.Cliente;
import sv.edu.itca.sadu.model.Usuario;
import sv.edu.itca.sadu.service.ServicioCliente;
import sv.edu.itca.sadu.util.Seguridad;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "PerfilServlet", urlPatterns = {"/cliente/perfil"})
public class PerfilServlet extends HttpServlet {

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

        String accion = request.getParameter("accion");

        if ("desactivar".equals(accion)) {
            servicioCliente.desactivarCuenta(usuarioActual.getIdUsuario());
            sesion.invalidate();
            response.sendRedirect(request.getContextPath() + "/inicio?mensaje=cuenta_desactivada");
            return;
        }

        try {
            Cliente perfil = servicioCliente.obtenerPerfil(usuarioActual.getIdUsuario());
            request.setAttribute("perfil", perfil);
            request.getRequestDispatcher("/WEB-INF/views/cliente/perfil.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        try {
            Cliente c = new Cliente();
            c.setIdUsuario(usuarioActual.getIdUsuario());
            c.setNombreCompleto(request.getParameter("nombreCompleto"));
            c.setCorreo(request.getParameter("correo"));
            c.setTelefono(request.getParameter("telefono"));
            c.setDireccion(request.getParameter("direccion"));

            boolean exito = servicioCliente.actualizarPerfil(c);
            
            String nuevaClave = request.getParameter("nuevaClave");
            String confirmarClave = request.getParameter("confirmarClave");

            boolean errorClave = false;
            if (nuevaClave != null && !nuevaClave.trim().isEmpty()) {
                if (nuevaClave.equals(confirmarClave)) {
                    // Cifra la contraseña
                    String hash = Seguridad.hash(nuevaClave);
                    servicioCliente.actualizarClave(usuarioActual.getIdUsuario(), hash);
                } else {
                    errorClave = true;
                }
            }
            if (exito && !errorClave) {
                usuarioActual.setNombreCompleto(c.getNombreCompleto());
                sesion.setAttribute("usuarioAutenticado", usuarioActual);
                response.sendRedirect(request.getContextPath() + "/cliente/perfil?exito=1");
            } else if (errorClave) {
                request.setAttribute("error", "Las contraseñas no coinciden. Tus otros datos fueron guardados.");
                request.setAttribute("perfil", c);
                request.getRequestDispatcher("/WEB-INF/views/cliente/perfil.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "No se pudo actualizar el perfil.");
                request.setAttribute("perfil", c);
                request.getRequestDispatcher("/WEB-INF/views/cliente/perfil.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error procesando la solicitud: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/cliente/perfil.jsp").forward(request, response);
        }
    }
}
