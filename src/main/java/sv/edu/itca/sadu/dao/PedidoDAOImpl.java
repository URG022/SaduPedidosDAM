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
import sv.edu.itca.sadu.model.ReporteDTO;

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

    // Reportes
    @Override
    public ReporteDTO obtenerMetricasPorRango(String fechaInicio, String fechaFin) throws SQLException {
        ReporteDTO dto = new ReporteDTO();

        boolean filtrarFechas = (fechaInicio != null && !fechaInicio.trim().isEmpty())
                && (fechaFin != null && !fechaFin.trim().isEmpty());

        String sqlVentas = "SELECT COUNT(*) as total_pedidos, COALESCE(SUM(total), 0) as total_ventas "
                + "FROM pedidos WHERE estado = 'ENTREGADO'";
        if (filtrarFechas) {
            sqlVentas += " AND DATE(fecha_creacion) BETWEEN ? AND ?";
        }

        String sqlActivos = "SELECT COUNT(*) FROM pedidos WHERE estado IN ('RECIBIDO', 'EN_PREPARACION', 'LISTO', 'EN_CAMINO')";

        String sqlTop = "SELECT pr.nombre, SUM(dp.cantidad) as cant "
                + "FROM detalle_pedido dp "
                + "JOIN productos pr ON dp.id_producto = pr.id_producto "
                + "JOIN pedidos p ON dp.id_pedido = p.id_pedido "
                + "WHERE p.estado = 'ENTREGADO' ";
        if (filtrarFechas) {
            sqlTop += "AND DATE(p.fecha_creacion) BETWEEN ? AND ? ";
        }
        sqlTop += "GROUP BY pr.id_producto, pr.nombre ORDER BY cant DESC LIMIT 5";

        String sqlEstados = "SELECT estado, COUNT(*) as cant FROM pedidos ";
        if (filtrarFechas) {
            sqlEstados += "WHERE DATE(fecha_creacion) BETWEEN ? AND ? ";
        }
        sqlEstados += "GROUP BY estado";

        try (Connection con = ConexionBD.obtenerConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sqlVentas)) {
                if (filtrarFechas) {
                    ps.setString(1, fechaInicio);
                    ps.setString(2, fechaFin);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int entregados = rs.getInt("total_pedidos");
                        double total = rs.getDouble("total_ventas");
                        dto.setTotalPedidosEntregados(entregados);
                        dto.setVentasTotales(total);
                        dto.setTicketPromedio(entregados > 0 ? total / entregados : 0.0);
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlActivos); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.setPedidosActivos(rs.getInt(1));
                }
            }

            java.util.Map<String, Integer> top = new java.util.LinkedHashMap<>();
            try (PreparedStatement ps = con.prepareStatement(sqlTop)) {
                if (filtrarFechas) {
                    ps.setString(1, fechaInicio);
                    ps.setString(2, fechaFin);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        top.put(rs.getString("nombre"), rs.getInt("cant"));
                    }
                }
            }
            dto.setTopPlatillos(top);

            java.util.Map<String, Integer> estados = new java.util.LinkedHashMap<>();
            try (PreparedStatement ps = con.prepareStatement(sqlEstados)) {
                if (filtrarFechas) {
                    ps.setString(1, fechaInicio);
                    ps.setString(2, fechaFin);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        estados.put(rs.getString("estado"), rs.getInt("cant"));
                    }
                }
            }
            dto.setPedidosPorEstado(estados);
            dto.setHoraPico("12:00 p. m.");
        }
        return dto;
    }
}
