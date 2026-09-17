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

    @Override
    public List<MovimientoInventario> listar() throws SQLException {
        List<MovimientoInventario> lista = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_listarmovimientosinventario()}");
             ResultSet r = s.executeQuery()) {
            while (r.next()) {
                lista.add(map(r));
            }
        }
        return lista;
    }

    @Override
    public List<MovimientoInventario> listarPorLibro(String isbn) throws SQLException {
        List<MovimientoInventario> lista = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_movimientosporlibro(?)}")) {
            s.setString(1, isbn);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    lista.add(map(r));
                }
            }
        }
        return lista;
    }

    private MovimientoInventario map(ResultSet r) throws SQLException {
        Timestamp fecha = r.getTimestamp("fecha_movimiento");
        Integer idVenta = null;
        int valorVenta = r.getInt("id_venta");
        if (!r.wasNull()) {
            idVenta = valorVenta;
        }
        return new MovimientoInventario(
                r.getInt("id_movimiento"),
                r.getString("isbn"),
                has(r, "titulo") ? r.getString("titulo") : "",
                r.getString("tipo_movimiento"),
                r.getInt("cantidad"),
                fecha == null ? null : fecha.toLocalDateTime(),
                r.getInt("id_usuario"),
                has(r, "username") ? r.getString("username") : "",
                idVenta,
                r.getString("nit_proveedor"),
                r.getString("observacion")
        );
    }

    private String limpiar(String texto) {
        if (texto == null) {
            return null;
        }
        String t = texto.trim();
        return t.isEmpty() ? null : t;
    }

    private boolean has(ResultSet r, String nombre) {
        try {
            r.findColumn(nombre);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}