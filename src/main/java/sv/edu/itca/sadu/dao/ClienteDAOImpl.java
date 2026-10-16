package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.Cliente;
import sv.edu.itca.sadu.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public Cliente obtenerPorIdUsuario(Long idUsuario) {
        Cliente c = null;
        String sql = "SELECT u.id_usuario, u.nombre_completo, u.correo, u.rol, u.activo AS u_activo, " +
                     "c.id_cliente, c.telefono, c.direccion, c.activo AS c_activo " +
                     "FROM usuarios u INNER JOIN clientes c ON u.id_usuario = c.id_usuario " +
                     "WHERE u.id_usuario = ?";
        
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = new Cliente();
                    c.setIdUsuario(rs.getLong("id_usuario"));
                    c.setNombreCompleto(rs.getString("nombre_completo"));
                    c.setCorreo(rs.getString("correo"));
                    c.setRol(rs.getString("rol"));
                    c.setActivo(rs.getBoolean("u_activo"));
                    c.setIdCliente(rs.getLong("id_cliente"));
                    c.setTelefono(rs.getString("telefono"));
                    c.setDireccion(rs.getString("direccion"));
                    c.setClienteActivo(rs.getBoolean("c_activo"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public boolean actualizarPerfil(Cliente cliente) {
        // Actualiza ambas tablas
        String sqlUsuario = "UPDATE usuarios SET nombre_completo = ?, correo = ? WHERE id_usuario = ?";
        String sqlCliente = "UPDATE clientes SET telefono = ?, direccion = ? WHERE id_usuario = ?";
        boolean exito = false;
        
        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false); //Transacción para consistencia
            
            try (PreparedStatement psU = con.prepareStatement(sqlUsuario);
                 PreparedStatement psC = con.prepareStatement(sqlCliente)) {
                
                psU.setString(1, cliente.getNombreCompleto());
                psU.setString(2, cliente.getCorreo());
                psU.setLong(3, cliente.getIdUsuario());
                psU.executeUpdate();
                
                psC.setString(1, cliente.getTelefono());
                psC.setString(2, cliente.getDireccion());
                psC.setLong(3, cliente.getIdUsuario());
                psC.executeUpdate();
                
                con.commit();
                exito = true;
            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exito;
    }

    @Override
    public List<Cliente> obtenerTodosLosClientes() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre_completo, u.correo, u.activo AS u_activo, " +
                     "c.id_cliente, c.telefono, c.direccion " +
                     "FROM usuarios u INNER JOIN clientes c ON u.id_usuario = c.id_usuario " +
                     "WHERE u.rol = 'CLIENTE' ORDER BY u.id_usuario ASC";
                     
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdUsuario(rs.getLong("id_usuario"));
                c.setNombreCompleto(rs.getString("nombre_completo"));
                c.setCorreo(rs.getString("correo"));
                c.setActivo(rs.getBoolean("u_activo"));
                c.setIdCliente(rs.getLong("id_cliente"));
                c.setTelefono(rs.getString("telefono"));
                c.setDireccion(rs.getString("direccion"));
                lista.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean desactivarCuenta(Long idUsuario) {
        String sql = "UPDATE usuarios SET activo = 0 WHERE id_usuario = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean activarCuenta(Long idUsuario) {
        String sql = "UPDATE usuarios SET activo = 1 WHERE id_usuario = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}