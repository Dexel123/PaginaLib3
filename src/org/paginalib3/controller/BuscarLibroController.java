package org.paginalib3.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.paginalib3.dao.LibroDAO;
import org.paginalib3.dao.impl.LibroDAOImpl;
import org.paginalib3.model.Libro;
import org.paginalib3.system.Main;

import java.sql.SQLException;

public class BuscarLibroController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Libro> tblLibros;
    @FXML private TableColumn<Libro, String> colIsbn, colTitulo, colAutores;
    @FXML private TableColumn<Libro, Double> colPrecio;
    @FXML private TableColumn<Libro, Integer> colStock;
    @FXML private Label lblEstado;

    private final LibroDAO dao = new LibroDAOImpl();

    @FXML
    private void initialize() {
        colIsbn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("titulo"));
        colAutores.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("autores"));
        colPrecio.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("stockActual"));
        buscar();
    }

    @FXML
    private void buscar() {
        try {
            var lista = dao.buscar(txtBuscar.getText());
            tblLibros.setItems(FXCollections.observableArrayList(lista));
            lblEstado.setText(lista.size() + " libro(s) encontrado(s).");
        } catch (SQLException e) {
            lblEstado.setText("Error al consultar libros: " + e.getMessage());
        }
    }

    @FXML
    private void limpiar() {
        txtBuscar.clear();
        buscar();
    }

    @FXML
    private void irACaja() {
        Main.cambiarVista("/org/paginalib3/view/venta.fxml", "Pagina-Libreria | Registrar venta", 1200, 760);
    }

    @FXML
    private void volver() {
        Main.cambiarVista("/org/paginalib3/view/dashboard_cajero.fxml", "Pagina-Libreria | Dashboard Caja", 1100, 680);
    }
}