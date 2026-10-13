package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.Usuario;

public interface UsuarioDAO {

    /** Busca un usuario ACTIVO por correo (incluye idCliente si es cliente). Null si no existe. */
    Usuario buscarActivoPorCorreo(String correo);

    boolean existeCorreo(String correo);

    /**
     * Registra un cliente nuevo en UNA transaccion:
     * INSERT en usuarios (rol CLIENTE) + INSERT en clientes.
     * Devuelve el usuario con sus ids asignados.
     */
    Usuario registrarCliente(Usuario usuario, String telefono, String direccion);
}
