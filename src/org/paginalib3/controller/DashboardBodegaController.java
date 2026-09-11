package org.paginalib3.controller;

import javafx.fxml.FXML;
import org.paginalib3.system.Main;

public class DashboardBodegaController extends DashboardBaseController {

    @FXML
    private void irASalidaInventario() {
        Main.cambiarVista("/org/paginalib3/view/salida_inventario.fxml",
                "Pagina-Libreria | Registrar salida de inventario", 1200, 760);
    }

    @Override
    protected String rolPermitido() {
        return "bodega";
    }

    @Override
    protected String mensajeRol() {
        return "Control de inventario y existencias de la libreria.";
    }
}
