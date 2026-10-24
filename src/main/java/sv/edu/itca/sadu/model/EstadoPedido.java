package sv.edu.itca.sadu.model;

public enum EstadoPedido {
    RECIBIDO("Recibido"),
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    EN_CAMINO("En camino"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado");

    private final String etiqueta;

    EstadoPedido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}