package org.paginalib3.controller;

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
