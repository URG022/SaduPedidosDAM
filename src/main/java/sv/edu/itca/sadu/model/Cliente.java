package sv.edu.itca.sadu.model;

public class Cliente extends Usuario {

    private Long idCliente;
    private String telefono;
    private String direccion;
    private boolean clienteActivo;

    public Cliente() {
        super();
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isClienteActivo() {
        return clienteActivo;
    }

    public void setClienteActivo(boolean clienteActivo) {
        this.clienteActivo = clienteActivo;
    }
}
