package org.paginalib3.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.paginalib3.system.Main;
import org.paginalib3.dao.UsuarioDAO;
import org.paginalib3.dao.impl.UsuarioDAOImpl;
import org.paginalib3.model.Usuario;
import org.paginalib3.util.Seguridad;

public class RegistroController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmar;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtCorreo;
    @FXML private ComboBox<String> cmbRol;
    @FXML private Label lblEstado;

    @FXML
    private void initialize() {
        cmbRol.setItems(FXCollections.observableArrayList("admin", "bodega", "cajero"));
        cmbRol.setValue("cajero");
    }

    @FXML
    private void registrar() {
        lblEstado.setText("");
        try {
            String username = txtUsername.getText();
            String password = txtPassword.getText();
            String confirmar = txtConfirmar.getText();
            String nombre = txtNombre.getText();
            String apellido = txtApellido.getText();
            String correo = txtCorreo.getText();
            String rol = cmbRol.getValue();

            if (isBlank(username) || isBlank(password) || isBlank(confirmar)
                    || isBlank(nombre) || isBlank(apellido) || isBlank(correo) || rol == null) {
                throw new IllegalArgumentException("Completa todos los campos.");
            }
            if (username.trim().length() < 3) {
                throw new IllegalArgumentException("El usuario debe tener al menos 3 caracteres.");
            }
            if (password.length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
            }
            if (!password.equals(confirmar)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }
            if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                throw new IllegalArgumentException("Ingresa un correo válido.");
            }

            Usuario nuevo = new Usuario(0, username.trim(), nombre.trim(), apellido.trim(),
                    correo.trim(), rol, true);
            usuarioDAO.registrar(nuevo, Seguridad.sha256(password));

            Main.cambiarVista("/org/paginalib3/view/login.fxml",
                    "Pagina-Libreria | Iniciar sesión", 760, 560);
        } catch (Exception ex) {
            lblEstado.setText(ex.getMessage() == null ? "No se pudo registrar el usuario." : ex.getMessage());
        }
    }

    @FXML
    private void volver() {
        Main.cambiarVista("/org/paginalib3/view/login.fxml",
                "Pagina-Libreria | Iniciar sesión", 760, 560);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
