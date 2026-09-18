package org.paginalib3.controller;

import java.sql.SQLException;
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
import org.paginalib3.dao.VentaDAO;
import org.paginalib3.dao.impl.VentaDAOImpl;
import org.paginalib3.model.Usuario;
import org.paginalib3.model.Venta;
import org.paginalib3.system.Main;
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
        if (u == null || !"cajero".equalsIgnoreCase(u.getRol())) {
            return;
        }
        try {
            tblVentas.setItems(FXCollections.observableArrayList(dao.listarVentasDelDiaPorUsuario(u.getId())));
        } catch (SQLException e) {
            lblEstado.setText("No se pudieron cargar ventas: " + e.getMessage());
        }
    }

    @FXML
    private void seleccionarVenta() {
        Venta v = tblVentas.getSelectionModel().getSelectedItem();
        if (v != null) {
            txtIdVenta.setText(String.valueOf(v.getIdVenta()));
        }
    }

    @FXML
    private void ejecutar() {
        Usuario u = Sesion.getUsuarioActual();
        if (u == null) {
            lblEstado.setText("No hay sesión activa.");
            return;
        }
        try {
            int id = Integer.parseInt(txtIdVenta.getText().trim());
            String motivo = txtMotivo.getText() == null ? "" : txtMotivo.getText().trim();
            
            if (motivo.isBlank()) {
                throw new IllegalArgumentException("El motivo es obligatorio.");
            }
            
            Venta venta = dao.buscarPorId(id);
            if (venta == null) {
                throw new IllegalArgumentException("La venta no existe.");
            }
            if (!"COMPLETADA".equalsIgnoreCase(venta.getEstado())) {
                throw new IllegalArgumentException("La venta ya no está disponible para esta operación.");
            }
            
            boolean ok;
            if ("DEVOLVER".equals(cmbOperacion.getValue())) {
                ok = dao.devolverVenta(id, u.getId(), motivo);
            } else {
                ok = dao.anularVenta(id, u.getId(), motivo);
            }
            
            if (ok) {
                new Alert(Alert.AlertType.INFORMATION, "Operación realizada correctamente para la venta #" + id, ButtonType.OK).showAndWait();
                cargarRecientes();
                txtMotivo.clear();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo completar la operación: " + e.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    @FXML
    private void volver() {
        Main.cambiarVista("/org/paginalib3/view/dashboard_cajero.fxml", "Pagina-Libreria | Dashboard Caja", 1100, 680);
    }
}