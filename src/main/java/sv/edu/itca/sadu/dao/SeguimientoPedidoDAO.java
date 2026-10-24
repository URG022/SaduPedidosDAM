package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.SeguimientoPedido;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface SeguimientoPedidoDAO {

    // Transaccional: aquí solo se usa para registrar RECIBIDO al crear el pedido
    void insertar(Connection con, SeguimientoPedido seguimiento) throws SQLException;

    // Línea de tiempo (P6), del más antiguo al más reciente
    List<SeguimientoPedido> listarPorPedido(Long idPedido);
}