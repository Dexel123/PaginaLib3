package org.paginalib3.controller;

/**
 * Controlador del Dashboard de Caja (dashboard_cajero.fxml).
 * Los accesos de Caja y Ventas del dia pertenecen a la Epica 2
 * y se implementaran en un sprint posterior.
 */
public class DashboardCajeroController extends DashboardBaseController {

    @Override
    protected String rolPermitido() {
        return "cajero";
    }

    @Override
    protected String mensajeRol() {
        return "Acceso al area de caja y seguimiento de ventas.";
    }
}
