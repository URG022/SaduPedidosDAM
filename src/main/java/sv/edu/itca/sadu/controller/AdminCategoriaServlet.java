package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.model.Categoria;
import sv.edu.itca.sadu.service.ServicioCategoria;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "AdminCategoriaServlet", urlPatterns = {"/admin/categorias"})
public class AdminCategoriaServlet extends HttpServlet {

    private static final String LISTA = "/WEB-INF/views/admin/categorias.jsp";
    private static final String FORM = "/WEB-INF/views/admin/form_categoria.jsp";

    private ServicioCategoria servicioCategoria;

    @Override
    public void init() throws ServletException {
        servicioCategoria = new ServicioCategoria();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        String volver = request.getContextPath() + "/admin/categorias";

        try {
            if ("nuevo".equals(accion)) {
                request.getRequestDispatcher(FORM).forward(request, response);
                return;
            }
            if ("editar".equals(accion)) {
                Categoria c = servicioCategoria.obtenerPorId(Long.parseLong(request.getParameter("id")));
                if (c == null) {
                    response.sendRedirect(volver + "?error=1");
                    return;
                }
                request.setAttribute("categoria", c);
                request.getRequestDispatcher(FORM).forward(request, response);
                return;
            }
            if ("desactivar".equals(accion) || "activar".equals(accion)) {
                Long id = Long.parseLong(request.getParameter("id"));
                boolean ok = "activar".equals(accion)
                        ? servicioCategoria.activar(id)
                        : servicioCategoria.desactivar(id);
                response.sendRedirect(volver + (ok ? "?exito=" + accion : "?error=1"));
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(volver + "?error=1");
            return;
        }

        request.setAttribute("categorias", servicioCategoria.listarTodas());
        request.getRequestDispatcher(LISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Categoria c = new Categoria();
        c.setNombre(request.getParameter("nombre"));
        c.setDescripcion(request.getParameter("descripcion"));
        c.setActiva(request.getParameter("activa") != null);

        try {
            String id = request.getParameter("idCategoria");
            c.setIdCategoria(id == null || id.trim().isEmpty() ? null : Long.valueOf(id.trim()));
            c.setOrden(Integer.valueOf(request.getParameter("orden").trim()));
            servicioCategoria.guardar(c);
            response.sendRedirect(request.getContextPath() + "/admin/categorias?exito=guardada");
        } catch (NumberFormatException | NullPointerException e) {
            volverAlFormulario(request, response, c, "El orden debe ser un número entero.");
        } catch (IllegalArgumentException e) {
            volverAlFormulario(request, response, c, e.getMessage());
        }
    }

    private void volverAlFormulario(HttpServletRequest request, HttpServletResponse response,
            Categoria c, String error) throws ServletException, IOException {
        request.setAttribute("error", error);
        request.setAttribute("categoria", c);   // se conservan los datos escritos
        request.getRequestDispatcher(FORM).forward(request, response);
    }
}