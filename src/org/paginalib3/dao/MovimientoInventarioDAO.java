package org.paginalib3.dao;

import java.sql.SQLException;
import java.util.List;
import org.paginalib3.model.MovimientoInventario;

public interface MovimientoInventarioDAO {

    boolean registrarIngreso(String isbn, int cantidad, int idUsuario, String observacion) throws SQLException;

    boolean registrarSalida(String isbn, int cantidad, String tipo, int idUsuario, String observacion) throws SQLException;

    List<MovimientoInventario> listar() throws SQLException;

    List<MovimientoInventario> listarPorLibro(String isbn) throws SQLException;
}
