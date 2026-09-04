package org.paginalib3.controller;

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
import org.paginalib3.system.Main;
import org.paginalib3.dao.UsuarioDAO;
import org.paginalib3.dao.impl.UsuarioDAOImpl;
import org.paginalib3.model.Usuario;

import java.io.IOException;
import java.net.URL;
import java.util.function.Function;


public class UsuariosController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @FXML
    private TableView<Usuario> tablaUsuarios;
    @FXML
    private TableColumn<Usuario, String> colUsername;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colRol;
    @FXML
    private TableColumn<Usuario, String> colEstado;

    @FXML
    private void initialize() {
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
            alerta(Alert.AlertType.ERROR, "Error de conexion", ex.getMessage());
        }
    }

    @FXML
    private void registrar() {
        UsuarioFormController controller = abrirFormularioUsuario("Registrar usuario");
        if (controller != null) {
            controller.initModoRegistro();
            controller.setOnGuardadoExitoso(this::cargar);
        }
    }

    @FXML
    private void editar() {
        Usuario seleccionado = seleccionado();
        if (seleccionado == null) {
            return;
        }
        UsuarioFormController controller = abrirFormularioUsuario("Editar usuario");
        if (controller != null) {
            controller.initModoEdicion(seleccionado);
            controller.setOnGuardadoExitoso(this::cargar);
        }
    }

    @FXML
    private void cambiarRol() {
        Usuario seleccionado = seleccionado();
        if (seleccionado == null) {
            return;
        }
        try {
            URL url = getClass().getResource("/org/paginalib3/view/cambiar_rol.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            CambiarRolController controller = loader.getController();
            controller.init(seleccionado);
            controller.setOnGuardadoExitoso(this::cargar);

            mostrarVentanaModal(root, "Cambiar rol");
        } catch (IOException ex) {
            alerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana: " + ex.getMessage());
        }
    }

    @FXML
    private void cambiarEstado() {
        Usuario u = seleccionado();
        if (u == null) {
            return;
        }
        try {
            usuarioDAO.cambiarEstado(u.getId(), !u.isActivo());
            cargar();
        } catch (Exception ex) {
            alerta(Alert.AlertType.ERROR, "Error", ex.getMessage());
        }
    }

    @FXML
    private void volver() {
        Main.cambiarVista("/org/paginalib3/view/dashboard_admin.fxml",
                "Pagina-Libreria | Dashboard Administrador", 1100, 680);
    }

    private UsuarioFormController abrirFormularioUsuario(String titulo) {
        try {
            URL url = getClass().getResource("/org/paginalib3/view/usuario_form.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            mostrarVentanaModal(root, titulo);
            return loader.getController();
        } catch (IOException ex) {
            alerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana: " + ex.getMessage());
            return null;
        }
    }

    private void mostrarVentanaModal(Parent root, String titulo) {
        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle(titulo);
        Scene escena = new Scene(root, 380, 460);
        URL css = getClass().getResource("/org/paginalib3/view/styles.css");
        if (css != null) {
            escena.getStylesheets().add(css.toExternalForm());
        }
        ventana.setScene(escena);
        ventana.showAndWait();
    }

    private Usuario seleccionado() {
        Usuario u = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (u == null) {
            alerta(Alert.AlertType.WARNING, "Selecciona un usuario", "Debes seleccionar una fila.");
        }
        return u;
    }

    private void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
