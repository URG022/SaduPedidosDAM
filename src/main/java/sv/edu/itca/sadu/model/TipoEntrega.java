package sv.edu.itca.sadu.model;

public enum TipoEntrega {
    EN_LOCAL("En local"),
    PARA_LLEVAR("Para llevar"),
    DOMICILIO("A domicilio");

    private final String etiqueta;

    TipoEntrega(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}