package org.paginalib3.dao.impl;

import org.paginalib3.dao.UsuarioDAO;
import org.paginalib3.model.Usuario;
import org.paginalib3.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario iniciarSesion(String username, String passwordHash) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_iniciar_sesion(?,?)}")) {
            s.setString(1, username.trim());
            s.setString(2, passwordHash);
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    return mapear(r);
                }
            }
        }
        return null;
    }

    @Override
    public Usuario buscarPorUsername(String username) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_buscar_usuario_username(?)}")) {
            s.setString(1, username.trim());
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    return mapear(r);
                }
            }
        }
        return null;
    }

    @Override
    public void registrar(Usuario u, String h) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_registrar_usuario(?,?,?,?,?,?)}")) {
            s.setString(1, u.getUsername().trim());
            s.setString(2, h);
            s.setString(3, u.getNombre().trim());
            s.setString(4, u.getApellido().trim());
            s.setString(5, u.getCorreo() == null ? null : u.getCorreo().trim());
            s.setString(6, u.getRol());
            s.execute();
        }
    }

    @Override
    public List<Usuario> listar() throws SQLException {
        List<Usuario> l = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_listarusuarios()}");
             ResultSet r = s.executeQuery()) {
            while (r.next()) {
                l.add(mapear(r));
            }
        }
        return l;
    }

    @Override
    public void actualizarDatos(int id, String n, String a, String co) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_actualizarusuario(?,?,?,?)}")) {
            s.setInt(1, id);
            s.setString(2, n.trim());
            s.setString(3, a.trim());
            s.setString(4, co == null ? null : co.trim());
            s.execute();
        }
    }

    @Override
    public void cambiarEstado(int id, boolean activo) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_cambiar_estado_usuario(?,?)}")) {
            s.setInt(1, id);
            s.setBoolean(2, activo);
            s.execute();
        }
    }

    @Override
    public void cambiarRol(int id, String rol) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_cambiar_rol_usuario(?,?)}")) {
            s.setInt(1, id);
            s.setString(2, rol);
            s.execute();
        }
    }

    @Override
    public boolean cambiarContrasena(int id, String a, String n) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_cambiar_contrasena(?,?,?,?)}")) {
            s.setInt(1, id);
            s.setString(2, a);
            s.setString(3, n);
            s.registerOutParameter(4, Types.TINYINT);
            s.execute();
            return s.getInt(4) == 1;
        }
    }

    @Override
    public boolean restablecerContrasena(String u, String n) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar();
             CallableStatement s = c.prepareCall("{CALL sp_restablecer_contrasena(?,?,?)}")) {
            s.setString(1, u.trim());
            s.setString(2, n);
            s.registerOutParameter(3, Types.TINYINT);
            s.execute();
            return s.getInt(3) == 1;
        }
    }

    private Usuario mapear(ResultSet r) throws SQLException {
        return new Usuario(
                r.getInt("id"),
                r.getString("username"),
                r.getString("nombre"),
                r.getString("apellido"),
                r.getString("correo"),
                r.getString("rol"),
                r.getBoolean("activo")
        );
    }
}