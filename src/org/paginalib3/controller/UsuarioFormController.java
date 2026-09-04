package org.paginalib3.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.paginalib3.dao.UsuarioDAO;
import org.paginalib3.dao.impl.UsuarioDAOImpl;
import org.paginalib3.model.Usuario;
import org.paginalib3.util.Seguridad;

public class UsuarioFormController {

    private enum Modo { CREAR, EDITAR }

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    private Modo modo = Modo.CREAR;
    private Usuario usuarioEnEdicion;
    private Runnable onGuardadoExitoso;

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtCorreo;
    @FXML
    private ComboBox<String> cmbRol;
    @FXML
    private Label lblEstado;

    @FXML
    private void initialize() {
        cmbRol.setItems(FXCollections.observableArrayList("admin", "bodega", "cajero"));
        cmbRol.setValue("cajero");
    }

    /** Configura el formulario para registrar un usuario nuevo (US-1.2). */
    public void initModoRegistro() {
        modo = Modo.CREAR;
        lblTitulo.setText("Registrar usuario");
    }

    /** Configura el formulario para editar los datos de un usuario existente. */
    public void initModoEdicion(Usuario usuario) {
        modo = Modo.EDITAR;
        usuarioEnEdicion = usuario;
        lblTitulo.setText("Editar usuario");

        txtUsername.setText(usuario.getUsername());
        txtUsername.setDisable(true);
        txtPassword.setDisable(true);
        txtPassword.setVisible(false);
        txtPassword.setManaged(false);
        cmbRol.setValue(usuario.getRol());
        cmbRol.setDisable(true);

        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtCorreo.setText(usuario.getCorreo());
    }

    public void setOnGuardadoExitoso(Runnable onGuardadoExitoso) {
        this.onGuardadoExitoso = onGuardadoExitoso;
    }

    @FXML
    private void guardar() {
        lblEstado.setText("");
        try {
            if (modo == Modo.CREAR) {
                registrar();
            } else {
                actualizar();
            }
            if (onGuardadoExitoso != null) {
                onGuardadoExitoso.run();
            }
            cerrar();
        } catch (Exception ex) {
            lblEstado.setText(ex.getMessage());
        }
    }

    private void registrar() throws Exception {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String correo = txtCorreo.getText();
        String rol = cmbRol.getValue();

        if (isBlank(username) || isBlank(password) || isBlank(nombre) || isBlank(apellido) || isBlank(correo)) {
            throw new IllegalArgumentException("Completa todos los campos.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("La contrasena debe tener al menos 6 caracteres.");
        }
        if (!correo.contains("@")) {
            throw new IllegalArgumentException("Ingresa un correo valido.");
        }

        Usuario nuevo = new Usuario(0, username, nombre, apellido, correo, rol, true);
        usuarioDAO.registrar(nuevo, Seguridad.sha256(password));
    }

    private void actualizar() throws Exception {
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String correo = txtCorreo.getText();

        if (isBlank(nombre) || isBlank(apellido) || isBlank(correo)) {
            throw new IllegalArgumentException("Nombre, apellido y correo son obligatorios.");
        }
        usuarioDAO.actualizarDatos(usuarioEnEdicion.getId(), nombre, apellido, correo);
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private void cerrar() {
        ((Stage) lblTitulo.getScene().getWindow()).close();
    }
}
