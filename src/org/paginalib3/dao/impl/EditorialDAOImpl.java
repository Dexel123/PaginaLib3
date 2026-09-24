package org.paginalib3.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.paginalib3.dao.EditorialDAO;
import org.paginalib3.model.Editorial;
import org.paginalib3.util.Conexion;

public class EditorialDAOImpl implements EditorialDAO {

    @Override
    public List<Editorial> listarActivas() throws SQLException {
        List<Editorial> lista = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall("{CALL sp_listareditoriales()}"); ResultSet r = s.executeQuery()) {
            while (r.next()) {
                boolean activo = true;
                try {
                    activo = r.getBoolean("activo");
                } catch (SQLException ignorada) {
                  
                }
                if (activo) {
                    lista.add(new Editorial(
                            r.getString("nit"),
                            r.getString("nombre_editorial"),
                            true));
                }
            }
        }
        return lista;
    }
}
