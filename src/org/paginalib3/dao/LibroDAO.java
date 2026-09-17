package org.paginalib3.dao;

import java.sql.SQLException;
import java.util.List;
import org.paginalib3.model.Libro;

public interface LibroDAO {
    List<Libro> listar() throws SQLException;
    List<Libro> buscar(String texto) throws SQLException;
    List<Libro> listarStockCritico() throws SQLException;
}
