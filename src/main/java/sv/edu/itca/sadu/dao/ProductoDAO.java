package sv.edu.itca.sadu.dao;

import java.util.List;
import sv.edu.itca.sadu.model.Producto;

/**
 * Acceso a la tabla productos.
 * Dev 1 implemento listarDestacados() para el Inicio.
 * Dev 2 agrega aqui el resto del CRUD (listar, buscarPorId, insertar, actualizar, desactivar...).
 */
public interface ProductoDAO {

    List<Producto> listarDestacados();
}
