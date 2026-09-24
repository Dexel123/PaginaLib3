package org.paginalib3.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.paginalib3.dao.AdminDAO;
import org.paginalib3.model.DashboardIndicadores;
import org.paginalib3.util.Conexion;

public class AdminDAOImpl implements AdminDAO {

    @Override
    public boolean actualizarPrecio(String isbn, double nuevoPrecio, int idUsuario, String motivo) throws SQLException {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("Selecciona un libro.");
        }
        if (nuevoPrecio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }

        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_actualizarpreciolibro(?,?,?,?)}")) {

            s.setString(1, isbn);
            s.setDouble(2, nuevoPrecio);
            s.setInt(3, idUsuario);
            s.setString(4, motivo == null || motivo.isBlank() ? "Actualización administrativa" : motivo.trim());

            s.execute();
            return true;
        }
    }
}
