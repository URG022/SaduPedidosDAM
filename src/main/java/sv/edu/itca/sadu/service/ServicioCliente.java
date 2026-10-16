package sv.edu.itca.sadu.service;

import sv.edu.itca.sadu.dao.ClienteDAO;
import sv.edu.itca.sadu.dao.ClienteDAOImpl;
import sv.edu.itca.sadu.model.Cliente;
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
}