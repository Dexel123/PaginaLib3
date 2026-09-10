package org.paginalib3.dao.impl;

import org.paginalib3.dao.LibroDAO;
import org.paginalib3.model.Libro;
import org.paginalib3.util.Conexion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroDAOImpl implements LibroDAO {

    @Override
    public List<Libro> listar() throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String sql = "{CALL sp_listarlibros()}";

        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall(sql);
             ResultSet r = s.executeQuery()) {
            while (r.next()) {
                lista.add(map(r));
            }
        }
        return lista;
    }

    @Override
    public List<Libro> buscar(String texto) throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String t = (texto == null) ? "" : texto.trim();
        
        if (t.isEmpty()) {
            return listar();
        }

        String sql = "{CALL sp_buscar_libros(?)}";
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall(sql)) {
            s.setString(1, t);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    lista.add(map(r));
                }
            }
        }
        return lista;
    }

    private Libro map(ResultSet r) throws SQLException {
        return new Libro(
            r.getString("isbn"),
            r.getString("título"),
            r.getDouble("precio"),
            r.getInt("stock_actual"),
            r.getInt("stock_minimo"),
            r.getBoolean("activo"),
            has(r, "autores") ? r.getString("autores") : ""
        );
    }

    private boolean has(ResultSet r, String n) {
        try {
            r.findColumn(n);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}