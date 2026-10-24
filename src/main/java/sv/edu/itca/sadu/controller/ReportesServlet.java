/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.itca.sadu.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sv.edu.itca.sadu.model.ReporteDTO;
import sv.edu.itca.sadu.service.PedidoService;

/**
 *
 * @author serva
 */
@WebServlet(name = "ReportesServlet", urlPatterns = {"/admin/dashboard", "/admin/reportes"})
public class ReportesServlet extends HttpServlet {
    private final PedidoService pedidoService = new PedidoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        
        // Capturar los filtros de fecha de la petición
        String fechaInicio = request.getParameter("fechaInicio");
        String fechaFin = request.getParameter("fechaFin");
        
        ReporteDTO reporte = pedidoService.obtenerMetricasDashboard(fechaInicio, fechaFin);
        request.setAttribute("reporte", reporte);
        
        if ("/admin/dashboard".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/admin/reportes.jsp").forward(request, response);
        }
    }
}