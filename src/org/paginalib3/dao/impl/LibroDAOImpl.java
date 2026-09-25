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
        List<Libro> lista = new ArrayList<>();
        // Filtrado optimizado directamente desde la base de datos mediante procedimiento almacenado
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_listarlibros_activos()}");
             ResultSet r = s.executeQuery()) {

            while (r.next()) {
                lista.add(map(r));
            }
        } catch (SQLException e) {
            // Si no existe un SP dedicado a activos, realiza el filtrado con listarTodos()
            return listarTodos().stream()
                    .filter(Libro::isActivo)
                    .toList();
        }

        return lista;
    }

    @Override
    public List<Libro> listarTodos() throws SQLException {
        List<Libro> lista = new ArrayList<>();

        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_listarlibros()}");
             ResultSet r = s.executeQuery()) {

            while (r.next()) {
                lista.add(map(r));
            }
        }

        return lista;
    }

    @Override
    public List<Libro> buscar(String texto) throws SQLException {
        String t = texto == null ? "" : texto.trim();

        if (t.isEmpty()) {
            return listar();
        }

        List<Libro> lista = new ArrayList<>();

        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_buscar_libros(?)}")) {

            s.setString(1, t);

            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    lista.add(map(r));
                }
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
                if (r.next()) {
                    return map(r);
                }
            }
        }

        return null;
    }

    @Override
    public boolean insertar(Libro libro) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_insertarlibro(?,?,?,?,?,?)}")) {

            cargarDatosLibro(s, libro);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean insertarConStockInicial(Libro libro, int stockInicial, int idUsuario) throws SQLException {
        if (stockInicial < 0) {
            throw new IllegalArgumentException("El stock inicial no puede ser negativo.");
        }

        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_insertarlibro_stock_inicial(?,?,?,?,?,?,?,?)}")) {

            s.setString(1, libro.getIsbn());
            s.setString(2, libro.getTitulo());

            if (libro.getFechaPublicacion() == null) {
                s.setNull(3, Types.DATE);
            } else {
                s.setDate(3, Date.valueOf(libro.getFechaPublicacion()));
            }

            s.setDouble(4, libro.getPrecio());
            s.setInt(5, libro.getIdCategoria());
            s.setString(6, libro.getNitEditorial());
            s.setInt(7, stockInicial);
            s.setInt(8, idUsuario);

            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean actualizar(Libro libro) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_actualizarlibro(?,?,?,?,?,?)}")) {

            cargarDatosLibro(s, libro);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean cambiarEstado(String isbn, boolean activo) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_cambiar_estado_libro(?,?)}")) {

            s.setString(1, isbn);
            s.setBoolean(2, activo);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public List<Libro> listarStockCritico() throws SQLException {
        List<Libro> lista = new ArrayList<>();

        String sql = """
                SELECT
                    isbn,
                    titulo,
                    stock_actual,
                    stock_minimo,
                    nombre_categoria
                FROM vw_stock_critico
                ORDER BY stock_actual, titulo
                """;

        try (Connection c = Conexion.getInstancia().conectar();
             PreparedStatement s = c.prepareStatement(sql);
             ResultSet r = s.executeQuery()) {

            while (r.next()) {
                Libro libro = new Libro();
                libro.setIsbn(r.getString("isbn"));
                libro.setTitulo(r.getString("titulo"));
                libro.setStockActual(r.getInt("stock_actual"));
                libro.setStockMinimo(r.getInt("stock_minimo"));
                libro.setNombreCategoria(r.getString("nombre_categoria"));
                libro.setActivo(true);

                lista.add(libro);
            }
        }

        return lista;
    }

    private void cargarDatosLibro(CallableStatement s, Libro libro) throws SQLException {
        s.setString(1, libro.getIsbn());
        s.setString(2, libro.getTitulo());

        if (libro.getFechaPublicacion() == null) {
            s.setNull(3, Types.DATE);
        } else {
            s.setDate(3, Date.valueOf(libro.getFechaPublicacion()));
        }

        s.setDouble(4, libro.getPrecio());
        s.setInt(5, libro.getIdCategoria());
        s.setString(6, libro.getNitEditorial());
    }

    private Libro map(ResultSet r) throws SQLException {
        Date fecha = has(r, "fecha_publicacion") ? r.getDate("fecha_publicacion") : null;

        return new Libro(
                r.getString("isbn"),
                r.getString("titulo"),
                fecha == null ? null : fecha.toLocalDate(),
                has(r, "precio") ? r.getDouble("precio") : 0.0,
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