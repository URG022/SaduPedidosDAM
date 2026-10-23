package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.Pedido;
import sv.edu.itca.sadu.model.DetallePedido;
import sv.edu.itca.sadu.model.SeguimientoPedido;
import sv.edu.itca.sadu.model.EstadoPedido;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PedidoDAO {

    // Operaciones de escritura
    Long crearPedido(Pedido pedido, Connection con) throws SQLException;

    void agregarDetalle(DetallePedido detalle, Connection con)
            throws SQLException;

    void registrarSeguimiento(SeguimientoPedido seguimiento, Connection con)
            throws SQLException;

    boolean actualizarEstado(Long idPedido, EstadoPedido estado,
                             Connection con) throws SQLException;

    // Consultas
    Pedido obtenerPorId(Long idPedido);

    List<Pedido> obtenerTodos();

    List<Pedido> obtenerPorEstado(EstadoPedido estado);

    List<DetallePedido> obtenerDetalles(Long idPedido);

    List<SeguimientoPedido> obtenerSeguimiento(Long idPedido);
}
