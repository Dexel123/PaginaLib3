package org.paginalib3.controller;

/**
 * Controlador del Dashboard de Bodega (dashboard_bodega.fxml).
 * Los accesos de Inventario y Stock critico pertenecen a la Epica 3
 * y se implementaran en un sprint posterior.
 */
public class DashboardBodegaController extends DashboardBaseController {

    @Override
    protected String rolPermitido() {
        return "bodega";
    }

    @Override
    protected String mensajeRol() {
        return "Control de inventario y existencias de la libreria.";
    }
}
