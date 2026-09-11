package org.paginalib3.dao.impl;

import org.paginalib3.dao.DetalleVentaDAO;
import org.paginalib3.model.DetalleVenta;
import org.paginalib3.util.Conexion;
import java.sql.*;
import java.util.*;

public class DetalleVentaDAOImpl implements DetalleVentaDAO {

    @Override
    public List<DetalleVenta> listarPorVenta(int idVenta) throws SQLException {
        List<DetalleVenta> l = new ArrayList<>();
        String sql = "SELECT id_detalle, id_venta, isbn, cantidad, precio_unitario, subtotal FROM detalle_venta WHERE id_venta = ? ORDER BY id_detalle";
        
        try (Connection c = Conexion.getInstancia().conectar();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, idVenta);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    l.add(new DetalleVenta(
                            r.getInt(1),
                            r.getInt(2),
                            r.getString(3),
                            r.getInt(4),
                            r.getDouble(5),
                            r.getDouble(6)
                    ));
                }
            }
        }
        return l;
    }
}