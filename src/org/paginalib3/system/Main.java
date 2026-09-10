package org.paginalib3.system;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage stagePrincipal;
    private static Object controladorVistaActual;

    @Override
    public void start(Stage stage) {
        stagePrincipal = stage;
        stage.setResizable(true);
        cambiarVista("/org/paginalib3/view/login.fxml",
                "Pagina-Libreria | Iniciar sesión", 760, 560);
    }

    public static void cambiarVista(String rutaFxml, String titulo, double ancho, double alto) {
        try {
            URL url = Main.class.getResource(rutaFxml);
            if (url == null) {
                throw new IOException("No se encontró el recurso FXML: " + rutaFxml);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            System.out.println(">>> FXML CARGADO: " + rutaFxml);
System.out.println(">>> CONTROLADOR: " + loader.getController());
            controladorVistaActual = loader.getController();

            Scene scene = new Scene(root, ancho, alto);
            URL css = Main.class.getResource("/org/paginalib3/view/styles.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            stagePrincipal.setTitle(titulo);
            stagePrincipal.setScene(scene);
            stagePrincipal.centerOnScreen();
            stagePrincipal.show();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar la vista " + rutaFxml, e);
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
