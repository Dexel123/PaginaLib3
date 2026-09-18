package org.paginalib3.dao.impl;

import org.paginalib3.dao.ClienteDAO;
import org.paginalib3.model.Cliente;
import org.paginalib3.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public List<Cliente> listar() throws SQLException {
        List<Cliente> l = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_listarclientes()}");
             ResultSet r = s.executeQuery()) {
            while (r.next()) {
                l.add(map(r));
            }
        }
        return l;
    }

    @Override
    public Cliente buscarPorCui(String cui) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_buscarcliente(?)}")) {
            s.setLong(1, Long.parseLong(cui));
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    return map(r);
                }
            }
        }
        return null;
    }

    private Cliente map(ResultSet r) throws SQLException {
        return new Cliente(
                String.valueOf(r.getLong("cui")),
                r.getString("nombre_cliente"),
                r.getString("apellido_cliente"),
                r.getString("correo_electronico")
        );
    }
}