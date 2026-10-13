package sv.edu.itca.sadu.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Pagina de bienvenida del personal (administrador y cocina) con accesos a sus modulos.
 * Protegida por AccesoFilter.
 */
@WebServlet(name = "PanelServlet", urlPatterns = {"/panel"})
public class PanelServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/panel.jsp").forward(request, response);
    }
}
