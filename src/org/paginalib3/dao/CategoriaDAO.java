package org.paginalib3.dao;

import java.sql.SQLException;
import java.util.List;
import org.paginalib3.model.Categoria;

public interface CategoriaDAO {
    List<Categoria> listar() throws SQLException;
    boolean insertar(String nombre) throws SQLException;
    boolean actualizar(int id, String nombre) throws SQLException;
    boolean eliminar(int id) throws SQLException;
}
