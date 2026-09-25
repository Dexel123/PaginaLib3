package org.paginalib3.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.paginalib3.dao.EditorialDAO;
import org.paginalib3.model.Editorial;
import org.paginalib3.util.Conexion;

public class EditorialDAOImpl implements EditorialDAO {

    @Override
    public List<Editorial> listar() throws SQLException {

        List<Editorial> lista = new ArrayList<>();

        String sql = """
                SELECT nit, nombre
                FROM editoriales
                WHERE activo = TRUE
                ORDER BY nombre
                """;

        try (
                Connection c = Conexion.getInstancia().conectar(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(
                        new Editorial(
                                rs.getString("nit"),
                                rs.getString("nombre")
                        )
                );
            }
        }

        return lista;
    }

    @Override
    public boolean existe(String nit) throws SQLException {

        String sql = """
                SELECT 1
                FROM editoriales
                WHERE nit = ?
                  AND activo = TRUE
                LIMIT 1
                """;

        try (
                Connection c = Conexion.getInstancia().conectar(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nit == null ? "" : nit.trim());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
