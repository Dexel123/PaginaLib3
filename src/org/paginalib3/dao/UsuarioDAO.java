package org.paginalib3.dao;

import org.paginalib3.model.Usuario;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrato de acceso a datos para la entidad Usuario.
 * US-1.1, US-1.2, US-1.4 del Product Backlog (Epica 1).
 */
public interface UsuarioDAO {

    Usuario iniciarSesion(String username, String passwordHash) throws SQLException;
    Usuario buscarPorUsername(String username) throws SQLException;

    void registrar(Usuario usuario, String passwordHash) throws SQLException;

    List<Usuario> listar() throws SQLException;

    void actualizarDatos(int id, String nombre, String apellido, String correo) throws SQLException;

    void cambiarEstado(int id, boolean activo) throws SQLException;

    void cambiarRol(int id, String rol) throws SQLException;

    boolean cambiarContrasena(int id, String actualHash, String nuevaHash) throws SQLException;
    boolean restablecerContrasena(String username, String nuevaHash) throws SQLException;
}
