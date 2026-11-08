package sv.edu.itca.sadu.service;

import sv.edu.itca.sadu.dao.CategoriaDAO;
import sv.edu.itca.sadu.dao.CategoriaDAOImpl;
import sv.edu.itca.sadu.dao.DAOException;
import sv.edu.itca.sadu.model.Categoria;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reglas de negocio de las categorías del menú.
 */
public class ServicioCategoria {

    private final CategoriaDAO categoriaDAO;

    public ServicioCategoria() {
        this(new CategoriaDAOImpl());
    }

    public ServicioCategoria(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    public List<Categoria> listarTodas() {
        return categoriaDAO.listarTodas();
    }

    public List<Categoria> listarActivas() {
        return categoriaDAO.listarActivas();
    }

    public Categoria obtenerPorId(Long idCategoria) {
        return categoriaDAO.obtenerPorId(idCategoria);
    }

    /** id -> nombre, para mostrar el nombre de la categoría de cada platillo en los JSP. */
    public Map<Long, String> mapaNombres(List<Categoria> categorias) {
        Map<Long, String> mapa = new LinkedHashMap<>();
        for (Categoria c : categorias) {
            mapa.put(c.getIdCategoria(), c.getNombre());
        }
        return mapa;
    }

    /** Crea (si no tiene id) o actualiza una categoría. */
    public void guardar(Categoria c) {
        validar(c);
        try {
            if (c.getIdCategoria() == null) {
                categoriaDAO.insertar(c);
            } else if (!categoriaDAO.actualizar(c)) {
                throw new IllegalArgumentException("La categoría que intentas editar no existe.");
            }
        } catch (DAOException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new IllegalArgumentException("Ya existe una categoría con el nombre \"" + c.getNombre() + "\".");
            }
            throw e;
        }
    }

    public boolean activar(Long idCategoria) {
        return categoriaDAO.cambiarEstado(idCategoria, true);
    }

    public boolean desactivar(Long idCategoria) {
        return categoriaDAO.cambiarEstado(idCategoria, false);
    }

    private void validar(Categoria c) {
        if (c.getNombre() == null || c.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        c.setNombre(c.getNombre().trim());
        if (c.getNombre().length() > 80) {
            throw new IllegalArgumentException("El nombre no puede tener más de 80 caracteres.");
        }
        if (c.getDescripcion() != null && c.getDescripcion().trim().length() > 200) {
            throw new IllegalArgumentException("La descripción no puede tener más de 200 caracteres.");
        }
        if (c.getOrden() == null || c.getOrden() < 0) {
            throw new IllegalArgumentException("El orden debe ser un número igual o mayor a 0.");
        }
    }
}