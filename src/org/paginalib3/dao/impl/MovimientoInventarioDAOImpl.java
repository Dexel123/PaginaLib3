package org.paginalib3.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.paginalib3.dao.MovimientoInventarioDAO;
import org.paginalib3.model.MovimientoInventario;
import org.paginalib3.util.Conexion;

public class MovimientoInventarioDAOImpl implements MovimientoInventarioDAO {

    @Override
    public boolean registrarIngreso(String isbn, int cantidad, int idUsuario, String observacion) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_registrar_ingreso_inventario(?,?,?,?)}")) {
            s.setString(1, isbn);
            s.setInt(2, cantidad);
            s.setInt(3, idUsuario);
            s.setString(4, limpiar(observacion));
            s.execute();
            return true;
        }
    }

    @Override
    public boolean registrarSalida(String isbn, int cantidad, String tipo, int idUsuario, String observacion) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_registrar_salida_inventario(?,?,?,?,?)}")) {
            s.setString(1, isbn);
            s.setInt(2, cantidad);
            s.setString(3, tipo);
            s.setInt(4, idUsuario);
            s.setString(5, limpiar(observacion));
            s.execute();
            return true;
        }
    }
