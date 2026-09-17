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
        return "Control de alertas de stock de la librería.";
    }

    @FXML
    private void irAStockCritico() {
        Main.cambiarVista("/org/paginalib3/view/stock_critico.fxml", "Pagina-Libreria | Stock crítico", 1050, 680);
    }
}