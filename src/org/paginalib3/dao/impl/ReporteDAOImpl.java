package org.paginalib3.dao.impl;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import org.paginalib3.dao.ReporteDAO;
import org.paginalib3.model.ReporteVenta;
import org.paginalib3.util.Conexion;

public class ReporteDAOImpl implements ReporteDAO {

    public List<ReporteVenta> ventasPorPeriodo(LocalDate desde, LocalDate hasta) throws SQLException {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Selecciona ambas fechas.");
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final.");
        }
        List<ReporteVenta> lista = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall("{CALL sp_reporte_ventas_periodo(?,?)}")) {
            s.setDate(1, java.sql.Date.valueOf(desde));
            s.setDate(2, java.sql.Date.valueOf(hasta));
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    lista.add(new ReporteVenta(r.getDate("fecha").toLocalDate(), r.getInt("cantidad_ventas"), r.getDouble("subtotal"), r.getDouble("descuentos"), r.getDouble("total")));
                }
            }
        }
        return lista;
    }
}