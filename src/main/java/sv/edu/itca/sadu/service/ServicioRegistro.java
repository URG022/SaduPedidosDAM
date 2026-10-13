package sv.edu.itca.sadu.service;

import java.util.ArrayList;
import java.util.List;
import sv.edu.itca.sadu.dao.UsuarioDAO;
import sv.edu.itca.sadu.model.Usuario;
import sv.edu.itca.sadu.util.Seguridad;

/** Reglas para crear la cuenta de un cliente nuevo. */
public class ServicioRegistro {

    private final UsuarioDAO usuarioDAO;

    public ServicioRegistro(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /** Devuelve la lista de errores; vacia si todo es valido. */
    public List<String> validar(String nombre, String correo, String telefono, String clave, String confirmacion) {
        List<String> errores = new ArrayList<>();
        if (nombre == null || nombre.trim().length() < 3) {
            errores.add("Escribe tu nombre completo.");
        }
        if (correo == null || !correo.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            errores.add("Escribe un correo válido.");
        } else if (usuarioDAO.existeCorreo(correo.trim().toLowerCase())) {
            errores.add("Ese correo ya tiene una cuenta. Inicia sesión.");
        }
        if (telefono == null || !telefono.matches("^[267]\\d{3}-?\\d{4}$")) {
            errores.add("El teléfono debe tener 8 dígitos, por ejemplo 7012-3456.");
        }
        if (clave == null || clave.length() < 8 || !clave.matches(".*\\d.*")) {
            errores.add("La contraseña debe tener al menos 8 caracteres y un número.");
        } else if (!clave.equals(confirmacion)) {
            errores.add("Las contraseñas no coinciden.");
        }
        return errores;
    }

    public Usuario registrar(String nombre, String correo, String telefono, String direccion, String clave) {
        Usuario u = new Usuario();
        u.setNombreCompleto(nombre.trim());
        u.setCorreo(correo.trim().toLowerCase());
        u.setHashClave(Seguridad.hash(clave));
        String tel = telefono.replace("-", "");
        tel = tel.substring(0, 4) + "-" + tel.substring(4);
        Usuario creado = usuarioDAO.registrarCliente(u, tel,
                direccion == null || direccion.trim().isEmpty() ? null : direccion.trim());
        creado.setHashClave(null);
        return creado;
    }
}
