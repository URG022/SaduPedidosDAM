package sv.edu.itca.sadu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import sv.edu.itca.sadu.model.Rol;
import sv.edu.itca.sadu.model.Usuario;
import sv.edu.itca.sadu.util.ConexionBD;

public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SQL_BUSCAR =
            "SELECT u.id_usuario, u.nombre_completo, u.correo, u.hash_clave, u.rol, u.activo, c.id_cliente "
            + "FROM usuarios u LEFT JOIN clientes c ON c.id_usuario = u.id_usuario "
            + "WHERE u.correo = ? AND u.activo = TRUE";

    private static final String SQL_EXISTE = "SELECT 1 FROM usuarios WHERE correo = ?";

    private static final String SQL_INSERTAR_USUARIO =
            "INSERT INTO usuarios (nombre_completo, correo, hash_clave, rol) VALUES (?, ?, ?, ?)";

    private static final String SQL_INSERTAR_CLIENTE =
            "INSERT INTO clientes (id_usuario, telefono, direccion) VALUES (?, ?, ?)";

    @Override
    public Usuario buscarActivoPorCorreo(String correo) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_BUSCAR)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getLong("id_usuario"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setCorreo(rs.getString("correo"));
                u.setHashClave(rs.getString("hash_clave"));
                u.setRol(rs.getString("rol"));
                u.setActivo(rs.getBoolean("activo"));
                long idCliente = rs.getLong("id_cliente");
                u.setIdCliente(rs.wasNull() ? null : idCliente);
                return u;
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo consultar el usuario", e);
        }
    }

    @Override
    public boolean existeCorreo(String correo) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_EXISTE)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo verificar el correo", e);
        }
    }

    @Override
    public Usuario registrarCliente(Usuario u, String telefono, String direccion) {
        try (Connection cn = ConexionBD.obtenerConexion()) {
            cn.setAutoCommit(false);
            try (PreparedStatement psU = cn.prepareStatement(SQL_INSERTAR_USUARIO, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psC = cn.prepareStatement(SQL_INSERTAR_CLIENTE, Statement.RETURN_GENERATED_KEYS)) {

                psU.setString(1, u.getNombreCompleto());
                psU.setString(2, u.getCorreo());
                psU.setString(3, u.getHashClave());
                psU.setString(4, Rol.CLIENTE);
                psU.executeUpdate();
                try (ResultSet k = psU.getGeneratedKeys()) {
                    k.next();
                    u.setIdUsuario(k.getLong(1));
                }

                psC.setLong(1, u.getIdUsuario());
                psC.setString(2, telefono);
                psC.setString(3, direccion);
                psC.executeUpdate();
                try (ResultSet k = psC.getGeneratedKeys()) {
                    k.next();
                    u.setIdCliente(k.getLong(1));
                }

                cn.commit();
                u.setRol(Rol.CLIENTE);
                u.setActivo(true);
                return u;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo registrar el cliente", e);
        }
    }
}
