package org.paginalib3.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.paginalib3.dao.LibroDAO;
import org.paginalib3.model.Libro;
import org.paginalib3.util.Conexion;

public class LibroDAOImpl implements LibroDAO {

    @Override
    public List<Libro> listar() throws SQLException {

        List<Libro> lista = new ArrayList<>();

        String sql = "{CALL sp_listarlibros_activos()}";

        try (
                Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall(sql); ResultSet rs = s.executeQuery()) {

            while (rs.next()) {
                lista.add(cargarDatosLibro(rs));
            }
        }

        return lista;
    }

    @Override
    public List<Libro> listarTodos() throws SQLException {

        List<Libro> lista = new ArrayList<>();

        String sql = "{CALL sp_listarlibros()}";

        try (
                Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall(sql); ResultSet rs = s.executeQuery()) {

            while (rs.next()) {
                lista.add(cargarDatosLibro(rs));
            }
        }

        return lista;
    }

    @Override
    public List<Libro> buscar(String texto) throws SQLException {

        List<Libro> lista = new ArrayList<>();

        String sql = "{CALL sp_buscar_libros(?)}";

        try (
                Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall(sql)) {

            s.setString(1, texto);

            try (ResultSet rs = s.executeQuery()) {

                while (rs.next()) {
                    lista.add(cargarDatosLibro(rs));
                }
            }
        }

        return lista;
    }

    @Override
    public Libro buscarPorIsbn(String isbn) throws SQLException {

        String sql = "{CALL sp_buscarlibro(?)}";

        try (
                Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall(sql)) {

            s.setString(1, isbn);

            try (ResultSet rs = s.executeQuery()) {

                if (rs.next()) {
                    return cargarDatosLibro(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean insertar(Libro libro) throws SQLException {

        String sql = "{CALL sp_insertarlibro(?,?,?,?,?,?)}";

        try (
                Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall(sql)) {

            s.setString(1, libro.getIsbn());
            s.setString(2, libro.getTitulo());

            if (libro.getFechaPublicacion() == null) {
                s.setNull(3, java.sql.Types.DATE);
            } else {
                s.setDate(
                        3,
                        Date.valueOf(libro.getFechaPublicacion())
                );
            }

            s.setDouble(4, libro.getPrecio());
            s.setInt(5, libro.getIdCategoria());
            s.setString(6, libro.getNitEditorial());

            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean insertarConStockInicial(
            Libro libro,
            int stockInicial,
            int idUsuario
    ) throws SQLException {

        String sql
                = "{CALL sp_insertarlibro_stock_inicial(?,?,?,?,?,?,?,?,?)}";

        try (
                Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall(sql)) {

            s.setString(1, libro.getIsbn());
            s.setString(2, libro.getTitulo());

            if (libro.getFechaPublicacion() == null) {
                s.setNull(3, java.sql.Types.DATE);
            } else {
                s.setDate(
                        3,
                        Date.valueOf(
                                libro.getFechaPublicacion()
                        )
                );
            }

            s.setDouble(4, libro.getPrecio());
            s.setInt(5, libro.getIdCategoria());
            s.setString(6, libro.getNitEditorial());
            s.setInt(7, stockInicial);
            s.setInt(8, libro.getStockMinimo());
            s.setInt(9, idUsuario);

            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean actualizar(Libro libro) throws SQLException {

        String sql = "{CALL sp_actualizarlibro(?,?,?,?,?,?)}";

        try (
                Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall(sql)) {

            s.setString(1, libro.getIsbn());
            s.setString(2, libro.getTitulo());

            if (libro.getFechaPublicacion() == null) {
                s.setNull(3, java.sql.Types.DATE);
            } else {
                s.setDate(
                        3,
                        Date.valueOf(
                                libro.getFechaPublicacion()
                        )
                );
            }

            s.setDouble(4, libro.getPrecio());
            s.setInt(5, libro.getIdCategoria());
            s.setString(6, libro.getNitEditorial());

            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean cambiarEstado(
            String isbn,
            boolean activo
    ) throws SQLException {

        String sql
                = "{CALL sp_cambiar_estado_libro(?,?)}";

        try (
                Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall(sql)) {

            s.setString(1, isbn);
            s.setBoolean(2, activo);

            return s.executeUpdate() > 0;
        }
    }

    @Override
    public List<Libro> listarStockCritico()
            throws SQLException {

        List<Libro> lista = new ArrayList<>();

        String sql
                = "SELECT isbn, titulo, stock_actual, stock_minimo "
                + "FROM vw_stock_critico";

        try (
                Connection c = Conexion.getInstancia().conectar(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Libro libro = new Libro();

                libro.setIsbn(
                        rs.getString("isbn")
                );

                libro.setTitulo(
                        rs.getString("titulo")
                );

                libro.setStockActual(
                        rs.getInt("stock_actual")
                );

                libro.setStockMinimo(
                        rs.getInt("stock_minimo")
                );

                lista.add(libro);
            }
        }

        return lista;
    }

    private Libro cargarDatosLibro(
            ResultSet rs
    ) throws SQLException {

        Libro libro = new Libro();

        libro.setIsbn(
                rs.getString("isbn")
        );

        libro.setTitulo(
                rs.getString("titulo")
        );

        Date fecha
                = rs.getDate("fecha_publicacion");

        if (fecha != null) {
            libro.setFechaPublicacion(
                    fecha.toLocalDate()
            );
        }

        libro.setPrecio(
                rs.getDouble("precio")
        );

        libro.setIdCategoria(
                rs.getInt("id_categoria")
        );

        libro.setNitEditorial(
                rs.getString("nit_editorial")
        );

        try {
            libro.setStockActual(
                    rs.getInt("stock_actual")
            );
        } catch (SQLException ignored) {
        }

        try {
            libro.setStockMinimo(
                    rs.getInt("stock_minimo")
            );
        } catch (SQLException ignored) {
        }

        try {
            libro.setActivo(
                    rs.getBoolean("activo")
            );
        } catch (SQLException ignored) {
        }

        try {
            libro.setAutores(
                    rs.getString("autores")
            );
        } catch (SQLException ignored) {
        }

        try {
            libro.setNombreCategoria(
                    rs.getString("nombre_categoria")
            );
        } catch (SQLException ignored) {
        }

        return libro;
    }
}
