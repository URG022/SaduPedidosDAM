package sv.edu.itca.sadu.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import sv.edu.itca.sadu.model.EstadoPedido;

public class SeguimientoPedido {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Long idSeguimiento;
    private Long idPedido;
    private EstadoPedido estado;
    private String comentario;
    private Long idUsuario;
    private LocalDateTime fechaRegistro;

    public SeguimientoPedido() {
    }
    
    public String getFechaRegistroTexto() {
        return fechaRegistro == null ? "" : FORMATO.format(fechaRegistro);
    }

    public Long getIdSeguimiento() {
        return idSeguimiento;
    }

    public void setIdSeguimiento(Long idSeguimiento) {
        this.idSeguimiento = idSeguimiento;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
