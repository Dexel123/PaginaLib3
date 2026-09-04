package org.paginalib3.controller;

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
