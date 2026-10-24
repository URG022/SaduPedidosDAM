package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.Pedido;
import sv.edu.itca.sadu.model.EstadoPedido;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PedidoDAO {

    // Transaccional: usa la conexión del service y devuelve el id generado
    Long insertar(Connection con, Pedido pedido) throws SQLException;

    // Mis pedidos (P5)
    List<Pedido> listarPorCliente(Long idCliente);

    // Detalle (P6): filtra por cliente para que nadie vea pedidos ajenos
    Pedido buscarPorIdYCliente(Long idPedido, Long idCliente);

    Pedido obtenerPorId(Long idPedido);

    List<Pedido> obtenerPorEstado(EstadoPedido estado);

    List<Pedido> obtenerTodos();

    boolean actualizarEstado(Long idPedido, EstadoPedido nuevoEstado, Connection con) throws SQLException;

    List<Pedido> obtenerActivos();
}
