package sv.edu.itca.sadu.service;

import sv.edu.itca.sadu.dao.UsuarioDAO;
import sv.edu.itca.sadu.model.Usuario;
import sv.edu.itca.sadu.util.Seguridad;

/** Valida correo y contrasena. No conoce HTTP ni la sesion. */
public class ServicioAutenticacion {

    private final UsuarioDAO usuarioDAO;

    public ServicioAutenticacion(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /** Devuelve el usuario sin la contrasena, o null si los datos no coinciden. */
    public Usuario autenticar(String correo, String clave) {
        if (correo == null || clave == null || correo.isEmpty() || clave.isEmpty()) {
            return null;
        }
        Usuario u = usuarioDAO.buscarActivoPorCorreo(correo.trim().toLowerCase());
        if (u == null || !u.getHashClave().equals(Seguridad.hash(clave))) {
            return null;
        }
        u.setHashClave(null);   // nunca se guarda el hash en la sesion
        return u;
    }
}
