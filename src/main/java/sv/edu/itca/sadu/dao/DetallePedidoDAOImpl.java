package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.DetallePedido;
import sv.edu.itca.sadu.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetallePedidoDAOImpl implements DetallePedidoDAO {

    @Override
    public void insertarLote(Connection con, Long idPedido, List<DetallePedido> detalles) throws SQLException {
        String sql = "INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario, subtotal) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (DetallePedido d : detalles) {
                ps.setLong(1, idPedido);
                ps.setLong(2, d.getIdProducto());
                ps.setInt(3, d.getCantidad());
                ps.setBigDecimal(4, d.getPrecioUnitario());
                ps.setBigDecimal(5, d.getSubtotal());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public List<DetallePedido> listarPorPedido(Long idPedido) {
        List<DetallePedido> lista = new ArrayList<>();
        String sql = "SELECT d.id_detalle, d.id_pedido, d.id_producto, p.nombre AS nombre_producto, " +
                     "d.cantidad, d.precio_unitario, d.subtotal " +
                     "FROM detalle_pedido d INNER JOIN productos p ON d.id_producto = p.id_producto " +
                     "WHERE d.id_pedido = ? ORDER BY d.id_detalle ASC";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallePedido d = new DetallePedido();
                    d.setIdDetalle(rs.getLong("id_detalle"));
                    d.setIdPedido(rs.getLong("id_pedido"));
                    d.setIdProducto(rs.getLong("id_producto"));
                    d.setNombreProducto(rs.getString("nombre_producto"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    d.setSubtotal(rs.getBigDecimal("subtotal"));
                    lista.add(d);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}