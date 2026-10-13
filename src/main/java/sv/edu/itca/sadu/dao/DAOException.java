package sv.edu.itca.sadu.dao;

/**
 * Error de acceso a datos. Envuelve SQLException para que los
 * Servlets y servicios no dependan de JDBC.
 */
public class DAOException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
