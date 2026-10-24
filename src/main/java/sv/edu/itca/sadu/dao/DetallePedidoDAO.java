package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.DetallePedido;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DetallePedidoDAO {

    // Transaccional: inserta todos los platillos del pedido en un solo lote
    void insertarLote(Connection con, Long idPedido, List<DetallePedido> detalles) throws SQLException;

    // Detalle (P6): trae también el nombre del producto
    List<DetallePedido> listarPorPedido(Long idPedido);
}