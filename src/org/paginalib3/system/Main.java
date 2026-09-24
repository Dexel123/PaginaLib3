package org.paginalib3.system;

import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.application.Platform;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.paginalib3.util.MensajesUI;
import org.paginalib3.util.VerificadorProyecto;

public class Main extends Application {

    private static Stage stagePrincipal;
    private static Object controladorVistaActual;

    @Override
    public void start(Stage stage) {
        stagePrincipal = stage;
        stage.setResizable(true);

        cambiarVista("/org/paginalib3/view/login.fxml",
                "Pagina-Libreria | Iniciar sesión", 760, 560);

        // Revisión no destructiva de recursos después de cargar la primera escena.
        Platform.runLater(() -> {
            List<String> problemas = VerificadorProyecto.validarRecursos();
            if (!problemas.isEmpty()) {
                MensajesUI.error(
                        "Configuración incompleta",
                        "Se detectaron recursos faltantes:\n\n" + String.join("\n", problemas)
                        + "\n\nRevisa las librerías del proyecto antes de continuar.");
            }
        });
    }

    /**
     * Cambia la vista principal. Si una pantalla contiene un error de FXML,
     * conserva la vista anterior y muestra un mensaje en lugar de cerrar la
     * app.
     */
    public static void cambiarVista(String rutaFxml, String titulo, double ancho, double alto) {
        try {
            URL url = Main.class.getResource(rutaFxml);
            if (url == null) {
                throw new IllegalStateException("No se encontró el recurso FXML: " + rutaFxml);
            }

            // Limpia manejadores de la vista anterior antes de inicializar la nueva.
            // Si la nueva vista instala su propio manejador (por ejemplo Venta), se conserva.
            if (stagePrincipal != null) {
                stagePrincipal.setOnCloseRequest(null);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Object nuevoControlador = loader.getController();

            Rectangle2D area = Screen.getPrimary().getVisualBounds();
            double anchoReal = Math.max(640, Math.min(ancho, area.getWidth() - 36));
            double altoReal = Math.max(520, Math.min(alto, area.getHeight() - 42));

            Scene scene = new Scene(root, anchoReal, altoReal);
            URL css = Main.class.getResource("/org/paginalib3/view/style/styles.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            stagePrincipal.setTitle(titulo);
            stagePrincipal.setScene(scene);
            stagePrincipal.setMinWidth(Math.min(680, area.getWidth() - 20));
            stagePrincipal.setMinHeight(Math.min(520, area.getHeight() - 20));
            stagePrincipal.setMaxWidth(area.getWidth());
            stagePrincipal.setMaxHeight(area.getHeight());
            stagePrincipal.setWidth(anchoReal);
            stagePrincipal.setHeight(altoReal);
            stagePrincipal.centerOnScreen();
            stagePrincipal.show();

            controladorVistaActual = nuevoControlador;
        } catch (Exception e) {
            MensajesUI.error(
                    "No se pudo abrir la pantalla",
                    "La vista solicitada no pudo cargarse.\n\n"
                    + "Vista: " + rutaFxml + "\n"
                    + "Detalle: " + MensajesUI.mensajeTecnico(e),
                    e);
        }
    }

    public static void configurarVistaActual(Consumer<Object> configurador) {
        if (controladorVistaActual != null && configurador != null) {
            configurador.accept(controladorVistaActual);
        }
    }

    public static Stage getStagePrincipal() {
        return stagePrincipal;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
