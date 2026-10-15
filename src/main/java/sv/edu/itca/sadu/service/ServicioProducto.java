/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package sv.edu.itca.sadu.service;

import sv.edu.itca.sadu.dao.DAOException;
import sv.edu.itca.sadu.dao.ProductoDAO;
import sv.edu.itca.sadu.dao.ProductoDAOImpl;
import sv.edu.itca.sadu.model.Producto;

import java.math.BigDecimal;
import java.util.List;
/**
 *
 * @author Sarai Villalta
 */
public class ServicioProducto {
    
    private ProductoDAO productoDAO;

    public ServicioProducto() {
        this.productoDAO = new ProductoDAOImpl();
    }

    public void registrarProducto(Producto producto) throws Exception {
        validarProducto(producto);
        productoDAO.insertar(producto);
    }

    public void actualizarProducto(Producto producto) throws Exception {
        validarProducto(producto);
        productoDAO.actualizar(producto);
    }

    public void eliminarProducto(Long idProducto) throws DAOException {
        productoDAO.eliminar(idProducto);
    }

    public Producto obtenerProductoPorId(Long idProducto) throws DAOException {
        return productoDAO.obtenerPorId(idProducto);
    }

    public List<Producto> obtenerTodosLosProductos() throws DAOException {
        return productoDAO.obtenerTodos();
    }

    // Reglas de negocio y validaciones antes de tocar la base de datos
    private void validarProducto(Producto producto) throws Exception {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del producto es obligatorio.");
        }
        if (producto.getIdCategoria() <= 0) {
            throw new Exception("Debe seleccionar una categoría válida.");
        }
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("El precio del producto debe ser mayor a cero.");
        }
    }
}