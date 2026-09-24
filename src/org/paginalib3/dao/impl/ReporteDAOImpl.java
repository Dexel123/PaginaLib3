package org.paginalib3.dao.impl;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import org.paginalib3.dao.ReporteDAO;
import org.paginalib3.model.*;
import org.paginalib3.util.Conexion;
 
public class ReporteDAOImpl implements ReporteDAO {
  
    public List<ReporteLibroVendido> librosMasVendidos(LocalDate desde, LocalDate hasta, int limite) throws SQLException {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Selecciona ambas fechas.");
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final.");
        }
        List<ReporteLibroVendido> lista = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall("{CALL sp_reporte_libros_mas_vendidos(?,?,?)}")) {
            s.setDate(1, Date.valueOf(desde));
            s.setDate(2, Date.valueOf(hasta));
            s.setInt(3, limite);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    lista.add(new ReporteLibroVendido(r.getString("isbn"), r.getString("titulo"), r.getInt("unidades_netas"), r.getDouble("ingresos_brutos")));
                }
            }
        }
        return lista;
    }

    public List<StockValorizado> stockValorizado() throws SQLException {
        List<StockValorizado> lista = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall("{CALL sp_reporte_stock_valorizado()}"); ResultSet r = s.executeQuery()) {
            while (r.next()) {
                lista.add(new StockValorizado(r.getString("isbn"), r.getString("titulo"), r.getInt("stock_actual"), r.getDouble("costo_promedio"), r.getDouble("precio"), r.getDouble("valor_costo"), r.getDouble("valor_venta"), r.getDouble("margen_potencial")));
            }
        }
        return lista;
    }
}
