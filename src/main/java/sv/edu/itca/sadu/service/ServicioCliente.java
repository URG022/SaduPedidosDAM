package sv.edu.itca.sadu.service;

import sv.edu.itca.sadu.dao.ClienteDAO;
import sv.edu.itca.sadu.dao.ClienteDAOImpl;
import sv.edu.itca.sadu.model.Cliente;
import sv.edu.itca.sadu.util.Seguridad;

import java.util.ArrayList;
import java.util.List;

public class ServicioCliente {

    private ClienteDAO clienteDAO;

    public ServicioCliente() {
        this.clienteDAO = new ClienteDAOImpl();
    }

    public Cliente obtenerPerfil(Long idUsuario) {
        return clienteDAO.obtenerPorIdUsuario(idUsuario);
    }

    public boolean actualizarPerfil(Cliente cliente) {
        return clienteDAO.actualizarPerfil(cliente);
    }

    public List<Cliente> obtenerTodosLosClientes() {
        return clienteDAO.obtenerTodosLosClientes();
    }

    public boolean desactivarCuenta(Long idUsuario) {
        return clienteDAO.desactivarCuenta(idUsuario);
    }

    public boolean activarCuenta(Long idUsuario) {
        return clienteDAO.activarCuenta(idUsuario);
    }

    public boolean actualizarClave(Long idUsuario, String hashClave) {
        return clienteDAO.actualizarClave(idUsuario, hashClave);
    }

    /**
     * Valida los datos del perfil con las mismas reglas del registro y los deja limpios
     * (nombre sin espacios extra, correo en minúsculas, teléfono con formato 0000-0000).
     */
    public List<String> validarPerfil(Cliente c) {
        List<String> errores = new ArrayList<>();
        String nombre = c.getNombreCompleto() == null ? "" : c.getNombreCompleto().trim();
        String correo = c.getCorreo() == null ? "" : c.getCorreo().trim().toLowerCase();
        String telefono = c.getTelefono() == null ? "" : c.getTelefono().trim();

        if (nombre.length() < 3 || nombre.length() > 120) {
            errores.add("Escribe tu nombre completo (entre 3 y 120 caracteres).");
        }
        if (!correo.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            errores.add("Escribe un correo válido.");
        }
        if (!telefono.matches("^[267]\\d{3}-?\\d{4}$")) {
            errores.add("El teléfono debe tener 8 dígitos, por ejemplo 7012-3456.");
        } else {
            String tel = telefono.replace("-", "");
            telefono = tel.substring(0, 4) + "-" + tel.substring(4);
        }
        if (c.getDireccion() != null && c.getDireccion().trim().length() > 200) {
            errores.add("La dirección no puede tener más de 200 caracteres.");
        }

        c.setNombreCompleto(nombre);
        c.setCorreo(correo);
        c.setTelefono(telefono);
        c.setDireccion(c.getDireccion() == null || c.getDireccion().trim().isEmpty() ? null : c.getDireccion().trim());
        return errores;
    }

    /**
     * Revisa un cambio de contraseña. Devuelve la lista de errores (vacía si todo está bien).
     * Si los tres campos vienen vacíos, no hay cambio de contraseña y no hay errores.
     */
    public List<String> validarCambioClave(Long idUsuario, String actual, String nueva, String confirmar) {
        List<String> errores = new ArrayList<>();
        boolean quiereCambiar = !vacio(actual) || !vacio(nueva) || !vacio(confirmar);
        if (!quiereCambiar) {
            return errores;
        }
        String hashGuardado = clienteDAO.obtenerHashClave(idUsuario);
        if (vacio(actual) || hashGuardado == null || !hashGuardado.equals(Seguridad.hash(actual))) {
            errores.add("La contraseña actual no es correcta.");
        }
        if (nueva == null || nueva.length() < 8 || !nueva.matches(".*\\d.*")) {
            errores.add("La nueva contraseña debe tener al menos 8 caracteres y un número.");
        } else if (!nueva.equals(confirmar)) {
            errores.add("La nueva contraseña y su confirmación no coinciden.");
        }
        return errores;
    }

    /** Guarda la nueva contraseña (ya validada con validarCambioClave). */
    public boolean cambiarClave(Long idUsuario, String nueva) {
        return clienteDAO.actualizarClave(idUsuario, Seguridad.hash(nueva));
    }

    private boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }
}