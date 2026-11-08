package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.Categoria;
import sv.edu.itca.sadu.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl implements CategoriaDAO {

    private static final String COLUMNAS = "SELECT id_categoria, nombre, descripcion, orden, activa FROM categorias ";

    @Override
    public List<Categoria> listarTodas() {
        return listar(COLUMNAS + "ORDER BY orden, nombre");
    }

    @Override
    public List<Categoria> listarActivas() {
        return listar(COLUMNAS + "WHERE activa = TRUE ORDER BY orden, nombre");
    }

    @Override
    public Categoria obtenerPorId(Long idCategoria) {
        String sql = COLUMNAS + "WHERE id_categoria = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar la categoría", e);
        }
    }

    @Override
    public void insertar(Categoria c) {
        String sql = "INSERT INTO categorias (nombre, descripcion, orden, activa) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt(3, c.getOrden());
            ps.setBoolean(4, c.getActiva());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al registrar la categoría", e);
        }
    }

    @Override
    public boolean actualizar(Categoria c) {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ?, orden = ?, activa = ? WHERE id_categoria = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt(3, c.getOrden());
            ps.setBoolean(4, c.getActiva());
            ps.setLong(5, c.getIdCategoria());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar la categoría", e);
        }
    }

    @Override
    public boolean cambiarEstado(Long idCategoria, boolean activa) {
        String sql = "UPDATE categorias SET activa = ? WHERE id_categoria = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, activa);
            ps.setLong(2, idCategoria);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error al cambiar el estado de la categoría", e);
        }
    }

    private List<Categoria> listar(String sql) {
        List<Categoria> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar las categorías", e);
        }
        return lista;
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setIdCategoria(rs.getLong("id_categoria"));
        c.setNombre(rs.getString("nombre"));
        c.setDescripcion(rs.getString("descripcion"));
        c.setOrden(rs.getInt("orden"));
        c.setActiva(rs.getBoolean("activa"));
        return c;
    }
}