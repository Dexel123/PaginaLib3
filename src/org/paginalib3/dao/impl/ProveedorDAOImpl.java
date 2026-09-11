package org.paginalib3.dao.impl;

import org.paginalib3.dao.ProveedorDAO;
import org.paginalib3.model.Proveedor;
import org.paginalib3.util.Conexion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAOImpl implements ProveedorDAO {

    @Override
    public List<Proveedor> listarActivos() throws SQLException {
        List<Proveedor> lista = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_listarproveedores()}");
             ResultSet r = s.executeQuery()) {
            while (r.next()) {
                if (r.getBoolean("activo")) {
                    lista.add(new Proveedor(
                            r.getString("nit_proveedor"),
                            r.getString("nombre_proveedor"),
                            true
                    ));
                }
            }
        }
        return lista;
    }
}
