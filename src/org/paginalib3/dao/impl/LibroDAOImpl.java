package org.paginalib3.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.paginalib3.dao.LibroDAO;
import org.paginalib3.model.Libro;
import org.paginalib3.util.Conexion;

public class LibroDAOImpl implements LibroDAO {

    @Override
    public List<Libro> listar() throws SQLException {
        List<Libro> activos = new ArrayList<>();
        for (Libro libro : listarTodos()) {
            if (libro.isActivo()) activos.add(libro);
        }
        return activos;
    }

    @Override
    public List<Libro> listarTodos() throws SQLException {
        List<Libro> lista = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_listarlibros()}");
             ResultSet r = s.executeQuery()) {
            while (r.next()) lista.add(map(r));
        }
        return lista;
    }

    @Override
    public List<Libro> buscar(String texto) throws SQLException {
        String t = texto == null ? "" : texto.trim();
        if (t.isEmpty()) return listar();
        List<Libro> lista = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_buscar_libros(?)}")) {
            s.setString(1, t);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) lista.add(map(r));
            }
        }
        return lista;
    }

    @Override
    public Libro buscarPorIsbn(String isbn) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_buscarlibro(?)}")) {
            s.setString(1, isbn);
            try (ResultSet r = s.executeQuery()) {
                return r.next() ? map(r) : null; 
            }
        }
    }
 
    @Override
    public boolean insertar(Libro libro) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar()) {
            c.setAutoCommit(false);
            try (CallableStatement s = c.prepareCall("{CALL sp_insertarlibro(?,?,?,?,?,?)}")) {
                cargarDatosLibro(s, libro);
                s.executeUpdate();
            }
            try (CallableStatement s = c.prepareCall("{CALL sp_actualizarstocklibro(?,?,?)}")) {
                s.setString(1, libro.getIsbn());
                s.setInt(2, libro.getStockMinimo());
                s.setBoolean(3, libro.isActivo());
                s.executeUpdate(); 
            }
            c.commit();
            return true;
        }
    }
 
    @Override
    public boolean actualizar(Libro libro) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar()) {
            c.setAutoCommit(false);
            try {
                try (CallableStatement s = c.prepareCall("{CALL sp_actualizarlibro(?,?,?,?,?,?)}")) {
                    cargarDatosLibro(s, libro);
                    s.executeUpdate();
                }
                try (CallableStatement s = c.prepareCall("{CALL sp_actualizarstocklibro(?,?,?)}")) {
                    s.setString(1, libro.getIsbn());
                    s.setInt(2, libro.getStockMinimo());
                    s.setBoolean(3, libro.isActivo());
                    s.executeUpdate();
                }
                c.commit();
                return true;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    @Override
    public boolean cambiarEstado(String isbn, int stockMinimo, boolean activo) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_actualizarstocklibro(?,?,?)}")) {
            s.setString(1, isbn);
            s.setInt(2, stockMinimo);
            s.setBoolean(3, activo);
            return s.executeUpdate() >= 0;
        }
    }

    private void cargarDatosLibro(CallableStatement s, Libro libro) throws SQLException {
        s.setString(1, libro.getIsbn());
        s.setString(2, libro.getTitulo());
        if (libro.getFechaPublicacion() == null) s.setNull(3, Types.DATE);
        else s.setDate(3, Date.valueOf(libro.getFechaPublicacion()));
        s.setDouble(4, libro.getPrecio());
        s.setInt(5, libro.getIdCategoria());
        s.setString(6, libro.getNitEditorial());
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

    private Libro map(ResultSet r) throws SQLException {
        Date fecha = has(r, "fecha_publicacion") ? r.getDate("fecha_publicacion") : null;
        return new Libro(
                r.getString("isbn"),
                r.getString("titulo"),
                fecha == null ? null : fecha.toLocalDate(),
                has(r, "precio") ? r.getDouble("precio") : 0,
                has(r, "id_categoria") ? r.getInt("id_categoria") : 0,
                has(r, "nit_editorial") ? r.getString("nit_editorial") : null,
                has(r, "stock_actual") ? r.getInt("stock_actual") : 0,
                has(r, "stock_minimo") ? r.getInt("stock_minimo") : 0,
                !has(r, "activo") || r.getBoolean("activo"),
                has(r, "autores") ? r.getString("autores") : "",
                has(r, "nombre_categoria") ? r.getString("nombre_categoria") : null
        );
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
