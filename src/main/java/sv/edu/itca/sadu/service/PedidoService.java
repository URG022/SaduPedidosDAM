package sv.edu.itca.sadu.service;

import sv.edu.itca.sadu.dao.PedidoDAO;
import sv.edu.itca.sadu.model.Pedido;
import sv.edu.itca.sadu.model.DetallePedido;
import sv.edu.itca.sadu.model.SeguimientoPedido;
import sv.edu.itca.sadu.model.EstadoPedido;
import sv.edu.itca.sadu.model.TipoEntrega;
import sv.edu.itca.sadu.util.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PedidoService {

    private final PedidoDAO pedidoDAO;

    public PedidoService(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    /**
     * Crea un pedido, sus detalles y el seguimiento inicial
     * dentro de una única transacción.
     *
     * @return ID del pedido creado.
     */
    public Long crearPedido(
            Pedido pedido,
            List<DetallePedido> detalles,
            Long idUsuario) throws SQLException {

        validarPedido(pedido, detalles);

        BigDecimal total = BigDecimal.ZERO;

        // Calcular subtotales y total del pedido.
        for (DetallePedido detalle : detalles) {

            if (detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor que cero.");
            }

            if (detalle.getIdProducto() == null) {
                throw new IllegalArgumentException(
                        "Cada detalle debe tener un producto.");
            }

            BigDecimal precio = detalle.getPrecioUnitario();

            if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "El precio del producto debe ser mayor que cero.");
            }

            BigDecimal subtotal = precio.multiply(
                    BigDecimal.valueOf(detalle.getCantidad()));

            detalle.setSubtotal(subtotal);
            total = total.add(subtotal);
        }

        pedido.setEstado(EstadoPedido.RECIBIDO.name());
        pedido.setTotal(total);

        if (pedido.getTipoEntrega() != null) {
            pedido.setTipoEntrega(
                    TipoEntrega.valueOf(pedido.getTipoEntrega()).name());
        }

        try (Connection con = ConexionBD.obtenerConexion()) {

            con.setAutoCommit(false);

            try {
                // 1. Crear el pedido.
                Long idPedido = pedidoDAO.crearPedido(pedido, con);

                // 2. Guardar los detalles.
                for (DetallePedido detalle : detalles) {
                    detalle.setIdPedido(idPedido);
                    pedidoDAO.agregarDetalle(detalle, con);
                }

                // 3. Registrar el estado inicial.
                SeguimientoPedido seguimiento =
                        new SeguimientoPedido();

                seguimiento.setIdPedido(idPedido);
                seguimiento.setEstado(EstadoPedido.RECIBIDO.name());
                seguimiento.setComentario("Pedido recibido.");
                seguimiento.setIdUsuario(idUsuario);

                pedidoDAO.registrarSeguimiento(seguimiento, con);

                // 4. Confirmar todos los cambios.
                con.commit();

                return idPedido;

            } catch (SQLException | RuntimeException e) {
                try {
                    con.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw e;
            }
        }
    }

    /**
     * Valida los datos generales del pedido.
     */
    private void validarPedido(
            Pedido pedido,
            List<DetallePedido> detalles) {

        if (pedido == null) {
            throw new IllegalArgumentException(
                    "El pedido no puede ser nulo.");
        }

        if (pedido.getIdCliente() == null) {
            throw new IllegalArgumentException(
                    "Debes indicar el cliente del pedido.");
        }

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException(
                    "El pedido debe contener al menos un producto.");
        }

        if (pedido.getTipoEntrega() == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar el tipo de entrega.");
        }

        // Comprueba que el tipo de entrega sea válido.
        TipoEntrega tipoEntrega;

        try {
            tipoEntrega = TipoEntrega.valueOf(
                    pedido.getTipoEntrega());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "El tipo de entrega no es válido.");
        }

        // Para entrega a domicilio se requiere dirección.
        if (tipoEntrega == TipoEntrega.DOMICILIO
                && (pedido.getDireccionEntrega() == null
                || pedido.getDireccionEntrega().trim().isEmpty())) {

            throw new IllegalArgumentException(
                    "Debes ingresar una dirección de entrega.");
        }
    }

    /**
     * Cambia el estado de un pedido y registra
     * el cambio en su historial.
     */
    public boolean cambiarEstado(
            Long idPedido,
            EstadoPedido nuevoEstado,
            String comentario,
            Long idUsuario) throws SQLException {

        if (idPedido == null || idPedido <= 0) {
            throw new IllegalArgumentException(
                    "El ID del pedido no es válido.");
        }

        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "Debes indicar el nuevo estado.");
        }

        try (Connection con = ConexionBD.obtenerConexion()) {

            con.setAutoCommit(false);

            try {
                boolean actualizado = pedidoDAO.actualizarEstado(
                        idPedido, nuevoEstado, con);

                if (!actualizado) {
                    con.rollback();
                    return false;
                }

                SeguimientoPedido seguimiento =
                        new SeguimientoPedido();

                seguimiento.setIdPedido(idPedido);
                seguimiento.setEstado(nuevoEstado.name());
                seguimiento.setComentario(comentario);
                seguimiento.setIdUsuario(idUsuario);

                pedidoDAO.registrarSeguimiento(seguimiento, con);

                con.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                try {
                    con.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw e;
            }
        }
    }

    /**
     * Consulta el pedido por su identificador.
     */
    public Pedido obtenerPedido(Long idPedido) {
        if (idPedido == null || idPedido <= 0) {
            throw new IllegalArgumentException(
                    "El ID del pedido no es válido.");
        }

        return pedidoDAO.obtenerPorId(idPedido);
    }

    /**
     * Consulta los pedidos por estado.
     */
    public List<Pedido> obtenerPedidosPorEstado(
            EstadoPedido estado) {

        if (estado == null) {
            throw new IllegalArgumentException(
                    "Debes indicar el estado del pedido.");
        }

        return pedidoDAO.obtenerPorEstado(estado);
    }

    /**
     * Consulta todos los pedidos.
     */
    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoDAO.obtenerTodos();
    }

    /**
     * Consulta los productos de un pedido.
     */
    public List<DetallePedido> obtenerDetalles(Long idPedido) {
        validarIdPedido(idPedido);
        return pedidoDAO.obtenerDetalles(idPedido);
    }

    /**
     * Consulta el historial de un pedido.
     */
    public List<SeguimientoPedido> obtenerSeguimiento(
            Long idPedido) {

        validarIdPedido(idPedido);
        return pedidoDAO.obtenerSeguimiento(idPedido);
    }

    private void validarIdPedido(Long idPedido) {
        if (idPedido == null || idPedido <= 0) {
            throw new IllegalArgumentException(
                    "El ID del pedido no es válido.");
        }
    }
}
