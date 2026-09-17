package org.paginalib3.controller;

import java.sql.SQLException;
import java.util.List;
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
import org.paginalib3.dao.LibroDAO;
import org.paginalib3.dao.MovimientoInventarioDAO;
import org.paginalib3.dao.impl.LibroDAOImpl;
import org.paginalib3.dao.impl.MovimientoInventarioDAOImpl;
import org.paginalib3.model.Libro;
import org.paginalib3.model.MovimientoInventario;
import org.paginalib3.model.Usuario;
import org.paginalib3.system.Main;
import org.paginalib3.util.Sesion;

public class IngresoInventarioController {

    @FXML private ComboBox<Libro> cmbLibro;
    @FXML private TextField txtCantidad;
    @FXML private TextArea txtObservacion;
    @FXML private Label lblStockActual;
    @FXML private Label lblEstado;
    @FXML private TableView<MovimientoInventario> tblMovimientos;
    @FXML private TableColumn<MovimientoInventario, String> colIsbn;
    @FXML private TableColumn<MovimientoInventario, String> colLibro;
    @FXML private TableColumn<MovimientoInventario, String> colTipo;
    @FXML private TableColumn<MovimientoInventario, Integer> colCantidad;
    @FXML private TableColumn<MovimientoInventario, Object> colFecha;

    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final MovimientoInventarioDAO movimientoDAO = new MovimientoInventarioDAOImpl();

    @FXML
    private void initialize() {
        if (!esBodega()) {
            return;
        }
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colLibro.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoMovimiento"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaMovimiento"));
        txtCantidad.setText("1");
        cargarDatos();
    }

    @FXML
    private void actualizarStockSeleccionado() {
        Libro libro = cmbLibro.getValue();
        lblStockActual.setText(libro == null ? "-" : String.valueOf(libro.getStockActual()));
    }

    @FXML
    private void registrarIngreso() {
        Libro libro = cmbLibro.getValue();
        if (libro == null) {
            advertencia("Selecciona un libro.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (Exception e) {
            advertencia("La cantidad debe ser un entero mayor a 0.");
            return;
        }

        if (cantidad <= 0) {
            advertencia("La cantidad debe ser mayor a 0.");
            return;
        }

        Usuario usuario = Sesion.getUsuarioActual();
        try {
            movimientoDAO.registrarIngreso(libro.getIsbn(), cantidad, usuario.getId(), txtObservacion.getText());
            informacion("Ingreso registrado correctamente.");
            txtCantidad.setText("1");
            txtObservacion.clear();
            cargarDatos();
            seleccionarPorIsbn(libro.getIsbn());
            lblEstado.setText("Ingreso aplicado y stock actualizado.");
        } catch (SQLException e) {
            error("No se pudo registrar el ingreso: " + mensaje(e));
        }
    }

    @FXML
    private void volver() {
        Main.cambiarVista("/org/paginalib3/view/dashboard_bodega.fxml", "Pagina-Libreria | Dashboard Bodega", 1180, 720);
    }

    private void cargarDatos() {
        try {
            List<Libro> libros = libroDAO.listar();
            cmbLibro.setItems(FXCollections.observableArrayList(libros));
            tblMovimientos.setItems(FXCollections.observableArrayList(movimientoDAO.listar()));
            lblEstado.setText("Inventario actualizado.");
            actualizarStockSeleccionado();
        } catch (SQLException e) {
            error("No se pudieron cargar los datos: " + mensaje(e));
        }
    }

    private void seleccionarPorIsbn(String isbn) {
        for (Libro libro : cmbLibro.getItems()) {
            if (libro.getIsbn().equals(isbn)) {
                cmbLibro.setValue(libro);
                actualizarStockSeleccionado();
                return;
            }
        }
    }

    private boolean esBodega() {
        if (Sesion.getUsuarioActual() != null && "bodega".equalsIgnoreCase(Sesion.getUsuarioActual().getRol())) {
            return true;
        }
        Main.cambiarVista("/org/paginalib3/view/login.fxml", "Pagina-Libreria | Iniciar sesión", 760, 560);
        return false;
    }

    private String mensaje(SQLException e) {
        return e.getMessage() == null ? "Error de base de datos" : e.getMessage();
    }

    private void informacion(String m) {
        new Alert(Alert.AlertType.INFORMATION, m, ButtonType.OK).showAndWait();
    }

    private void advertencia(String m) {
        new Alert(Alert.AlertType.WARNING, m, ButtonType.OK).showAndWait();
    }

    private void error(String m) {
        lblEstado.setText(m);
        new Alert(Alert.AlertType.ERROR, m, ButtonType.OK).showAndWait();
    }
}