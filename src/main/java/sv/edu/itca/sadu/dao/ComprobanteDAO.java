package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.Comprobante;
import java.sql.Connection;
import java.sql.SQLException;

public interface ComprobanteDAO {

    // Transaccional: se llama dentro de PedidoService.cambiarEstado
    void insertar(Connection con, Comprobante comprobante) throws SQLException;

    // P6: null si el pedido todavía no tiene comprobante
    Comprobante buscarPorPedido(Long idPedido);
}