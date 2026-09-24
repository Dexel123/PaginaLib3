package org.paginalib3.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.paginalib3.dao.ReporteDAO;
import org.paginalib3.model.ReporteLibroVendido;
import org.paginalib3.model.ReporteVenta;
import org.paginalib3.model.StockValorizado;
import org.paginalib3.util.Conexion;

public class ReporteDAOImpl implements ReporteDAO {

    @Override
    public List<ReporteVenta> ventasPorPeriodo(LocalDate desde, LocalDate hasta) throws SQLException {
        validarFechas(desde, hasta);
        List<ReporteVenta> lista = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall("{CALL sp_reporte_ventas_periodo(?,?)}")) {
            s.setDate(1, Date.valueOf(desde));
            s.setDate(2, Date.valueOf(hasta));
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    lista.add(new ReporteVenta(r.getDate("fecha").toLocalDate(), r.getInt("cantidad_ventas"),
                            r.getDouble("subtotal"), r.getDouble("descuentos"), r.getDouble("total")));
                }
            }
        }
        return lista;
    }

    @Override
    public List<ReporteLibroVendido> librosMasVendidos(LocalDate desde, LocalDate hasta, int limite) throws SQLException {
        validarFechas(desde, hasta);
        List<ReporteLibroVendido> lista = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall("{CALL sp_reporte_libros_mas_vendidos(?,?,?)}")) {
            s.setDate(1, Date.valueOf(desde));
            s.setDate(2, Date.valueOf(hasta));
            s.setInt(3, limite);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    lista.add(new ReporteLibroVendido(r.getString("isbn"), r.getString("titulo"),
                            r.getInt("unidades_netas"), r.getDouble("ingresos_brutos")));
                }
            }
        }
        return lista;
    }

    @Override
    public List<StockValorizado> stockValorizado() throws SQLException {
        List<StockValorizado> lista = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall("{CALL sp_reporte_stock_valorizado()}"); ResultSet r = s.executeQuery()) {
            while (r.next()) {
                lista.add(new StockValorizado(r.getString("isbn"), r.getString("titulo"), r.getInt("stock_actual"),
                        r.getDouble("costo_promedio"), r.getDouble("precio"), r.getDouble("valor_costo"),
                        r.getDouble("valor_venta"), r.getDouble("margen_potencial")));
            }
        }
        return lista;
    }

    private void validarFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Selecciona ambas fechas.");
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final.");
        }
    }
}
