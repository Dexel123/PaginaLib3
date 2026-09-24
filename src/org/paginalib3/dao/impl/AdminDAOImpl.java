package org.paginalib3.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.paginalib3.dao.AdminDAO;
import org.paginalib3.model.DashboardIndicadores;
import org.paginalib3.util.Conexion;

public class AdminDAOImpl implements AdminDAO {

    @Override
    public DashboardIndicadores obtenerIndicadores() throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall("{CALL sp_dashboardadmin()}"); ResultSet r = s.executeQuery()) {
            DashboardIndicadores d = new DashboardIndicadores();
            if (r.next()) {
                d.setVentasTotales(r.getDouble("ventas_totales"));
                d.setVentasHoy(r.getDouble("ventas_hoy"));
                d.setTransaccionesHoy(r.getInt("transacciones_hoy"));
                d.setLibrosActivos(r.getInt("libros_activos"));
                d.setUnidadesEnStock(r.getInt("unidades_en_stock"));
                d.setInventarioValorizadoCosto(r.getDouble("inventario_valorizado_costo"));
                d.setUsuariosActivos(r.getInt("usuarios_activos"));
                d.setLibrosStockCritico(r.getInt("libros_stock_critico"));
            }
            return d;
        }
    }
}
       