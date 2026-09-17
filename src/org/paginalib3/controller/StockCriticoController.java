package org.paginalib3.controller;

import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalib3.dao.LibroDAO;
import org.paginalib3.dao.impl.LibroDAOImpl;
import org.paginalib3.model.Libro;
import org.paginalib3.system.Main;

public class StockCriticoController {

    @FXML
    private Label lblCantidad, lblEstado;
    @FXML
    private TableView<Libro> tblCriticos;
    @FXML
    private TableColumn<Libro, String> colIsbn, colTitulo;
    @FXML
    private TableColumn<Libro, Integer> colStockActual, colStockMinimo;
    private final LibroDAO dao = new LibroDAOImpl();

    @FXML
    private void initialize() {
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        actualizar();
    }

    @FXML
    private void actualizar() {
        try {
            List<Libro> l = dao.listarStockCritico();
            tblCriticos.setItems(FXCollections.observableArrayList(l));
            lblCantidad.setText(String.valueOf(l.size()));
            lblEstado.setText(l.isEmpty() ? "Sin alertas de stock." : l.size() + " libro(s) requieren atención.");
        } catch (SQLException e) {
            lblEstado.setText(e.getMessage());
        }
    }

    @FXML
    private void volver() {
        Main.cambiarVista("/org/paginalib3/view/dashboard_bodega.fxml", "Pagina-Libreria | Dashboard Bodega", 1100, 680);
    }
}
