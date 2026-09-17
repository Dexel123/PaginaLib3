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
        if (!esBodega()) return;
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colLibro.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoMovimiento"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaMovimiento"));
        cmbTipoSalida.setItems(FXCollections.observableArrayList("MERMA", "TRASLADO", "DEVOLUCION"));
        cmbTipoSalida.setValue("MERMA");
        txtCantidad.setText("1");
        cargarDatos();
    }

    @FXML
    private void actualizarStockSeleccionado() {
        Libro libro = cmbLibro.getValue();
        lblStockActual.setText(libro == null ? "-" : String.valueOf(libro.getStockActual()));
    }
    }
