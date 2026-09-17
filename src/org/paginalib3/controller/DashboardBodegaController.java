package org.paginalib3.controller;

import javafx.fxml.FXML;
import org.paginalib3.system.Main;

public class DashboardBodegaController extends DashboardBaseController {

    @Override
    protected String rolPermitido() {
        return "bodega";
    }

    @Override
    protected String mensajeRol() {
        return "Control de ingresos y existencias de la librería.";
    }

    @FXML
    private void irAIngreso() {
        Main.cambiarVista("/org/paginalib3/view/ingreso_inventario.fxml", "Pagina-Libreria | Ingreso de inventario", 1120, 720);
    }
}