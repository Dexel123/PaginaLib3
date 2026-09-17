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
import org.paginalib3.util.Sesion;

public class DashboardBodegaController extends DashboardBaseController {

    @FXML
    private Label lblCriticos, lblEstadoInventario;
    @FXML
    private TableView<Libro> tblCriticos;
    @FXML
    private TableColumn<Libro, String> colIsbn, colTitulo;
    @FXML
    private TableColumn<Libro, Integer> colStockActual, colStockMinimo;
    private final LibroDAO dao = new LibroDAOImpl();

    @Override
    protected String rolPermitido() {
        return "bodega";
    }

    @Override
    protected String mensajeRol() {
        return "Control de inventario y existencias de la librería.";
    }

    @Override
    @FXML
    protected void initialize() {
        super.initialize();
        if (Sesion.getUsuarioActual() == null) {
            return;
        }
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        actualizarStockCritico();
    }

    @FXML
    private void actualizarStockCritico() {
        try {
            List<Libro> l = dao.listarStockCritico();
            tblCriticos.setItems(FXCollections.observableArrayList(l));
            lblCriticos.setText(String.valueOf(l.size()));
            lblEstadoInventario.setText(l.isEmpty() ? "Inventario sin alertas críticas." : l.size() + " libro(s) en nivel crítico.");
        } catch (SQLException e) {
            lblEstadoInventario.setText(e.getMessage());
        }
    }
