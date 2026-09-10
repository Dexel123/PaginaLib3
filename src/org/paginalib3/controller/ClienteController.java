package org.paginalib3.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalib3.dao.ClienteDAO;
import org.paginalib3.dao.impl.ClienteDAOImpl;
import org.paginalib3.model.Cliente;
import org.paginalib3.system.Main;

public class ClienteController {

    @FXML private TextField txtCui, txtNombre, txtApellido, txtCorreo;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, String> colCui, colNombre, colApellido, colCorreo;
    @FXML private Label lblEstado;

    private final ClienteDAO clienteDAO = new ClienteDAOImpl();

    @FXML
    private void initialize() {
        // Enlace de las columnas de la tabla con las propiedades del modelo Cliente
        colCui.setCellValueFactory(new PropertyValueFactory<>("cuiCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellidoCliente"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        cargarClientes();

        // Listener para autocompletar campos al seleccionar un cliente de la tabla
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtCui.setText(newVal.getCuiCliente());
                txtCui.setEditable(false); // El CUI es clave primaria y no debe modificarse
                txtNombre.setText(newVal.getNombreCliente());
                txtApellido.setText(newVal.getApellidoCliente());
                txtCorreo.setText(newVal.getCorreo());
            }
        });
    }

    private void cargarClientes() {
        try {
            tblClientes.setItems(FXCollections.observableArrayList(clienteDAO.listar()));
            lblEstado.setText("Clientes cargados correctamente.");
        } catch (Exception e) {
            lblEstado.setText("Error al cargar clientes: " + e.getMessage());
        }
    }

    @FXML
    private void limpiar() {
        txtCui.clear();
        txtCui.setEditable(true);
        txtNombre.clear();
        txtApellido.clear();
        txtCorreo.clear();
        tblClientes.getSelectionModel().clearSelection();
        lblEstado.setText("");
    }

    @FXML
    private void volver() {
        // Redirige al dashboard principal (puedes cambiar la vista según convenga)
        Main.cambiarVista("/org/paginalib3/view/dashboard_admin.fxml", "Pagina-Libreria | Dashboard", 1100, 680);
    }

    private void alert(Alert.AlertType t, String m) {
        new Alert(t, m, ButtonType.OK).showAndWait();
    }
}