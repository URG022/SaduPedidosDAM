package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.Comprobante;
import sv.edu.itca.sadu.model.MetodoPago;
import sv.edu.itca.sadu.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ComprobanteDAOImpl implements ComprobanteDAO {

    @Override
    public void insertar(Connection con, Comprobante c) throws SQLException {
        String sql = "INSERT INTO comprobantes (id_pedido, numero, metodo_pago, total) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, c.getIdPedido());
            ps.setString(2, c.getNumero());
            ps.setString(3, c.getMetodoPago().name());
            ps.setBigDecimal(4, c.getTotal());
            ps.executeUpdate();
        }
    }

    @Override
    public Comprobante buscarPorPedido(Long idPedido) {
        Comprobante c = null;
        String sql = "SELECT id_comprobante, id_pedido, numero, metodo_pago, total, fecha_emision " +
                     "FROM comprobantes WHERE id_pedido = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = new Comprobante();
                    c.setIdComprobante(rs.getLong("id_comprobante"));
                    c.setIdPedido(rs.getLong("id_pedido"));
                    c.setNumero(rs.getString("numero"));
                    c.setMetodoPago(MetodoPago.valueOf(rs.getString("metodo_pago")));
                    c.setTotal(rs.getBigDecimal("total"));
                    Timestamp fecha = rs.getTimestamp("fecha_emision");
                    if (fecha != null) {
                        c.setFechaEmision(fecha.toLocalDateTime());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }
}