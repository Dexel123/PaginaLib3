package org.paginalib3.controller;

import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
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
import org.paginalib3.util.Sesion;
import org.paginalib3.util.MensajesUI;
import org.paginalib3.util.Permisos;

public class SalidaInventarioController {

    @FXML private ComboBox<Libro> cmbLibro;
    @FXML private ComboBox<String> cmbTipoSalida;
    @FXML private TextField txtCantidad;
    @FXML private TextArea txtObservacion;
    @FXML private Label lblStockActual, lblEstado;
    @FXML private TableView<MovimientoInventario> tblMovimientos;
    @FXML private TableColumn<MovimientoInventario, String> colIsbn, colLibro, colTipo;
    @FXML private TableColumn<MovimientoInventario, Integer> colCantidad;
    @FXML private TableColumn<MovimientoInventario, Object> colFecha;

    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final MovimientoInventarioDAO movimientoDAO = new MovimientoInventarioDAOImpl();

    @FXML
    private void initialize() {
        if (!Permisos.requerirInventario("Inventario")) return;
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colLibro.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoMovimiento"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaMovimiento"));
        cmbTipoSalida.setItems(FXCollections.observableArrayList("MERMA", "TRASLADO", "DEVOLUCION_PROVEEDOR"));
        cmbTipoSalida.setValue("MERMA");
        txtCantidad.setText("1");
        cargarDatos();
    }

    @FXML
    private void actualizarStockSeleccionado() {
        Libro libro = cmbLibro.getValue();
        lblStockActual.setText(libro == null ? "-" : String.valueOf(libro.getStockActual()));
    }

    @FXML
    private void registrarSalida() {
        Libro libro = cmbLibro.getValue();
        String tipo = cmbTipoSalida.getValue();
        if (libro == null) { advertencia("Selecciona un libro."); return; }
        if (tipo == null || tipo.isBlank()) { advertencia("Selecciona el tipo de salida."); return; }
        int cantidad;
        try { cantidad = Integer.parseInt(txtCantidad.getText().trim()); }
        catch (Exception e) { advertencia("La cantidad debe ser un entero mayor a 0."); return; }
        if (cantidad <= 0) { advertencia("La cantidad debe ser mayor a 0."); return; }
        if (cantidad > libro.getStockActual()) {
            advertencia("La cantidad supera el stock disponible. Stock actual: " + libro.getStockActual());
            return;
        }
        Usuario usuario = Sesion.getUsuarioActual();
        try {
            movimientoDAO.registrarSalida(libro.getIsbn(), cantidad, tipo, usuario.getId(), txtObservacion.getText());
            informacion("Salida registrada correctamente.");
            txtCantidad.setText("1");
            txtObservacion.clear();
            cargarDatos();
            seleccionarPorIsbn(libro.getIsbn());
            lblEstado.setText("Salida aplicada y stock actualizado.");
        } catch (SQLException e) {
            error("No se pudo registrar la salida: " + mensaje(e));
        }
    }

    @FXML
    private void volver() {
        Permisos.volverDashboardSegunRol();
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

    private String mensaje(SQLException e) { return MensajesUI.mensajeTecnico(e); }
    private void informacion(String m) { MensajesUI.informacion("Inventario", m); }
    private void advertencia(String m) { MensajesUI.advertencia("Revisa los datos", m); }
    private void error(String m) {
        lblEstado.setText(m);
        MensajesUI.error("Inventario", m);
    }
}
