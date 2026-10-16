package sv.edu.itca.sadu.dao;

import java.util.List;
import sv.edu.itca.sadu.model.Producto;

public interface ProductoDAO {

    List<Producto> listarDestacados();
    
    //CRUD
    void insertar(Producto producto) throws DAOException;
    void actualizar(Producto producto) throws DAOException;
    void eliminar(Long idProducto) throws DAOException;
    Producto obtenerPorId(Long idProducto) throws DAOException;
    List<Producto> obtenerTodos() throws DAOException;
    List<Producto> obtenerProductosPaginados(int limite, int offset);
    int contarTotalProductos();
    boolean activarProducto(Long idProducto);
}
