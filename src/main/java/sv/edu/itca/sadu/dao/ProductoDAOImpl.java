package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.Producto;
import sv.edu.itca.sadu.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {

    private static final String SQL_DESTACADOS
            = "SELECT p.id_producto, p.id_categoria, c.nombre AS categoria, p.nombre, p.descripcion, "
            + "p.precio, p.imagen_url, p.disponible, p.destacado "
            + "FROM productos p JOIN categorias c ON c.id_categoria = p.id_categoria "
            + "WHERE p.destacado = TRUE AND p.disponible = TRUE AND c.activa = TRUE "
            + "ORDER BY c.orden, p.nombre";

    @Override
    public List<Producto> listarDestacados() {
        List<Producto> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion(); PreparedStatement ps = cn.prepareStatement(SQL_DESTACADOS); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudieron cargar los destacados", e);
        }
        return lista;
    }

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

    @Override
    public void insertar(Producto producto) throws DAOException {
        String sql = "INSERT INTO productos (id_categoria, nombre, descripcion, precio, imagen_url, disponible, destacado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, producto.getIdCategoria());
            stmt.setString(2, producto.getNombre());
            stmt.setString(3, producto.getDescripcion());
            stmt.setBigDecimal(4, producto.getPrecio());
            stmt.setString(5, producto.getImagenUrl());
            stmt.setBoolean(6, producto.isDisponible());
            stmt.setBoolean(7, producto.isDestacado());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al insertar el producto", e);
        }
    }

    @Override
    public List<Producto> obtenerTodos() throws DAOException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getLong("id_producto"));
                p.setIdCategoria(rs.getLong("id_categoria"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecio(rs.getBigDecimal("precio"));
                p.setImagenUrl(rs.getString("imagen_url"));
                p.setDisponible(rs.getBoolean("disponible"));
                p.setDestacado(rs.getBoolean("destacado"));
                p.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener los productos", e);
        }
        return lista;
    }

    @Override
    public void actualizar(Producto producto) throws DAOException {
        String sql = "UPDATE productos SET id_categoria = ?, nombre = ?, descripcion = ?, precio = ?, imagen_url = ?, disponible = ?, destacado = ? WHERE id_producto = ?";
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, producto.getIdCategoria());
            stmt.setString(2, producto.getNombre());
            stmt.setString(3, producto.getDescripcion());
            stmt.setBigDecimal(4, producto.getPrecio());
            stmt.setString(5, producto.getImagenUrl());
            stmt.setBoolean(6, producto.isDisponible());
            stmt.setBoolean(7, producto.isDestacado());
            stmt.setLong(8, producto.getIdProducto());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el producto", e);
        }
    }

    @Override
    public void eliminar(Long idProducto) throws DAOException {
        String sql = "UPDATE productos SET disponible = false WHERE id_producto = ?";
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idProducto);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al desactivar (eliminar) el producto", e);
        }
    }

    @Override
    public Producto obtenerPorId(Long idProducto) throws DAOException {
        Producto p = null;
        String sql = "SELECT * FROM productos WHERE id_producto = ?";
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idProducto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    p = new Producto();
                    p.setIdProducto(rs.getLong("id_producto"));
                    p.setIdCategoria(rs.getLong("id_categoria"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setPrecio(rs.getBigDecimal("precio"));
                    p.setImagenUrl(rs.getString("imagen_url"));
                    p.setDisponible(rs.getBoolean("disponible"));
                    p.setDestacado(rs.getBoolean("destacado"));
                    p.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener el producto por ID", e);
        }
        return p;
    }

    //Paginador
    // Método para obtener la lista cortada (15 registros)
    public List<Producto> obtenerProductosPaginados(int limite, int offset) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos ORDER BY id_producto ASC LIMIT ? OFFSET ?";

        try (Connection con = ConexionBD.obtenerConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, limite);
            ps.setInt(2, offset);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getLong("id_producto"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setIdCategoria(rs.getLong("id_categoria"));
                    p.setPrecio(rs.getBigDecimal("precio"));
                    p.setImagenUrl(rs.getString("imagen_url"));
                    p.setDisponible(rs.getBoolean("disponible"));
                    p.setDestacado(rs.getBoolean("destacado"));
                    
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener productos paginados: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    //Método para contar el total exacto de registros en la tabla
    public int contarTotalProductos() {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM productos";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                total = rs.getInt(1); // Obtiene el valor del COUNT(*)
            }
        } catch (Exception e) {
            System.err.println("Error al contar productos: " + e.getMessage());
            e.printStackTrace();
        }
        return total;
    }
    
    public boolean activarProducto(Long idProducto) {
    String sql = "UPDATE productos SET disponible = 1 WHERE id_producto = ?";
    try (Connection con = ConexionBD.obtenerConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setLong(1, idProducto);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
}
