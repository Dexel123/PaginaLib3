package org.paginalib3.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.paginalib3.system.Main;
import org.paginalib3.model.Usuario;
import org.paginalib3.util.Sesion;

public abstract class DashboardBaseController {

    @FXML protected Label lblNombreUsuario;
    @FXML protected Label lblRolUsuario;
    @FXML protected Label lblSaludo;
    @FXML protected Label lblUsuarioMetric;

    @FXML
    protected void initialize() {
        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario == null || !rolPermitido().equalsIgnoreCase(usuario.getRol())) {
            Sesion.cerrar();
            Main.cambiarVista("/org/paginalib3/view/login.fxml",
                    "Pagina-Libreria | Iniciar sesión", 760, 560);
            return;
        }

        if (lblNombreUsuario != null) lblNombreUsuario.setText(usuario.getNombreCompleto());
        if (lblRolUsuario != null) lblRolUsuario.setText(usuario.getRol().toUpperCase());
        if (lblUsuarioMetric != null) lblUsuarioMetric.setText(usuario.getUsername());
        if (lblSaludo != null) lblSaludo.setText(mensajeRol());
    }

    protected abstract String mensajeRol();
    protected abstract String rolPermitido();

    @FXML
    protected void irACambiarContrasena() {
        Main.cambiarVista("/org/paginalib3/view/cambiar_contrasena.fxml",
                "Pagina-Libreria | Cambiar contraseña", 800, 620);
    }

    @FXML
    protected void cerrarSesion() {
        Sesion.cerrar();
        Main.cambiarVista("/org/paginalib3/view/login.fxml",
                "Pagina-Libreria | Iniciar sesión", 760, 560);
    }

    @FXML
    protected void accionProximamente() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Disponible próximamente");
        alerta.setHeaderText(null);
        alerta.setContentText("Este módulo se implementará en un próximo sprint.");
        alerta.showAndWait();
    }
}
