package org.paginalib3.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Utilidad central para mensajes de interfaz y registro de errores.
 * Evita que cada controlador muestre errores de una forma distinta.
 */
public final class MensajesUI {

    private static final Logger LOGGER = Logger.getLogger(MensajesUI.class.getName());

    private MensajesUI() {
    }

    public static void informacion(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    public static void advertencia(String titulo, String mensaje) {
        mostrar(Alert.AlertType.WARNING, titulo, mensaje);
    }

    public static void error(String titulo, String mensaje) {
        mostrar(Alert.AlertType.ERROR, titulo, mensaje);
    }

    public static void error(String titulo, String mensaje, Throwable error) {
        registrarError(error);
        mostrar(Alert.AlertType.ERROR, titulo, mensaje);
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        return alerta.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    public static void registrarError(Throwable error) {
        if (error != null) {
            LOGGER.log(Level.SEVERE, mensajeTecnico(error), error);
        }
    }

    public static String mensajeTecnico(Throwable error) {
        if (error == null) {
            return "Error desconocido";
        }
        Throwable actual = error;
        while (actual.getCause() != null && actual.getCause() != actual) {
            actual = actual.getCause();
        }
        String mensaje = actual.getMessage();
        if (mensaje == null || mensaje.isBlank()) {
            mensaje = actual.getClass().getSimpleName();
        }
        return mensaje;
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje == null ? "" : mensaje, ButtonType.OK);
        alerta.setTitle(titulo == null ? "Página Viva" : titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}