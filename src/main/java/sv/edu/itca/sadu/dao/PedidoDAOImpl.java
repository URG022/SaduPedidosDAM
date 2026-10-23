package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.Pedido;
import sv.edu.itca.sadu.model.DetallePedido;
import sv.edu.itca.sadu.model.SeguimientoPedido;
import sv.edu.itca.sadu.model.EstadoPedido;
import sv.edu.itca.sadu.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {

    @Override
    public Long crearPedido(Pedido pedido, Connection con)
            throws SQLException {

        String sql = "INSERT INTO pedidos "
                + "(id_cliente, tipo_entrega, estado, direccion_entrega, "
                + "notas, total) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, pedido.getIdCliente());
            ps.setString(2, pedido.getTipoEntrega());
            ps.setString(3, pedido.getEstado());

            ps.setString(4, pedido.getDireccionEntrega());
            ps.setString(5, pedido.getNotas());
            ps.setBigDecimal(6, pedido.getTotal());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new SQLException("No se pudo crear el pedido.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        throw new SQLException(
                "No se pudo obtener el ID del pedido generado.");
    }

    @Override
    public void agregarDetalle(DetallePedido detalle, Connection con)
            throws SQLException {

        String sql = "INSERT INTO detalle_pedido "
                + "(id_pedido, id_producto, cantidad, precio_unitario, "
                + "subtotal) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, detalle.getIdPedido());
            ps.setLong(2, detalle.getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.setBigDecimal(4, detalle.getPrecioUnitario());
            ps.setBigDecimal(5, detalle.getSubtotal());

            ps.executeUpdate();
        }
    }

    @Override
    public void registrarSeguimiento(
            SeguimientoPedido seguimiento, Connection con)
            throws SQLException {

        String sql = "INSERT INTO seguimiento_pedido "
                + "(id_pedido, estado, comentario, id_usuario) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, seguimiento.getIdPedido());
            ps.setString(2, seguimiento.getEstado());
            ps.setString(3, seguimiento.getComentario());

            if (seguimiento.getIdUsuario() != null) {
                ps.setLong(4, seguimiento.getIdUsuario());
            } else {
                ps.setNull(4, java.sql.Types.BIGINT);
            }

            ps.executeUpdate();
        }
    }

    @Override
    public boolean actualizarEstado(
            Long idPedido, EstadoPedido estado, Connection con)
            throws SQLException {

        String sql = "UPDATE pedidos SET estado = ? WHERE id_pedido = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setLong(2, idPedido);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Pedido obtenerPorId(Long idPedido) {

        String sql = "SELECT * FROM pedidos WHERE id_pedido = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPedido(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Pedido> obtenerTodos() {

        List<Pedido> lista = new ArrayList<>();

        String sql = "SELECT * FROM pedidos ORDER BY fecha_creacion DESC";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPedido(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public List<Pedido> obtenerPorEstado(EstadoPedido estado) {

        List<Pedido> lista = new ArrayList<>();

        String sql = "SELECT * FROM pedidos "
                + "WHERE estado = ? ORDER BY fecha_creacion ASC";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPedido(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public List<DetallePedido> obtenerDetalles(Long idPedido) {

        List<DetallePedido> lista = new ArrayList<>();

        String sql = "SELECT * FROM detalle_pedido WHERE id_pedido = ? "
                + "ORDER BY id_detalle ASC";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallePedido detalle = new DetallePedido();

                    detalle.setIdDetalle(rs.getLong("id_detalle"));
                    detalle.setIdPedido(rs.getLong("id_pedido"));
                    detalle.setIdProducto(rs.getLong("id_producto"));
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setPrecioUnitario(
                            rs.getBigDecimal("precio_unitario"));
                    detalle.setSubtotal(rs.getBigDecimal("subtotal"));

                    lista.add(detalle);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public List<SeguimientoPedido> obtenerSeguimiento(Long idPedido) {

        List<SeguimientoPedido> lista = new ArrayList<>();

        String sql = "SELECT * FROM seguimiento_pedido "
                + "WHERE id_pedido = ? ORDER BY fecha_registro ASC";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SeguimientoPedido seguimiento =
                            new SeguimientoPedido();

                    seguimiento.setIdSeguimiento(
                            rs.getLong("id_seguimiento"));
                    seguimiento.setIdPedido(rs.getLong("id_pedido"));
                    seguimiento.setEstado(rs.getString("estado"));
                    seguimiento.setComentario(rs.getString("comentario"));

                    long idUsuario = rs.getLong("id_usuario");
                    seguimiento.setIdUsuario(
                            rs.wasNull() ? null : idUsuario);

                    java.sql.Timestamp fecha =
                            rs.getTimestamp("fecha_registro");

                    if (fecha != null) {
                        seguimiento.setFechaRegistro(
                                fecha.toLocalDateTime());
                    }

                    lista.add(seguimiento);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {

        Pedido pedido = new Pedido();

        pedido.setIdPedido(rs.getLong("id_pedido"));
        pedido.setIdCliente(rs.getLong("id_cliente"));
        pedido.setTipoEntrega(rs.getString("tipo_entrega"));
        pedido.setEstado(rs.getString("estado"));
        pedido.setDireccionEntrega(rs.getString("direccion_entrega"));
        pedido.setNotas(rs.getString("notas"));
        pedido.setTotal(rs.getBigDecimal("total"));

        java.sql.Timestamp fechaCreacion =
                rs.getTimestamp("fecha_creacion");

        if (fechaCreacion != null) {
            pedido.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        java.sql.Timestamp fechaActualizacion =
                rs.getTimestamp("fecha_actualizacion");

        if (fechaActualizacion != null) {
            pedido.setFechaActualizacion(
                    fechaActualizacion.toLocalDateTime());
        }

        return pedido;
    }
}
