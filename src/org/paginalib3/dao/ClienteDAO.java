package org.paginalib3.dao;

import org.paginalib3.model.Cliente;
import java.sql.SQLException;
import java.util.List;

public interface ClienteDAO {

    List<Cliente> listar() throws SQLException;

    Cliente buscarPorCui(String cui) throws SQLException;
    
}