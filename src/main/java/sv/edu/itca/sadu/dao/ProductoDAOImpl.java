package sv.edu.itca.sadu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sv.edu.itca.sadu.model.Producto;
import sv.edu.itca.sadu.util.ConexionBD;

public class ProductoDAOImpl implements ProductoDAO {

    private static final String SQL_DESTACADOS =
            "SELECT p.id_producto, p.id_categoria, c.nombre AS categoria, p.nombre, p.descripcion, "
            + "p.precio, p.imagen_url, p.disponible, p.destacado "
            + "FROM productos p JOIN categorias c ON c.id_categoria = p.id_categoria "
            + "WHERE p.destacado = TRUE AND p.disponible = TRUE AND c.activa = TRUE "
            + "ORDER BY c.orden, p.nombre";

    @Override
    public List<Producto> listarDestacados() {
        List<Producto> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_DESTACADOS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudieron cargar los destacados", e);
        }
        return lista;
    }

    /** Convierte una fila en Producto. Dev 2 puede reutilizarlo en sus consultas. */
    protected Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getLong("id_producto"));
        p.setIdCategoria(rs.getLong("id_categoria"));
        p.setCategoria(rs.getString("categoria"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setImagenUrl(rs.getString("imagen_url"));
        p.setDisponible(rs.getBoolean("disponible"));
        p.setDestacado(rs.getBoolean("destacado"));
        return p;
    }
}
