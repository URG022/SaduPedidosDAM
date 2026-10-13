package sv.edu.itca.sadu.model;

import java.io.Serializable;

/**
 * Usuario que inicia sesion. Se guarda en la sesion con el nombre
 * "usuarioAutenticado" (sin la contrasena).
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idUsuario;
    private String nombreCompleto;
    private String correo;
    private String hashClave;
    private String rol;
    private boolean activo;
    private Long idCliente;   // solo para rol CLIENTE

    public boolean isCliente() { return Rol.CLIENTE.equals(rol); }
    public boolean isAdministrador() { return Rol.ADMINISTRADOR.equals(rol); }
    public boolean isCocina() { return Rol.COCINA.equals(rol); }

    /** Primer nombre para saludar: "Hola, Ana". */
    public String getPrimerNombre() {
        return nombreCompleto == null ? "" : nombreCompleto.trim().split("\\s+")[0];
    }

    public long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(long idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getHashClave() { return hashClave; }
    public void setHashClave(String hashClave) { this.hashClave = hashClave; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
}
