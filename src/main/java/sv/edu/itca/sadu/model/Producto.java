package sv.edu.itca.sadu.model;

import java.math.BigDecimal;

/**
 * Platillo del menu (tabla productos).
 * Dev 1 lo usa para los destacados del Inicio; Dev 2 es el dueno del modulo
 * y puede agregar lo que necesite para el menu y el mantenimiento.
 */
public class Producto {

    private long idProducto;
    private long idCategoria;
    private String categoria;     // nombre de la categoria (viene del JOIN)
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;
    private boolean disponible;
    private boolean destacado;

    public long getIdProducto() { return idProducto; }
    public void setIdProducto(long idProducto) { this.idProducto = idProducto; }

    public long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(long idCategoria) { this.idCategoria = idCategoria; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public boolean isDestacado() { return destacado; }
    public void setDestacado(boolean destacado) { this.destacado = destacado; }
}
