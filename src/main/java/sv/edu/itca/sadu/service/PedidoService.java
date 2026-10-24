package sv.edu.itca.sadu.service;

import sv.edu.itca.sadu.dao.ClienteDAO;
import sv.edu.itca.sadu.dao.ClienteDAOImpl;
import sv.edu.itca.sadu.dao.DetallePedidoDAO;
import sv.edu.itca.sadu.dao.DetallePedidoDAOImpl;
import sv.edu.itca.sadu.dao.PedidoDAO;
import sv.edu.itca.sadu.dao.PedidoDAOImpl;
import sv.edu.itca.sadu.dao.ProductoDAO;
import sv.edu.itca.sadu.dao.ProductoDAOImpl;
import sv.edu.itca.sadu.dao.SeguimientoPedidoDAO;
import sv.edu.itca.sadu.dao.SeguimientoPedidoDAOImpl;
import sv.edu.itca.sadu.model.Carrito;
import sv.edu.itca.sadu.model.Cliente;
import sv.edu.itca.sadu.model.DetallePedido;
import sv.edu.itca.sadu.model.EstadoPedido;
import sv.edu.itca.sadu.model.ItemCarrito;
import sv.edu.itca.sadu.model.Pedido;
import sv.edu.itca.sadu.model.Producto;
import sv.edu.itca.sadu.model.SeguimientoPedido;
import sv.edu.itca.sadu.model.TipoEntrega;
import sv.edu.itca.sadu.util.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PedidoService {

    private final PedidoDAO pedidoDAO;
    private final DetallePedidoDAO detalleDAO;
    private final SeguimientoPedidoDAO seguimientoDAO;
    private final ProductoDAO productoDAO;
    private final ClienteDAO clienteDAO;

    // Para usarlo directo desde los servlets: new PedidoService()
    public PedidoService() {
        this(new PedidoDAOImpl(), new DetallePedidoDAOImpl(), new SeguimientoPedidoDAOImpl(),
                new ProductoDAOImpl(), new ClienteDAOImpl());
    }

    public PedidoService(PedidoDAO pedidoDAO, DetallePedidoDAO detalleDAO,
            SeguimientoPedidoDAO seguimientoDAO, ProductoDAO productoDAO,
            ClienteDAO clienteDAO) {
        this.pedidoDAO = pedidoDAO;
        this.detalleDAO = detalleDAO;
        this.seguimientoDAO = seguimientoDAO;
        this.productoDAO = productoDAO;
        this.clienteDAO = clienteDAO;
    }

    // =====================================================================
    //  P4 - Confirmar pedido desde el carrito
    // =====================================================================
    /**
     * Crea el pedido, sus detalles y el seguimiento RECIBIDO en una sola
     * transacción. Los precios se leen de la BD; los del carrito solo se usan
     * para mostrar.
     *
     * @param tipoEntrega texto tal como llega del formulario (EN_LOCAL,
     * PARA_LLEVAR, DOMICILIO)
     * @return ID del pedido creado
     * @throws IllegalArgumentException si algo no es válido (mostrar el mensaje
     * al usuario)
     */
    public Long crearPedido(Carrito carrito, Long idUsuario, String tipoEntrega,
            String direccion, String notas) throws SQLException {

        if (carrito == null || carrito.isVacio()) {
            throw new IllegalArgumentException("Tu carrito está vacío.");
        }

        TipoEntrega tipo = parsearTipoEntrega(tipoEntrega);

        String direccionLimpia = direccion == null ? "" : direccion.trim();
        String notasLimpias = notas == null ? "" : notas.trim();

        if (tipo == TipoEntrega.DOMICILIO && direccionLimpia.isEmpty()) {
            throw new IllegalArgumentException("Debes ingresar una dirección de entrega.");
        }
        if (direccionLimpia.length() > 200) {
            throw new IllegalArgumentException("La dirección no puede pasar de 200 caracteres.");
        }
        if (notasLimpias.length() > 300) {
            throw new IllegalArgumentException("Las notas no pueden pasar de 300 caracteres.");
        }

        Cliente cliente = clienteDAO.obtenerPorIdUsuario(idUsuario);
        if (cliente == null) {
            throw new IllegalArgumentException("No se encontró tu perfil de cliente.");
        }

        // Detalles con el precio real de la BD
        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = productoDAO.obtenerPorId(item.getIdProducto());

            if (producto == null || !producto.isDisponible()) {
                throw new IllegalArgumentException(
                        "«" + item.getNombre() + "» ya no está disponible. Quítalo del carrito para continuar.");
            }

            DetallePedido detalle = new DetallePedido();
            detalle.setIdProducto(producto.getIdProducto());
            detalle.setNombreProducto(producto.getNombre());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));

            total = total.add(detalle.getSubtotal());
            detalles.add(detalle);
        }

        Pedido pedido = new Pedido();
        pedido.setIdCliente(cliente.getIdCliente());
        pedido.setTipoEntrega(tipo);
        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedido.setDireccionEntrega(tipo == TipoEntrega.DOMICILIO ? direccionLimpia : null);
        pedido.setNotas(notasLimpias.isEmpty() ? null : notasLimpias);
        pedido.setTotal(total);

        try (Connection con = ConexionBD.obtenerConexion()) {

            con.setAutoCommit(false);

            try {
                // 1. Encabezado del pedido
                Long idPedido = pedidoDAO.insertar(con, pedido);

                // 2. Platillos
                detalleDAO.insertarLote(con, idPedido, detalles);

                // 3. Estado inicial
                SeguimientoPedido seguimiento = new SeguimientoPedido();
                seguimiento.setIdPedido(idPedido);
                seguimiento.setEstado(EstadoPedido.RECIBIDO);
                seguimiento.setComentario("Pedido recibido.");
                seguimiento.setIdUsuario(idUsuario);
                seguimientoDAO.insertar(con, seguimiento);

                // 4. Confirmar todo
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

    private TipoEntrega parsearTipoEntrega(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar el tipo de entrega.");
        }
        try {
            return TipoEntrega.valueOf(texto.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El tipo de entrega no es válido.");
        }
    }

    // =====================================================================
    //  P5 y P6 - Consultas del cliente
    // =====================================================================
    /**
     * P5: pedidos del usuario con sesión iniciada, del más reciente al más
     * antiguo.
     */
    public List<Pedido> listarMisPedidos(Long idUsuario) {
        Cliente cliente = clienteDAO.obtenerPorIdUsuario(idUsuario);
        if (cliente == null) {
            return Collections.emptyList();
        }
        return pedidoDAO.listarPorCliente(cliente.getIdCliente());
    }

    /**
     * P6: pedido completo (platillos y línea de tiempo) o null si no existe o
     * no pertenece a este usuario.
     */
    public Pedido obtenerDetalleDeCliente(Long idPedido, Long idUsuario) {
        validarIdPedido(idPedido);

        Cliente cliente = clienteDAO.obtenerPorIdUsuario(idUsuario);
        if (cliente == null) {
            return null;
        }

        Pedido pedido = pedidoDAO.buscarPorIdYCliente(idPedido, cliente.getIdCliente());
        if (pedido == null) {
            return null;
        }

        pedido.setDetalles(detalleDAO.listarPorPedido(idPedido));
        pedido.setSeguimientos(seguimientoDAO.listarPorPedido(idPedido));
        return pedido;
    }

    // =====================================================================
    //  Cocina / administración
    // =====================================================================
    /**
     * Cambia el estado de un pedido y lo registra en su historial.
     */
    public boolean cambiarEstado(Long idPedido, EstadoPedido nuevoEstado,
            String comentario, Long idUsuario) throws SQLException {

        validarIdPedido(idPedido);

        if (nuevoEstado == null) {
            throw new IllegalArgumentException("Debes indicar el nuevo estado.");
        }

        Pedido actual = pedidoDAO.obtenerPorId(idPedido);
        if (actual == null) {
            return false;
        }
        if (actual.getEstado() == EstadoPedido.ENTREGADO || actual.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException("El pedido ya está finalizado y no puede cambiar de estado.");
        }
        if (actual.getEstado() == nuevoEstado) {
            throw new IllegalArgumentException("El pedido ya se encuentra en ese estado.");
        }

        try (Connection con = ConexionBD.obtenerConexion()) {

            con.setAutoCommit(false);

            try {
                boolean actualizado = pedidoDAO.actualizarEstado(idPedido, nuevoEstado, con);

                if (!actualizado) {
                    con.rollback();
                    return false;
                }

                SeguimientoPedido seguimiento = new SeguimientoPedido();
                seguimiento.setIdPedido(idPedido);
                seguimiento.setEstado(nuevoEstado);
                seguimiento.setComentario(comentario);
                seguimiento.setIdUsuario(idUsuario);
                seguimientoDAO.insertar(con, seguimiento);

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

    public Pedido obtenerPedido(Long idPedido) {
        validarIdPedido(idPedido);
        return pedidoDAO.obtenerPorId(idPedido);
    }

    public List<Pedido> obtenerPedidosPorEstado(EstadoPedido estado) {
        if (estado == null) {
            throw new IllegalArgumentException("Debes indicar el estado del pedido.");
        }
        return pedidoDAO.obtenerPorEstado(estado);
    }

    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoDAO.obtenerTodos();
    }

    public List<DetallePedido> obtenerDetalles(Long idPedido) {
        validarIdPedido(idPedido);
        return detalleDAO.listarPorPedido(idPedido);
    }

    public List<SeguimientoPedido> obtenerSeguimiento(Long idPedido) {
        validarIdPedido(idPedido);
        return seguimientoDAO.listarPorPedido(idPedido);
    }

    private void validarIdPedido(Long idPedido) {
        if (idPedido == null || idPedido <= 0) {
            throw new IllegalArgumentException("El ID del pedido no es válido.");
        }
    }
}
