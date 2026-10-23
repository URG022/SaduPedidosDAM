package sv.edu.itca.sadu.model;

import java.math.BigDecimal;

public class ItemCarrito {

    private Long idProducto;
    private String nombre;
    private BigDecimal precio;
    private int cantidad;

    public ItemCarrito() {
    }

    public ItemCarrito(Long idProducto, String nombre,
                       BigDecimal precio, int cantidad) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        if (precio == null) {
            return BigDecimal.ZERO;
        }

        return precio.multiply(BigDecimal.valueOf(cantidad));
    }
}
