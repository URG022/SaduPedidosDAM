package sv.edu.itca.sadu.controller;

import java.io.IOException;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sv.edu.itca.sadu.dao.DAOException;
import sv.edu.itca.sadu.service.ServicioProducto;

/**
 * Portada con los platillos destacados.
 */
@WebServlet(name = "InicioServlet", urlPatterns = {"/inicio"})
public class InicioServlet extends HttpServlet {

    private final ServicioProducto servicioProducto = new ServicioProducto();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("destacados", servicioProducto.listarDestacados());
        } catch (DAOException e) {
            log("Inicio sin destacados: " + e.getCause().getMessage());
            request.setAttribute("destacados", Collections.emptyList());
            request.setAttribute("avisoBD", true);
        }
        request.getRequestDispatcher("/WEB-INF/views/inicio.jsp").forward(request, response);
    }
}
