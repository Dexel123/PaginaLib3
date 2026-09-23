package org.paginalib3.controller;

import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalib3.dao.VentaDAO;
import org.paginalib3.dao.impl.VentaDAOImpl;
import org.paginalib3.model.Usuario;
import org.paginalib3.model.Venta;
import org.paginalib3.util.MensajesUI;
import org.paginalib3.util.Permisos;
import org.paginalib3.util.Sesion;

public class DevolucionController {

    @FXML private TextField txtIdVenta;
    @FXML private TextArea txtMotivo;
    @FXML private ComboBox<String> cmbOperacion;
    @FXML private TableView<Venta> tblVentas;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, String> colEstado;
    @FXML private TableColumn<Venta, Double> colTotal;
    @FXML private Label lblEstado;

    private final VentaDAO dao = new VentaDAOImpl();

    @FXML
    private void initialize() {
        if (!Permisos.requerirCaja("Anulaciones y devoluciones")) return;
        cmbOperacion.setItems(FXCollections.observableArrayList("ANULAR", "DEVOLVER"));
        cmbOperacion.setValue("ANULAR");

        colId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        cargarRecientes();
    }

    @FXML
    private void cargarRecientes() {
        Usuario u = Sesion.getUsuarioActual();
        if (u == null || !Permisos.puedeCaja()) return;
        try {
            tblVentas.setItems(FXCollections.observableArrayList(
                    "admin".equalsIgnoreCase(u.getRol())
                            ? dao.listarVentas()
                            : dao.listarVentasDelDiaPorUsuario(u.getId())));
            lblEstado.setText("Ventas recientes actualizadas.");
        } catch (SQLException e) {
            lblEstado.setText("No se pudieron cargar las ventas.");
            MensajesUI.error("Anulaciones y devoluciones",
                    "No fue posible cargar las ventas recientes.\nDetalle: " + MensajesUI.mensajeTecnico(e), e);
        }
    }