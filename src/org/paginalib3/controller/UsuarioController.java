package org.paginalib3.controller;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.paginalib3.dao.UsuarioDAO;
import org.paginalib3.dao.impl.UsuarioDAOImpl;
import org.paginalib3.model.Usuario;
import org.paginalib3.system.Main;
import org.paginalib3.util.MensajesUI;
import org.paginalib3.util.Permisos;
import org.paginalib3.util.Sesion;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colEstado;

    @FXML
    private void initialize() {
        if (!Permisos.requerirAdmin("Gestión de usuarios")) return;

        colUsername.setCellValueFactory(data -> texto(data.getValue(), Usuario::getUsername));
        colNombre.setCellValueFactory(data -> texto(data.getValue(), Usuario::getNombreCompleto));
        colRol.setCellValueFactory(data -> texto(data.getValue(), Usuario::getRol));
        colEstado.setCellValueFactory(data -> texto(data.getValue(), Usuario::getEstadoTexto));
        cargar();
    }

    private javafx.beans.property.SimpleStringProperty texto(Usuario u, Function<Usuario, String> fn) {
        return new javafx.beans.property.SimpleStringProperty(fn.apply(u));
    }

    private void cargar() {
        try {
            tablaUsuarios.setItems(FXCollections.observableArrayList(usuarioDAO.listar()));
        } catch (Exception ex) {
            MensajesUI.error(
                    "Gestión de usuarios",
                    "No se pudo cargar la lista de usuarios.\n\nDetalle: " + MensajesUI.mensajeTecnico(ex),
                    ex);
        }
    }

    @FXML
    private void registrar() {
        abrirFormularioUsuario("Registrar usuario", controller -> {
            controller.initModoRegistro();
            controller.setOnGuardadoExitoso(this::cargar);
        });
    }

    @FXML
    private void editar() {
        Usuario seleccionado = seleccionado();
        if (seleccionado == null) return;

        abrirFormularioUsuario("Editar usuario", controller -> {
            controller.initModoEdicion(seleccionado);
            controller.setOnGuardadoExitoso(this::cargar);
        });
    }

    @FXML
    private void cambiarRol() {
        Usuario seleccionado = seleccionado();
        if (seleccionado == null) return;

        try {
            URL url = getClass().getResource("/org/paginalib3/view/cambiar_rol.fxml");
            if (url == null) throw new IOException("No se encontró cambiar_rol.fxml");

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            CambiarRolController controller = loader.getController();
            controller.init(seleccionado);
            controller.setOnGuardadoExitoso(this::cargar);
            mostrarVentanaModal(root, "Cambiar rol", 430, 330);
        } catch (Exception ex) {
            MensajesUI.error(
                    "Gestión de usuarios",
                    "No se pudo abrir la ventana para cambiar el rol.\n\nDetalle: " + MensajesUI.mensajeTecnico(ex),
                    ex);
        }
    }

    @FXML
    private void cambiarEstado() {
        Usuario u = seleccionado();
        if (u == null) return;

        if (u.isActivo()) {
            Usuario usuarioActual = Sesion.getUsuarioActual();
            if (usuarioActual != null && usuarioActual.getId() == u.getId()) {
                MensajesUI.advertencia("Acción no permitida", "No puedes desactivarte a ti mismo.");
                return;
            }

            // Evita dejar el sistema sin administradores activos.
            if ("admin".equalsIgnoreCase(u.getRol())) {
                MensajesUI.advertencia(
                        "Acción no permitida",
                        "Los administradores no se desactivan desde esta pantalla. Cambia primero su rol si corresponde.");
                return;
            }
        }

        String accion = u.isActivo() ? "desactivar" : "activar";
        if (!MensajesUI.confirmar(
                "Cambiar estado de usuario",
                "¿Deseas " + accion + " al usuario '" + u.getUsername() + "'?")) {
            return;
        }

        try {
            boolean nuevoEstado = !u.isActivo();
            usuarioDAO.cambiarEstado(u.getId(), nuevoEstado);
            cargar();
            MensajesUI.informacion(
                    "Estado actualizado",
                    "El usuario '" + u.getUsername() + "' fue "
                    + (nuevoEstado ? "activado" : "desactivado") + " correctamente.");
        } catch (Exception ex) {
            MensajesUI.error(
                    "Gestión de usuarios",
                    "No se pudo cambiar el estado del usuario.\n\nDetalle: " + MensajesUI.mensajeTecnico(ex),
                    ex);
        }
    }

    @FXML
    private void volver() {
        Main.cambiarVista(
                "/org/paginalib3/view/dashboard_admin.fxml",
                "Pagina-Libreria | Dashboard Administrador", 1180, 720);
    }

    /**
     * Importante: el controlador se configura ANTES de showAndWait().
     * Esto corrige el fallo anterior donde el formulario se abría sin modo ni datos.
     */
    private void abrirFormularioUsuario(String titulo, Consumer<UsuarioFormController> configurador) {
        try {
            URL url = getClass().getResource("/org/paginalib3/view/usuario_form.fxml");
            if (url == null) throw new IOException("No se encontró usuario_form.fxml");

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            UsuarioFormController controller = loader.getController();
            configurador.accept(controller);
            mostrarVentanaModal(root, titulo, 470, 590);
        } catch (Exception ex) {
            MensajesUI.error(
                    "Gestión de usuarios",
                    "No se pudo abrir el formulario de usuario.\n\nDetalle: " + MensajesUI.mensajeTecnico(ex),
                    ex);
        }
    }

    private void mostrarVentanaModal(Parent root, String titulo, double ancho, double alto) {
        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        if (Main.getStagePrincipal() != null) {
            ventana.initOwner(Main.getStagePrincipal());
        }
        ventana.setTitle("Pagina-Libreria | " + titulo);
        ventana.setResizable(false);

        Scene escena = new Scene(root, ancho, alto);
        URL css = getClass().getResource("/org/paginalib3/view/style/styles.css");
        if (css != null) escena.getStylesheets().add(css.toExternalForm());

        ventana.setScene(escena);
        ventana.centerOnScreen();
        ventana.showAndWait();
    }

    private Usuario seleccionado() {
        Usuario u = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (u == null) {
            MensajesUI.advertencia("Selecciona un usuario", "Debes seleccionar una fila de la tabla.");
        }
        return u;
    }
}
