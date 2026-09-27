package org.paginalib3.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.paginalib3.system.Main;
import org.paginalib3.dao.UsuarioDAO;
import org.paginalib3.dao.impl.UsuarioDAOImpl;
import org.paginalib3.util.Seguridad;

public class RestablecerContrasenaController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtNueva;
    @FXML private PasswordField txtConfirmar;
    @FXML private Label lblEstado;

    private String usuarioForzado;

    public void setUsuario(String username) {
        usuarioForzado = username == null ? "" : username.trim();
        if (txtUsuario != null) {
            txtUsuario.setText(usuarioForzado);
            txtUsuario.setDisable(true);
        }
    }

    @FXML
    private void guardar() {
        lblEstado.setText("");
        try {
            String username = usuarioForzado == null ? txtUsuario.getText() : usuarioForzado;
            String nueva = txtNueva.getText();
            String confirmar = txtConfirmar.getText();

            if (username == null || username.isBlank() || nueva == null || nueva.isBlank()
                    || confirmar == null || confirmar.isBlank()) {
                throw new IllegalArgumentException("Completa todos los campos.");
            }
            if (nueva.length() < 6) {
                throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres.");
            }
            if (!nueva.equals(confirmar)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }
            if (usuarioDAO.buscarPorUsername(username) == null) {
                throw new IllegalArgumentException("No existe un usuario con ese nombre.");
            }
            if (!usuarioDAO.restablecerContrasena(username, Seguridad.sha256(nueva))) {
                throw new IllegalArgumentException("No fue posible cambiar la contraseña.");
            }

            Main.cambiarVista("/org/paginalib3/view/login.fxml",
                    "Pagina-Libreria | Iniciar sesión", 760, 560);
        } catch (Exception ex) {
            lblEstado.setText(ex.getMessage() == null ? "No se pudo cambiar la contraseña." : ex.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        Main.cambiarVista("/org/paginalib3/view/login.fxml",
                "Pagina-Libreria | Iniciar sesión", 760, 560);
    }
}
