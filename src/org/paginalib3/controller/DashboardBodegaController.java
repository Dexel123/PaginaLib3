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
