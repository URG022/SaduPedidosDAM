package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.EstadoPedido;
import sv.edu.itca.sadu.model.Pedido;
import sv.edu.itca.sadu.model.TipoEntrega;
import sv.edu.itca.sadu.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {

    @Override
    public Long insertar(Connection con, Pedido p) throws SQLException {
        String sql = "INSERT INTO pedidos (id_cliente, tipo_entrega, estado, direccion_entrega, notas, total) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, p.getIdCliente());
            ps.setString(2, p.getTipoEntrega().name());
            ps.setString(3, p.getEstado().name());
            ps.setString(4, p.getDireccionEntrega());   // null si no es DOMICILIO
            ps.setString(5, p.getNotas());
            ps.setBigDecimal(6, p.getTotal());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del pedido creado.");
    }

    @Override
    public List<Pedido> listarPorCliente(Long idCliente) {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT id_pedido, id_cliente, tipo_entrega, estado, direccion_entrega, notas, total, "
                + "fecha_creacion, fecha_actualizacion "
                + "FROM pedidos WHERE id_cliente = ? "
                + "ORDER BY fecha_creacion DESC, id_pedido DESC";

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Pedido buscarPorIdYCliente(Long idPedido, Long idCliente) {
        Pedido p = null;
        String sql = "SELECT id_pedido, id_cliente, tipo_entrega, estado, direccion_entrega, notas, total, "
                + "fecha_creacion, fecha_actualizacion "
                + "FROM pedidos WHERE id_pedido = ? AND id_cliente = ?";

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idPedido);
            ps.setLong(2, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = mapear(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    // En PedidoDAOImpl (agrega el import de EstadoPedido si no lo tienes; ya lo usa mapear)
    @Override
    public Pedido obtenerPorId(Long idPedido) {
        Pedido p = null;
        String sql = "SELECT id_pedido, id_cliente, tipo_entrega, estado, direccion_entrega, notas, total, "
                + "fecha_creacion, fecha_actualizacion FROM pedidos WHERE id_pedido = ?";

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = mapear(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    @Override
    public List<Pedido> obtenerPorEstado(EstadoPedido estado) {
        List<Pedido> lista = new ArrayList<>();
        // Más antiguos primero: es el orden natural de un tablero de cocina
        String sql = "SELECT id_pedido, id_cliente, tipo_entrega, estado, direccion_entrega, notas, total, "
                + "fecha_creacion, fecha_actualizacion FROM pedidos WHERE estado = ? "
                + "ORDER BY fecha_creacion ASC, id_pedido ASC";

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Pedido> obtenerTodos() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT id_pedido, id_cliente, tipo_entrega, estado, direccion_entrega, notas, total, "
                + "fecha_creacion, fecha_actualizacion FROM pedidos "
                + "ORDER BY fecha_creacion DESC, id_pedido DESC";

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean actualizarEstado(Long idPedido, EstadoPedido nuevoEstado, Connection con) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ? WHERE id_pedido = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.name());
            ps.setLong(2, idPedido);
            return ps.executeUpdate() > 0;
        }
    }

    private Pedido mapear(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setIdPedido(rs.getLong("id_pedido"));
        p.setIdCliente(rs.getLong("id_cliente"));
        p.setTipoEntrega(TipoEntrega.valueOf(rs.getString("tipo_entrega")));
        p.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
        p.setDireccionEntrega(rs.getString("direccion_entrega"));
        p.setNotas(rs.getString("notas"));
        p.setTotal(rs.getBigDecimal("total"));

        Timestamp creacion = rs.getTimestamp("fecha_creacion");
        if (creacion != null) {
            p.setFechaCreacion(creacion.toLocalDateTime());
        }
        Timestamp actualizacion = rs.getTimestamp("fecha_actualizacion");
        if (actualizacion != null) {
            p.setFechaActualizacion(actualizacion.toLocalDateTime());
        }
        return p;
    }

    // Implementación
    @Override
    public List<Pedido> obtenerActivos() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT id_pedido, id_cliente, tipo_entrega, estado, direccion_entrega, notas, total, "
                + "fecha_creacion, fecha_actualizacion FROM pedidos "
                + "WHERE estado IN ('RECIBIDO','EN_PREPARACION','LISTO') "
                + "ORDER BY fecha_creacion ASC, id_pedido ASC";

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
