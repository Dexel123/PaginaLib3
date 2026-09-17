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

