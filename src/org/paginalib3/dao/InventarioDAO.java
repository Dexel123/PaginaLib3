package org.paginalib3.dao;

import java.sql.SQLException;

public interface InventarioDAO {

    void registrarSalida(String isbn, int cantidad, String tipo, int idUsuario,
                         String observacion, String nitProveedor) throws SQLException;
}
