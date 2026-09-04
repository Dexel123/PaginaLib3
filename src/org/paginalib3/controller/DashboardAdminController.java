package org.paginalib3.controller;

import javafx.fxml.FXML;
import org.paginalib3.system.Main;

/**
 * Controlador del Dashboard de Administrador (dashboard_admin.fxml).
 * Unico rol con acceso a Gestion de usuarios (US-1.2).
 */
public class DashboardAdminController extends DashboardBaseController {

    @Override
    protected String rolPermitido() {
        return "admin";
    }

    @Override
    protected String mensajeRol() {
        return "Control general del sistema y administracion de usuarios.";
    }

    @FXML
    private void irAUsuarios() {
        Main.cambiarVista("/org/paginalib3/view/usuarios.fxml", "Pagina-Libreria | Usuarios", 1000, 650);
    }
}
