/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

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

            if (exito) {
                usuarioActual.setNombreCompleto(c.getNombreCompleto());
                // Al actualizar, guardamos con el nombre correcto
                sesion.setAttribute("usuarioAutenticado", usuarioActual);
                
                response.sendRedirect(request.getContextPath() + "/cliente/perfil?exito=1");
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