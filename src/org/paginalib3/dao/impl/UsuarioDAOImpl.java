package org.paginalib3.dao.impl;

import org.paginalib3.dao.UsuarioDAO;
import org.paginalib3.model.Usuario;
import org.paginalib3.util.Conexion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario iniciarSesion(String username, String passwordHash) throws SQLException {
        String sql = "{CALL sp_iniciar_sesion(?,?)}";
        try (Connection cn = Conexion.getInstancia().conectar();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setString(1, username.trim());
            cs.setString(2, passwordHash);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Usuario buscarPorUsername(String username) throws SQLException {
        try (Connection cn = Conexion.getInstancia().conectar();
             CallableStatement cs = cn.prepareCall("{CALL sp_buscar_usuario_username(?)}")) {
            cs.setString(1, username.trim());
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public void registrar(Usuario usuario, String passwordHash) throws SQLException {
        String sql = "{CALL sp_registrar_usuario(?,?,?,?,?,?)}";
        try (Connection cn = Conexion.getInstancia().conectar();
             CallableStatement cs = cn.prepareCall(sql)) {
            cs.setString(1, usuario.getUsername().trim());
            cs.setString(2, passwordHash);
            cs.setString(3, usuario.getNombre().trim());
            cs.setString(4, usuario.getApellido().trim());
            cs.setString(5, usuario.getCorreo().trim());
            cs.setString(6, usuario.getRol());
            cs.execute();
        }
    }

    @Override
    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection cn = Conexion.getInstancia().conectar();
             CallableStatement cs = cn.prepareCall("{CALL sp_listarusuarios()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    @Override
    public void actualizarDatos(int id, String nombre, String apellido, String correo) throws SQLException {
        try (Connection cn = Conexion.getInstancia().conectar();
             CallableStatement cs = cn.prepareCall("{CALL sp_actualizarusuario(?,?,?,?)}")) {
            cs.setInt(1, id);
            cs.setString(2, nombre.trim());
            cs.setString(3, apellido.trim());
            cs.setString(4, correo.trim());
            cs.execute();
        }
    }

    @Override
    public void cambiarEstado(int id, boolean activo) throws SQLException {
        try (Connection cn = Conexion.getInstancia().conectar();
             CallableStatement cs = cn.prepareCall("{CALL sp_cambiar_estado_usuario(?,?)}")) {
            cs.setInt(1, id);
            cs.setBoolean(2, activo);
            cs.execute();
        }
    }

    @Override
    public void cambiarRol(int id, String rol) throws SQLException {
        try (Connection cn = Conexion.getInstancia().conectar();
             CallableStatement cs = cn.prepareCall("{CALL sp_cambiar_rol_usuario(?,?)}")) {
            cs.setInt(1, id);
            cs.setString(2, rol);
            cs.execute();
        }
    }

    @Override
    public boolean cambiarContrasena(int id, String actualHash, String nuevaHash) throws SQLException {
        try (Connection cn = Conexion.getInstancia().conectar();
             CallableStatement cs = cn.prepareCall("{CALL sp_cambiar_contrasena(?,?,?,?)}")) {
            cs.setInt(1, id);
            cs.setString(2, actualHash);
            cs.setString(3, nuevaHash);
            cs.registerOutParameter(4, Types.TINYINT);
            cs.execute();
            return cs.getInt(4) == 1;
        }
    }

    @Override
    public boolean restablecerContrasena(String username, String nuevaHash) throws SQLException {
        try (Connection cn = Conexion.getInstancia().conectar();
             CallableStatement cs = cn.prepareCall("{CALL sp_restablecer_contrasena(?,?,?)}")) {
            cs.setString(1, username.trim());
            cs.setString(2, nuevaHash);
            cs.registerOutParameter(3, Types.TINYINT);
            cs.execute();
            return cs.getInt(3) == 1;
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("correo"),
                rs.getString("rol"),
                rs.getBoolean("activo")
        );
    }
}
