package org.paginalib3.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class VerificadorProyecto {

    private static final String[] RECURSOS = {
        "/db.properties",
        "/org/paginalib3/view/style/styles.css",
        "/org/paginalib3/view/login.fxml",
        "/org/paginalib3/view/dashboard_admin.fxml",
        "/org/paginalib3/view/dashboard_bodega.fxml",
        "/org/paginalib3/view/dashboard_cajero.fxml",
        "/org/paginalib3/view/usuarios.fxml",
        "/org/paginalib3/view/usuario_form.fxml",
        "/org/paginalib3/view/cambiar_rol.fxml",
        "/org/paginalib3/view/cambiar_contrasena.fxml",
        "/org/paginalib3/view/restablecer_contrasena.fxml",
        "/org/paginalib3/view/buscar_libros.fxml",
        "/org/paginalib3/view/venta.fxml",
        "/org/paginalib3/view/comprobante.fxml",
        "/org/paginalib3/view/devolucion.fxml",
        "/org/paginalib3/view/libros.fxml",
        "/org/paginalib3/view/ingreso_inventario.fxml",
        "/org/paginalib3/view/salida_inventario.fxml",
        "/org/paginalib3/view/stock_critico.fxml",
        "/org/paginalib3/view/reportes_ventas.fxml",
        "/org/paginalib3/view/reportes_inventario.fxml",
        "/org/paginalib3/view/gestion_administrativa.fxml"
    };

    private VerificadorProyecto() {
    }

    public static List<String> validarRecursos() {
        List<String> problemas = new ArrayList<>();
        for (String recurso : RECURSOS) {
            URL url = VerificadorProyecto.class.getResource(recurso);
            if (url == null) {
                problemas.add("Falta el recurso: " + recurso);
            }
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            problemas.add("No está disponible el MySQL Connector (com.mysql.cj.jdbc.Driver).");
        }
        return Collections.unmodifiableList(problemas);
    }
}
