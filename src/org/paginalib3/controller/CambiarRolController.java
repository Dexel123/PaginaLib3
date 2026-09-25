package org.paginalib3.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.paginalib3.dao.UsuarioDAO;
import org.paginalib3.dao.impl.UsuarioDAOImpl;
import org.paginalib3.model.Usuario;

public class CambiarRolController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    private Usuario usuario;
    private Runnable onGuardadoExitoso;

    @FXML
    private Label lblUsuario;
    @FXML
    private ComboBox<String> cmbRol;
    @FXML
    private Label lblEstado;
    @FXML
    private Button btnGuardar;

    @FXML
    private void initialize() {
        cmbRol.setItems(FXCollections.observableArrayList("bodega", "cajero"));
    }

    public void init(Usuario usuario) {
        this.usuario = usuario;
        lblUsuario.setText("Usuario: " + usuario.getUsername());
        cmbRol.setValue(usuario.getRol());
        boolean esAdmin = "admin".equalsIgnoreCase(usuario.getRol());
        cmbRol.setDisable(esAdmin);
        btnGuardar.setDisable(esAdmin);
        if (esAdmin) {
            lblEstado.setText("Los usuarios con rol admin no pueden cambiar de rol.");
        }
    }

    public void setOnGuardadoExitoso(Runnable onGuardadoExitoso) {
        this.onGuardadoExitoso = onGuardadoExitoso;
    }

    @FXML
    private void guardar() {
        lblEstado.setText("");
        try {
            String rolSeleccionado = cmbRol.getValue();
            if ("admin".equalsIgnoreCase(usuario.getRol())) {
                throw new IllegalArgumentException("El rol administrador no puede modificarse.");
            }
            if (rolSeleccionado == null || "admin".equalsIgnoreCase(rolSeleccionado)) {
                throw new IllegalArgumentException("Selecciona un rol.");
            }
            usuarioDAO.cambiarRol(usuario.getId(), rolSeleccionado);
            if (onGuardadoExitoso != null) {
                onGuardadoExitoso.run();
            }
            cerrar();
        } catch (Exception ex) {
            lblEstado.setText(ex.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        ((Stage) lblUsuario.getScene().getWindow()).close();
    }
}
