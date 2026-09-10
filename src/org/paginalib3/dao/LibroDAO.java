package org.paginalib3.dao;

import org.paginalib3.model.Libro;

import java.sql.SQLException;
import java.util.List;

public interface LibroDAO {

    List<Libro> listar() throws SQLException;

    List<Libro> buscar(String texto) throws SQLException;
    
}