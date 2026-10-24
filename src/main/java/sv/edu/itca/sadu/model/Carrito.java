package sv.edu.itca.sadu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Carrito implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int MAX_CANTIDAD = 20;

    // LinkedHashMap mantiene el orden en que se agregaron los platillos
    private final Map<Long, ItemCarrito> items = new LinkedHashMap<>();

    public void agregar(ItemCarrito nuevo) {
        ItemCarrito existente = items.get(nuevo.getIdProducto());
        if (existente == null) {
            nuevo.setCantidad(Math.min(Math.max(nuevo.getCantidad(), 1), MAX_CANTIDAD));
            items.put(nuevo.getIdProducto(), nuevo);
        } else {
            existente.setCantidad(Math.min(existente.getCantidad() + nuevo.getCantidad(), MAX_CANTIDAD));
        }
    }

    public void sumar(Long idProducto) {
        ItemCarrito item = items.get(idProducto);
        if (item != null && item.getCantidad() < MAX_CANTIDAD) {
            item.setCantidad(item.getCantidad() + 1);
        }
    }

    public void restar(Long idProducto) {
        ItemCarrito item = items.get(idProducto);
        if (item == null) {
            return;
        }
        if (item.getCantidad() <= 1) {
            items.remove(idProducto);
        } else {
            item.setCantidad(item.getCantidad() - 1);
        }
    }

    public void quitar(Long idProducto) {
        items.remove(idProducto);
    }

    public void vaciar() {
        items.clear();
    }

    /** Lista para recorrer con c:forEach en el JSP. */
    public List<ItemCarrito> getItems() {
        return new ArrayList<>(items.values());
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrito item : items.values()) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    /** Suma de unidades, útil para el contador del encabezado. */
    public int getCantidadTotal() {
        int suma = 0;
        for (ItemCarrito item : items.values()) {
            suma += item.getCantidad();
        }
        return suma;
    }

    /** En el JSP: ${carrito.vacio} */
    public boolean isVacio() {
        return items.isEmpty();
    }
}