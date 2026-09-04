package org.paginalib3.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import org.paginalib3.system.Main;
import org.paginalib3.dao.UsuarioDAO;
import org.paginalib3.dao.impl.UsuarioDAOImpl;
import org.paginalib3.model.Usuario;
import org.paginalib3.util.Seguridad;
import org.paginalib3.util.Sesion;

/**
 * US-1.4: Como usuario, quiero cambiar mi contrasena proporcionando mi
 * contrasena actual, para mantener segura mi cuenta.
 */
public class CambiarContrasenaController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @FXML
    private PasswordField txtActual;
    @FXML
    private PasswordField txtNueva;
    @FXML
    private PasswordField txtConfirmar;
    @FXML
    private Label lblEstado;

    @FXML
    private void guardar() {
        lblEstado.setText("");
        Usuario usuario = Sesion.getUsuarioActual();

        try {
            String actual = txtActual.getText();
            String nueva = txtNueva.getText();
            String confirmar = txtConfirmar.getText();

            if (isBlank(actual) || isBlank(nueva) || isBlank(confirmar)) {
                throw new IllegalArgumentException("Completa todos los campos.");
            }
            if (nueva.length() < 6) {
                throw new IllegalArgumentException("La nueva contrasena debe tener al menos 6 caracteres.");
            }
            if (!nueva.equals(confirmar)) {
                throw new IllegalArgumentException("Las nuevas contrasenas no coinciden.");
            }
            if (nueva.equals(actual)) {
                throw new IllegalArgumentException("La nueva contrasena debe ser diferente.");
            }

            boolean ok = usuarioDAO.cambiarContrasena(usuario.getId(),
                    Seguridad.sha256(actual), Seguridad.sha256(nueva));

            if (!ok) {
                throw new IllegalArgumentException("La contrasena actual no es correcta.");
            }

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Contrasena actualizada");
            alerta.setHeaderText(null);
            alerta.setContentText("Tu contrasena fue cambiada correctamente.");
            alerta.showAndWait();

            volverAlDashboard(usuario);

        } catch (Exception ex) {
            lblEstado.setText(ex.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        volverAlDashboard(Sesion.getUsuarioActual());
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private void volverAlDashboard(Usuario usuario) {
        switch (usuario.getRol()) {
            case "admin" ->
                Main.cambiarVista("/org/paginalib3/view/dashboard_admin.fxml",
                        "Pagina-Libreria | Dashboard Administrador", 1100, 680);
            case "bodega" ->
                Main.cambiarVista("/org/paginalib3/view/dashboard_bodega.fxml",
                        "Pagina-Libreria | Dashboard Bodega", 1100, 680);
            case "cajero" ->
                Main.cambiarVista("/org/paginalib3/view/dashboard_cajero.fxml",
                        "Pagina-Libreria | Dashboard Caja", 1100, 680);
            default ->
                Main.cambiarVista("/org/paginalib3/view/login.fxml", "Pagina-Libreria | Iniciar sesion", 760, 560);
        }
    }
}
