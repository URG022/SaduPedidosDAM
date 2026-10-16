package sv.edu.itca.sadu.dao;

import sv.edu.itca.sadu.model.Cliente;
import java.util.List;

public interface ClienteDAO {

    //Cliente
    Cliente obtenerPorIdUsuario(Long idUsuario);
    boolean actualizarPerfil(Cliente cliente);
    
    //ListadoAdministrador
    List<Cliente> obtenerTodosLosClientes();
    
    //Activar/desactivar cuenta (lógico)
    boolean desactivarCuenta(Long idUsuario);
    boolean activarCuenta(Long idUsuario);
    
}
