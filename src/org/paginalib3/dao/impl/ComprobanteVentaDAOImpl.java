package org.paginalib3.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.paginalib3.dao.ComprobanteVentaDAO;
import org.paginalib3.model.DetalleVenta;
import org.paginalib3.model.Venta;
import org.paginalib3.util.Conexion;

public class ComprobanteVentaDAOImpl implements ComprobanteVentaDAO {
    @Override
    public Venta buscarVenta(int idVenta) throws SQLException {
        if (idVenta <= 0) throw new IllegalArgumentException("El ID de venta debe ser mayor que cero.");
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_buscarventa(?)}")) {
            s.setInt(1, idVenta);
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    Timestamp t = r.getTimestamp("fecha_venta");
                    return new Venta(r.getInt("id_venta"), t == null ? null : t.toLocalDateTime(),
                            r.getDouble("subtotal"), r.getDouble("descuento"), r.getDouble("total"),
                            r.getString("estado"), r.getString("cui_cliente"), r.getInt("id_usuario"));
                }
            }
        }
        return null;
    }

    @Override
    public List<DetalleVenta> listarDetalles(int idVenta) throws SQLException {
        if (idVenta <= 0) throw new IllegalArgumentException("El ID de venta debe ser mayor que cero.");
        List<DetalleVenta> detalles = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_listardetalleventa()}");
             ResultSet r = s.executeQuery()) {
            while (r.next()) {
                if (r.getInt("id_venta") == idVenta) {
                    detalles.add(new DetalleVenta(r.getInt("id_detalle"), r.getInt("id_venta"),
                            r.getString("isbn"), r.getInt("cantidad"), r.getDouble("precio_unitario"), r.getDouble("subtotal")));
                }
            }
        }
        return detalles;
    }
}
