package org.paginalib3.dao.impl;

import org.paginalib3.dao.InventarioDAO;
import org.paginalib3.util.Conexion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class InventarioDAOImpl implements InventarioDAO {

    @Override
    public void registrarSalida(String isbn, int cantidad, String tipo, int idUsuario,
                                String observacion, String nitProveedor) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_registrar_salida_inventario(?,?,?,?,?,?)}")) {
            s.setString(1, isbn);
            s.setInt(2, cantidad);
            s.setString(3, tipo);
            s.setInt(4, idUsuario);
            s.setString(5, observacion);
            if (nitProveedor == null || nitProveedor.isBlank()) {
                s.setNull(6, java.sql.Types.VARCHAR);
            } else {
                s.setString(6, nitProveedor);
            }
            s.execute();
        }
    }
}

