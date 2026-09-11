package org.paginalib3.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalib3.dao.InventarioDAO;
import org.paginalib3.dao.LibroDAO;
import org.paginalib3.dao.ProveedorDAO;
import org.paginalib3.dao.impl.InventarioDAOImpl;
import org.paginalib3.dao.impl.LibroDAOImpl;
import org.paginalib3.dao.impl.ProveedorDAOImpl;
import org.paginalib3.model.Libro;
import org.paginalib3.model.Proveedor;
import org.paginalib3.model.Usuario;
import org.paginalib3.system.Main;
import org.paginalib3.util.Sesion;

import java.sql.SQLException;

public class SalidaInventarioController {

    @FXML private ComboBox<Libro> cmbLibro;
    @FXML private ComboBox<String> cmbTipoSalida;
    @FXML private ComboBox<Proveedor> cmbProveedor;
    @FXML private TextField txtCantidad;
    @FXML private TextArea txtMotivo;
    @FXML private Label lblStock;
    @FXML private Label lblEstado;
    @FXML private Label lblProveedor;
    @FXML private TableView<Libro> tblStock;
    @FXML private TableColumn<Libro, String> colIsbn;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, Integer> colStock;
    @FXML private TableColumn<Libro, Integer> colMinimo;

    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final InventarioDAO inventarioDAO = new InventarioDAOImpl();
    private final ProveedorDAO proveedorDAO = new ProveedorDAOImpl();

    @FXML
    private void initialize() {
        cmbTipoSalida.setItems(FXCollections.observableArrayList(
                "MERMA", "TRASLADO", "DEVOLUCIÓN A PROVEEDOR"
        ));
        cmbTipoSalida.setValue("MERMA");

        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));

        cmbLibro.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, actual) -> actualizarStock(actual));
        cmbTipoSalida.valueProperty().addListener(
                (obs, anterior, actual) -> actualizarProveedor(actual));

        cargarLibros();
        cargarProveedores();
        actualizarProveedor(cmbTipoSalida.getValue());
    }

    private void cargarLibros() {
        try {
            var lista = libroDAO.listarStockDisponible();
            cmbLibro.setItems(FXCollections.observableArrayList(lista));
            tblStock.setItems(FXCollections.observableArrayList(lista));
            lblEstado.setText(lista.size() + " libro(s) disponible(s) para salida.");
        } catch (SQLException e) {
            lblEstado.setText("No se pudo consultar el stock: " + e.getMessage());
        }
    }

    private void cargarProveedores() {
        try {
            cmbProveedor.setItems(FXCollections.observableArrayList(proveedorDAO.listarActivos()));
        } catch (SQLException e) {
            lblEstado.setText("No se pudieron cargar proveedores: " + e.getMessage());
        }
    }

    private void actualizarStock(Libro libro) {
        if (libro == null) {
            lblStock.setText("Stock disponible: --");
        } else {
            lblStock.setText("Stock disponible: " + libro.getStockActual());
        }
    }

    private void actualizarProveedor(String tipo) {
        boolean devolver = "DEVOLUCIÓN A PROVEEDOR".equalsIgnoreCase(tipo);
        cmbProveedor.setDisable(!devolver);
        lblProveedor.setDisable(!devolver);
        if (!devolver) {
            cmbProveedor.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void seleccionarLibro() {
        actualizarStock(cmbLibro.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void registrarSalida() {
        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario == null || !"bodega".equalsIgnoreCase(usuario.getRol())) {
            mostrarError("No hay una sesión de bodega válida.");
            return;
        }

        try {
            Libro libro = cmbLibro.getSelectionModel().getSelectedItem();
            String tipo = cmbTipoSalida.getValue();
            String cantidadTexto = txtCantidad.getText() == null ? "" : txtCantidad.getText().trim();
            String motivo = txtMotivo.getText() == null ? "" : txtMotivo.getText().trim();

            if (libro == null) {
                throw new IllegalArgumentException("Seleccione un libro.");
            }
            if (tipo == null || tipo.isBlank()) {
                throw new IllegalArgumentException("Seleccione el tipo de salida.");
            }
            if (!cantidadTexto.matches("\\d+")) {
                throw new IllegalArgumentException("La cantidad debe ser un número entero mayor que 0.");
            }

            int cantidad = Integer.parseInt(cantidadTexto);
            if (cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
            }
            if (cantidad > libro.getStockActual()) {
                throw new IllegalArgumentException(
                        "La cantidad solicitada supera el stock disponible (" + libro.getStockActual() + ").");
            }
            if (motivo.isBlank()) {
                throw new IllegalArgumentException("Ingrese la observación o motivo de la salida.");
            }

            String nitProveedor = null;
            if ("DEVOLUCIÓN A PROVEEDOR".equalsIgnoreCase(tipo)) {
                Proveedor proveedor = cmbProveedor.getSelectionModel().getSelectedItem();
                if (proveedor == null) {
                    throw new IllegalArgumentException("Seleccione el proveedor para la devolución.");
                }
                nitProveedor = proveedor.getNitProveedor();
            }

            String tipoBD = "DEVOLUCIÓN A PROVEEDOR".equalsIgnoreCase(tipo) ? "DEVOLUCION" : tipo;
            inventarioDAO.registrarSalida(
                    libro.getIsbn(), cantidad, tipoBD, usuario.getId(), motivo, nitProveedor);

            mostrarInfo("Salida registrada correctamente. Se descontaron " + cantidad
                    + " unidad(es) de " + libro.getTitulo() + ".");
            limpiarFormulario();
            cargarLibros();
        } catch (NumberFormatException e) {
            mostrarError("La cantidad ingresada no es válida.");
        } catch (IllegalArgumentException | SQLException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void limpiarFormulario() {
        cmbLibro.getSelectionModel().clearSelection();
        txtCantidad.clear();
        txtMotivo.clear();
        cmbProveedor.getSelectionModel().clearSelection();
        lblStock.setText("Stock disponible: --");
    }

    @FXML
    private void volver() {
        Main.cambiarVista("/org/paginalib3/view/dashboard_bodega.fxml",
                "Pagina-Libreria | Dashboard Bodega", 1100, 680);
    }

    private void mostrarInfo(String mensaje) {
        new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK).showAndWait();
    }

    private void mostrarError(String mensaje) {
        new Alert(Alert.AlertType.ERROR, mensaje == null ? "Ocurrió un error." : mensaje,
                ButtonType.OK).showAndWait();
    }
}
