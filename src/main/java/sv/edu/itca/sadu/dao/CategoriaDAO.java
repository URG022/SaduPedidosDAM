package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.Categoria;
import java.util.List;

/**
 * Acceso a la tabla categorias (secciones del menú).
 */
public interface CategoriaDAO {

    /** Todas las categorías (activas e inactivas), ordenadas por el campo orden. */
    List<Categoria> listarTodas();

    /** Solo las activas, en el orden en que se muestran en el menú del cliente. */
    List<Categoria> listarActivas();

    Categoria obtenerPorId(Long idCategoria);

    void insertar(Categoria categoria);

    boolean actualizar(Categoria categoria);

    /** Activa o desactiva una categoría (borrado lógico). */
    boolean cambiarEstado(Long idCategoria, boolean activa);
}