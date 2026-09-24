package org.paginalib3.controller;

import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalib3.dao.LibroDAO;
import org.paginalib3.dao.impl.LibroDAOImpl;
import org.paginalib3.model.Libro;
import org.paginalib3.system.Main;
import org.paginalib3.util.Sesion;
import org.paginalib3.util.MensajesUI;

public class DashboardBodegaController extends DashboardBaseController {

    @FXML private Label lblCriticos, lblEstadoInventario;
    @FXML private TableView<Libro> tblCriticos;
    @FXML private TableColumn<Libro, String> colIsbn, colTitulo;
    @FXML private TableColumn<Libro, Integer> colStockActual, colStockMinimo;

    private final LibroDAO libroDAO = new LibroDAOImpl();

    @Override
    protected String rolPermitido() { return "bodega"; }

    @Override
    protected String mensajeRol() { return "Control de inventario, catálogo, entradas, salidas y alertas de stock."; }

    @Override
    @FXML
    protected void initialize() {
        super.initialize();
        if (Sesion.getUsuarioActual() == null || !"bodega".equalsIgnoreCase(Sesion.getUsuarioActual().getRol())) return;
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        actualizarStockCritico();
    }

    @FXML
    private void actualizarStockCritico() {
        try {
            List<Libro> criticos = libroDAO.listarStockCritico();
            tblCriticos.setItems(FXCollections.observableArrayList(criticos));
            lblCriticos.setText(String.valueOf(criticos.size()));
            lblEstadoInventario.setText(criticos.isEmpty() ? "Inventario sin alertas críticas." : criticos.size() + " libro(s) en nivel crítico.");
        } catch (SQLException e) {
            MensajesUI.registrarError(e);
            lblCriticos.setText("-");
            lblEstadoInventario.setText("No se pudo consultar inventario: " + MensajesUI.mensajeTecnico(e));
        }
    }

    @FXML private void irALibros() { Main.cambiarVista("/org/paginalib3/view/libros.fxml", "Pagina-Libreria | Gestión de libros", 1180, 720); }
    @FXML private void irAIngreso() { Main.cambiarVista("/org/paginalib3/view/ingreso_inventario.fxml", "Pagina-Libreria | Ingreso de inventario", 1120, 720); }
    @FXML private void irASalida() { Main.cambiarVista("/org/paginalib3/view/salida_inventario.fxml", "Pagina-Libreria | Salida de inventario", 1120, 720); }
    @FXML private void irAStockCritico() { Main.cambiarVista("/org/paginalib3/view/stock_critico.fxml", "Pagina-Libreria | Stock crítico", 1050, 680); }
}
