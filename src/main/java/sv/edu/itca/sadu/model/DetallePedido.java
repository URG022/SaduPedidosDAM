package sv.edu.itca.sadu.model;

import java.math.BigDecimal;

public class DetallePedido {

    private Long idDetalle;
    private Long idPedido;
    private Long idProducto;
    private String nombreProducto;   // viene del JOIN con productos, no es columna de detalle_pedido
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public DetallePedido() {
    }

    /** Crea el detalle a partir de un item del carrito (el precio lo valida el service contra la BD). */
    public static DetallePedido desdeItem(ItemCarrito item) {
        DetallePedido d = new DetallePedido();
        d.idProducto = item.getIdProducto();
        d.nombreProducto = item.getNombre();
        d.cantidad = item.getCantidad();
        d.precioUnitario = item.getPrecio();
        d.subtotal = item.getSubtotal();
        return d;
    }

    public Long getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Long idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}