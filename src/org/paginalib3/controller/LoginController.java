package org.paginalib3.controller;

import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.paginalib3.system.Main;
import org.paginalib3.dao.UsuarioDAO;
import org.paginalib3.dao.impl.UsuarioDAOImpl;
import org.paginalib3.model.Usuario;
import org.paginalib3.util.Seguridad;
import org.paginalib3.util.Sesion;

public class LoginController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final Map<String, Integer> intentosFallidos = new HashMap<>();

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblEstado;
    @FXML private Button btnCambiarContrasena;

    @FXML
    private void initialize() {
        btnCambiarContrasena.setVisible(false);
        btnCambiarContrasena.setManaged(false);
    }

    @FXML
    private void autenticar() {
        lblEstado.setText("");
        String username = txtUsuario.getText();
        String password = txtPassword.getText();

        try {
            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                throw new IllegalArgumentException("Ingresa usuario y contraseña.");
            }

            username = username.trim();
            Usuario usuario = usuarioDAO.iniciarSesion(username, Seguridad.sha256(password));

            if (usuario == null) {
                int fallos = intentosFallidos.merge(username, 1, Integer::sum);
                if (fallos >= 3) {
                    btnCambiarContrasena.setVisible(true);
                    btnCambiarContrasena.setManaged(true);
                    lblEstado.setText("Tres intentos fallidos para '" + username
                            + "'. Puedes cambiar la contraseña de este usuario.");
                } else {
                    lblEstado.setText("Usuario o contraseña incorrectos. Intento " + fallos + " de 3.");
                }
                return;
            }

            intentosFallidos.remove(username);
            Sesion.iniciar(usuario);
            redirigirSegunRol(usuario);
        } catch (Exception ex) {
            lblEstado.setText(ex.getMessage() == null ? "No fue posible iniciar sesión." : ex.getMessage());
        }
    }

    @FXML
    private void irARegistro() {
        Main.cambiarVista("/org/paginalib3/view/registro.fxml",
                "Pagina-Libreria | Registro", 650, 760);
    }

    @FXML
    private void irACambiarContrasena() {
        String username = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        if (username.isBlank()) {
            lblEstado.setText("Escribe el usuario que falló 3 veces.");
            return;
        }
        Main.cambiarVista("/org/paginalib3/view/restablecer_contrasena.fxml",
                "Pagina-Libreria | Cambiar contraseña", 620, 620);
        Main.configurarVistaActual(controller -> {
            if (controller instanceof RestablecerContrasenaController reset) {
                reset.setUsuario(username);
            }
        });
    }

    private void redirigirSegunRol(Usuario usuario) {
        switch (usuario.getRol()) {
            case "admin" -> Main.cambiarVista("/org/paginalib3/view/dashboard_admin.fxml",
                    "Pagina-Libreria | Dashboard Administrador", 1100, 680);
            case "bodega" -> Main.cambiarVista("/org/paginalib3/view/dashboard_bodega.fxml",
                    "Pagina-Libreria | Dashboard Bodega", 1100, 680);
            case "cajero" -> Main.cambiarVista("/org/paginalib3/view/dashboard_cajero.fxml",
                    "Pagina-Libreria | Dashboard Caja", 1100, 680);
            default -> lblEstado.setText("El rol del usuario no tiene un Dashboard asignado.");
        }
    }
}
