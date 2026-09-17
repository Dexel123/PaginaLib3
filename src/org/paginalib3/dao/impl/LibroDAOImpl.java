package org.paginalib3.dao.impl;

import org.paginalib3.dao.LibroDAO;
import org.paginalib3.model.Libro;
import org.paginalib3.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAOImpl implements LibroDAO {

    @Override
    public List<Libro> listar() throws SQLException {
        List<Libro> l = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_listarlibros()}");
             ResultSet r = s.executeQuery()) {
            while (r.next()) {
                l.add(map(r));
            }
        }
        return l;
    }

    @Override
    public List<Libro> buscar(String texto) throws SQLException {
        List<Libro> l = new ArrayList<>();
        String t = texto == null ? "" : texto.trim();
        if (t.isEmpty()) {
            return listar();
        }
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_buscar_libros(?)}")) {
            s.setString(1, t);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    l.add(map(r));
                }
            }
        }
        return l;
    }

    @Override
    public List<Libro> listarStockCritico() throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT isbn,titulo,stock_actual,stock_minimo FROM vw_stock_critico ORDER BY stock_actual,titulo";
        try (Connection c = Conexion.getInstancia().conectar(); PreparedStatement s = c.prepareStatement(sql); ResultSet r = s.executeQuery()) {
            while (r.next()) {
                Libro libro = new Libro(r.getString("isbn"), r.getString("titulo"), 0, r.getInt("stock_actual"), r.getInt("stock_minimo"), true, "");
                lista.add(libro);
            }
        }
        return lista;
    }
    