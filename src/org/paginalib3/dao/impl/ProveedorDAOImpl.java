package org.paginalib3.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.paginalib3.dao.ProveedorDAO;
import org.paginalib3.model.Proveedor;
import org.paginalib3.util.Conexion;

public class ProveedorDAOImpl implements ProveedorDAO {

    @Override
    public List<Proveedor> listar() throws SQLException {
        List<Proveedor> lista = new ArrayList<>();
        
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_listarproveedores()}");
             ResultSet r = s.executeQuery()) {
            
            while (r.next()) {
                lista.add(new Proveedor(
                        r.getString("nit_proveedor"),
                        r.getString("nombre_proveedor"),
                        r.getString("telefono_proveedor"),
                        r.getString("direccion_proveedor"),
                        r.getString("correo_proveedor"),
                        r.getBoolean("activo")
                ));
            }
        }
        
        return lista;
    }

    @Override
    public boolean insertar(Proveedor p) throws SQLException {
        return guardar("sp_insertarproveedor", p);
    }

    @Override
    public boolean actualizar(Proveedor p) throws SQLException {
        return guardar("sp_actualizarproveedor", p);
    }

    private boolean guardar(String sp, Proveedor p) throws SQLException {
        validar(p);
        
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL " + sp + "(?,?,?,?,?)}")) {
            
            s.setString(1, p.getNit().trim());
            s.setString(2, p.getNombre().trim());
            s.setString(3, limpio(p.getTelefono()));
            s.setString(4, limpio(p.getDireccion()));
            s.setString(5, limpio(p.getCorreo()));
            
            s.execute();
            return true;
        }
    }

    @Override
    public boolean desactivar(String nit) throws SQLException {
        if (nit == null || nit.isBlank()) {
            throw new IllegalArgumentException("Selecciona un proveedor.");
        }
        
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_eliminarproveedor(?)}")) {
            
            s.setString(1, nit.trim());
            s.execute();
            return true;
        }
    }

    private void validar(Proveedor p) {
        if (p == null || p.getNit() == null || p.getNit().isBlank()) {
            throw new IllegalArgumentException("El NIT es obligatorio.");
        }
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (p.getCorreo() != null && !p.getCorreo().isBlank() && !p.getCorreo().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("El correo no tiene un formato válido.");
        }
    }

    private String limpio(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}