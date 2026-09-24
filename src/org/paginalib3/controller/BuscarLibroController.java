package org.paginalib3.controller;

import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalib3.dao.LibroDAO;
import org.paginalib3.dao.impl.LibroDAOImpl;
import org.paginalib3.model.Libro;
import org.paginalib3.system.Main;
import org.paginalib3.util.MensajesUI;
import org.paginalib3.util.Permisos;

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
        if (!Permisos.requerirCaja("Búsqueda de libros para caja")) return;
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutores.setCellValueFactory(new PropertyValueFactory<>("autores"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        buscar();
    }

    @FXML
    private void buscar() {
        try {
            var lista = dao.buscar(txtBuscar.getText());
            tblLibros.setItems(FXCollections.observableArrayList(lista));
            lblEstado.setText(lista.size() + " libro(s) encontrado(s).");
        } catch (SQLException e) {
            lblEstado.setText("No se pudieron consultar los libros.");
            MensajesUI.error("Búsqueda de libros",
                    "No fue posible consultar el catálogo.\nDetalle: " + MensajesUI.mensajeTecnico(e), e);
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
        Permisos.volverDashboardSegunRol();
    }
}
