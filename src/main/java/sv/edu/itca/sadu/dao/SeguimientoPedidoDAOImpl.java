package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.EstadoPedido;
import sv.edu.itca.sadu.model.SeguimientoPedido;
import sv.edu.itca.sadu.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SeguimientoPedidoDAOImpl implements SeguimientoPedidoDAO {

    @Override
    public void insertar(Connection con, SeguimientoPedido s) throws SQLException {
        String sql = "INSERT INTO seguimiento_pedido (id_pedido, estado, comentario, id_usuario) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, s.getIdPedido());
            ps.setString(2, s.getEstado().name());
            ps.setString(3, s.getComentario());
            if (s.getIdUsuario() != null) {
                ps.setLong(4, s.getIdUsuario());
            } else {
                ps.setNull(4, Types.BIGINT);   // id_usuario es NULL permitido
            }
            ps.executeUpdate();
        }
    }

    @Override
    public List<SeguimientoPedido> listarPorPedido(Long idPedido) {
        List<SeguimientoPedido> lista = new ArrayList<>();
        String sql = "SELECT id_seguimiento, id_pedido, estado, comentario, id_usuario, fecha_registro " +
                     "FROM seguimiento_pedido WHERE id_pedido = ? " +
                     "ORDER BY fecha_registro ASC, id_seguimiento ASC";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SeguimientoPedido s = new SeguimientoPedido();
                    s.setIdSeguimiento(rs.getLong("id_seguimiento"));
                    s.setIdPedido(rs.getLong("id_pedido"));
                    s.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
                    s.setComentario(rs.getString("comentario"));

                    long idUsuario = rs.getLong("id_usuario");
                    if (!rs.wasNull()) {
                        s.setIdUsuario(idUsuario);
                    }

                    Timestamp fecha = rs.getTimestamp("fecha_registro");
                    if (fecha != null) {
                        s.setFechaRegistro(fecha.toLocalDateTime());
                    }
                    lista.add(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}