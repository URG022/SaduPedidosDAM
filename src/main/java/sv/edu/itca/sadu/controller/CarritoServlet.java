
package sv.edu.itca.sadu.controller;

import sv.edu.itca.sadu.model.ItemCarrito;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@WebServlet(name = "CarritoServlet", urlPatterns = {"/carrito"})
public class CarritoServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/views/carrito.jsp";
    private static final String CARRITO = "carrito";

    @SuppressWarnings("unchecked")
    private List<ItemCarrito> obtenerCarrito(HttpSession session) {
        List<ItemCarrito> carrito =
                (List<ItemCarrito>) session.getAttribute(CARRITO);

        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute(CARRITO, carrito);
        }

        return carrito;
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        List<ItemCarrito> carrito = obtenerCarrito(session);

        request.setAttribute("carrito", carrito);
        request.getRequestDispatcher(VISTA)
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<ItemCarrito> carrito = obtenerCarrito(session);
        String accion = request.getParameter("accion");

        try {
            if ("actualizar".equals(accion)) {

                Long idProducto = Long.parseLong(
                        request.getParameter("idProducto"));

                int cantidad = Integer.parseInt(
                        request.getParameter("cantidad"));

                if (cantidad <= 0) {
                    throw new IllegalArgumentException(
                            "La cantidad debe ser mayor que cero.");
                }

                boolean encontrado = false;

                for (ItemCarrito item : carrito) {
                    if (item.getIdProducto().equals(idProducto)) {
                        item.setCantidad(cantidad);
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    throw new IllegalArgumentException(
                            "El producto no está en el carrito.");
                }

            } else if ("eliminar".equals(accion)) {

                Long idProducto = Long.parseLong(
                        request.getParameter("idProducto"));

                Iterator<ItemCarrito> iterator = carrito.iterator();

                while (iterator.hasNext()) {
                    ItemCarrito item = iterator.next();

                    if (item.getIdProducto().equals(idProducto)) {
                        iterator.remove();
                        break;
                    }
                }

            } else if ("vaciar".equals(accion)) {

                carrito.clear();

            } else {
                throw new IllegalArgumentException(
                        "La acción solicitada no es válida.");
            }

            response.sendRedirect(
                    request.getContextPath() + "/carrito?exito=1");

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Los datos enviados no son válidos.");

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("carrito", carrito);
            request.getRequestDispatcher(VISTA)
                    .forward(request, response);
        }
    }
}
