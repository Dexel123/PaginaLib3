package org.paginalib3.controller;

import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalib3.dao.LibroDAO;
import org.paginalib3.dao.impl.LibroDAOImpl;
import org.paginalib3.model.Libro;
import org.paginalib3.system.Main;
import org.paginalib3.util.Sesion;

public class StockCriticoController {

    @FXML private Label lblCantidad, lblEstado;
    @FXML private TableView<Libro> tblCriticos;
    @FXML private TableColumn<Libro, String> colIsbn, colTitulo, colCategoria;
    @FXML private TableColumn<Libro, Integer> colStockActual, colStockMinimo;

    private final LibroDAO libroDAO = new LibroDAOImpl();

    @FXML
    private void initialize() {
        if (!esBodega()) return;
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        actualizar();
    }

    @FXML
    private void actualizar() {
        try {
            List<Libro> libros = libroDAO.listarStockCritico();
            tblCriticos.setItems(FXCollections.observableArrayList(libros));
            lblCantidad.setText(String.valueOf(libros.size()));
            lblEstado.setText(libros.isEmpty() ? "No hay productos en stock crítico." : libros.size() + " producto(s) requieren atención.");
        } catch (SQLException e) {
            error("No se pudo consultar el stock crítico: " + (e.getMessage() == null ? "Error de base de datos" : e.getMessage()));
        }
    }

    @FXML
    private void abrirFicha() {
        Main.cambiarVista("/org/paginalib3/view/libros.fxml", "Pagina-Libreria | Gestión de libros", 1180, 720);
    }

    @FXML
    private void volver() {
        Main.cambiarVista("/org/paginalib3/view/dashboard_bodega.fxml", "Pagina-Libreria | Dashboard Bodega", 1180, 720);
    }

    private boolean esBodega() {
        if (Sesion.getUsuarioActual() != null && "bodega".equalsIgnoreCase(Sesion.getUsuarioActual().getRol())) return true;
        Main.cambiarVista("/org/paginalib3/view/login.fxml", "Pagina-Libreria | Iniciar sesión", 760, 560);
        return false;
    }

    private void error(String m) { lblEstado.setText(m); new Alert(Alert.AlertType.ERROR, m, ButtonType.OK).showAndWait(); }
}
