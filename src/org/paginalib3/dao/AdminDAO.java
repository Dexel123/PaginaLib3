package org.paginalib3.dao;

import java.sql.SQLException;

public interface AdminDAO {

    boolean actualizarPrecio(String isbn, double nuevoPrecio, int idUsuario, String motivo) throws SQLException;
}
