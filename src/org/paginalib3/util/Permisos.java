package org.paginalib3.util;

import org.paginalib3.model.Usuario;
import org.paginalib3.system.Main;

/**
 * Reglas de acceso de la aplicación en un solo lugar.
 */
public final class Permisos {

    private Permisos() {
    }

    public static boolean esAdmin() {
        Usuario u = Sesion.getUsuarioActual();
        return u != null && "admin".equalsIgnoreCase(u.getRol());
    }

    public static boolean puedeInventario() {
        Usuario u = Sesion.getUsuarioActual();
        return u != null
                && ("bodega".equalsIgnoreCase(u.getRol())
                || "admin".equalsIgnoreCase(u.getRol()));
    }

    public static boolean puedeCaja() {
        Usuario u = Sesion.getUsuarioActual();
        return u != null
                && ("cajero".equalsIgnoreCase(u.getRol())
                || "admin".equalsIgnoreCase(u.getRol()));
    }

    public static boolean requerirAdmin(String modulo) {
        if (esAdmin()) {
            return true;
        }
        accesoDenegado(modulo, "administración");
        return false;
    }

    public static boolean requerirInventario(String modulo) {
        if (puedeInventario()) {
            return true;
        }
        accesoDenegado(modulo, "bodega o administración");
        return false;
    }

    public static boolean requerirCaja(String modulo) {
        if (puedeCaja()) {
            return true;
        }
        accesoDenegado(modulo, "caja o administración");
        return false;
    }

    private static void accesoDenegado(String modulo, String rolesPermitidos) {
        Usuario u = Sesion.getUsuarioActual();
        if (u != null) {
            MensajesUI.advertencia(
                    "Acceso restringido",
                    "El módulo " + modulo + " requiere permisos de " + rolesPermitidos + ".");
        }
        volverDashboardSegunRol();
    }

    public static void volverDashboardSegunRol() {
        Usuario u = Sesion.getUsuarioActual();
        if (u == null) {
            Main.cambiarVista("/org/paginalib3/view/login.fxml",
                    "Pagina-Libreria | Iniciar sesión", 760, 560);
            return;
        }

        switch (u.getRol().toLowerCase()) {
            case "admin" ->
                Main.cambiarVista(
                        "/org/paginalib3/view/dashboard_admin.fxml",
                        "Pagina-Libreria | Administración", 1180, 720);
            case "bodega" ->
                Main.cambiarVista(
                        "/org/paginalib3/view/dashboard_bodega.fxml",
                        "Pagina-Libreria | Bodega", 1180, 720);
            case "cajero" ->
                Main.cambiarVista(
                        "/org/paginalib3/view/dashboard_cajero.fxml",
                        "Pagina-Libreria | Caja", 1100, 680);
            default -> {
                Sesion.cerrar();
                Main.cambiarVista("/org/paginalib3/view/login.fxml",
                        "Pagina-Libreria | Iniciar sesión", 760, 560);
            }
        }
    }
}
