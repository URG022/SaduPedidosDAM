package sv.edu.itca.sadu.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Guia 01: confirma que Tomcat 9 publica la aplicacion y ejecuta Servlets.
 */
@WebServlet(name = "DiagnosticoServlet", urlPatterns = {"/diagnostico"})
public class DiagnosticoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String contexto = request.getContextPath();
        String metodo = request.getMethod();
        String servidor = request.getServerName();
        int puerto = request.getServerPort();
        String javaVersion = System.getProperty("java.version");
        String contenedor = getServletContext().getServerInfo();

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'><head><meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Diagnóstico - Sadu Restaurant</title>");
            out.println("<link rel='stylesheet' href='" + contexto + "/resources/css/estilos.css'>");
            out.println("</head><body><main class='contenedor'>");
            out.println("<h1>Diagnóstico del sistema</h1>");
            out.println("<dl>");
            out.println("<dt>Contexto</dt><dd>" + contexto + "</dd>");
            out.println("<dt>Método HTTP</dt><dd>" + metodo + "</dd>");
            out.println("<dt>Servidor</dt><dd>" + servidor + ":" + puerto + "</dd>");
            out.println("<dt>Contenedor</dt><dd>" + contenedor + "</dd>");
            out.println("<dt>Versión de Java</dt><dd>" + javaVersion + "</dd>");
            out.println("</dl>");
            out.println("<a class='boton' href='" + contexto + "/'>Volver al inicio</a>");
            out.println("</main></body></html>");
        }
    }
}
