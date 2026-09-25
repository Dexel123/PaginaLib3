package org.paginalib3.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.paginalib3.dao.CategoriaDAO;
import org.paginalib3.model.Categoria;
import org.paginalib3.util.Conexion;

public class CategoriaDAOImpl implements CategoriaDAO {
    @Override
    public List<Categoria> listar() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_listarcategorias()}");
             ResultSet r = s.executeQuery()) {
            while (r.next()) lista.add(new Categoria(r.getInt("id_categoria"), r.getString("nombre_categoria")));
        }
        return lista;
    }

    @Override
    public boolean insertar(String nombre) throws SQLException {
        validarNombre(nombre);
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_insertarcategoria(?)}")) {
            s.setString(1, nombre.trim()); s.execute(); return true;
        }
    }

    @Override
    public boolean actualizar(int id, String nombre) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Categoría inválida.");
        validarNombre(nombre);
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_actualizarcategoria(?,?)}")) {
            s.setInt(1, id); s.setString(2, nombre.trim()); s.execute(); return true;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Categoría inválida.");
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_eliminarcategoria(?)}")) {
            s.setInt(1, id); s.execute(); return true;
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        if (nombre.trim().length() > 100) throw new IllegalArgumentException("El nombre de la categoría es demasiado largo.");
    }
}
