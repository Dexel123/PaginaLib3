package org.paginalib3.controller;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalib3.dao.ReporteDAO;
import org.paginalib3.dao.impl.ReporteDAOImpl;
import org.paginalib3.model.ReporteVenta;
import org.paginalib3.system.Main;
import org.paginalib3.util.Sesion;

public class ReporteVentaController {
    @FXML private ComboBox<String> cmbPeriodo;
    @FXML private DatePicker dpDesde, dpHasta;
    @FXML private TableView<ReporteVenta> tablaVentas;
    @FXML private TableColumn<ReporteVenta, LocalDate> colFecha;
    @FXML private TableColumn<ReporteVenta, Integer> colCantidad;
    @FXML private TableColumn<ReporteVenta, Double> colSubtotal, colDescuento, colTotal;
    @FXML private Label lblTotalVentas, lblCantidadVentas, lblEstado;

    private final ReporteDAO reporteDAO = new ReporteDAOImpl();
    private List<ReporteVenta> datosActuales = new ArrayList<>();

    @FXML
    private void initialize() {
        tablaVentas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        if (Sesion.getUsuarioActual() == null || !"admin".equalsIgnoreCase(Sesion.getUsuarioActual().getRol())) { lblEstado.setText("Acceso reservado para administración."); return; }
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadVentas"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuentos"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        cmbPeriodo.setItems(FXCollections.observableArrayList("Hoy", "Esta semana", "Este mes", "Personalizado"));
        cmbPeriodo.getSelectionModel().select("Este mes");
        aplicarPeriodo();
        consultar();
    }

    @FXML private void aplicarPeriodo() {
        String p = cmbPeriodo.getValue();
        LocalDate hoy = LocalDate.now();
        if ("Hoy".equals(p)) { dpDesde.setValue(hoy); dpHasta.setValue(hoy); }
        else if ("Esta semana".equals(p)) {
            dpDesde.setValue(hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
            dpHasta.setValue(hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));
        } else if ("Este mes".equals(p)) {
            dpDesde.setValue(hoy.withDayOfMonth(1)); dpHasta.setValue(hoy.withDayOfMonth(hoy.lengthOfMonth()));
        }
        boolean personal = "Personalizado".equals(p);
        dpDesde.setDisable(!personal); dpHasta.setDisable(!personal);
    }

    @FXML private void consultar() {
        try {
            datosActuales = reporteDAO.ventasPorPeriodo(dpDesde.getValue(), dpHasta.getValue());
            tablaVentas.setItems(FXCollections.observableArrayList(datosActuales));
            double total = datosActuales.stream().mapToDouble(ReporteVenta::getTotal).sum();
            int cantidad = datosActuales.stream().mapToInt(ReporteVenta::getCantidadVentas).sum();
            lblTotalVentas.setText(moneda(total)); lblCantidadVentas.setText(String.valueOf(cantidad));
            lblEstado.setText(datosActuales.isEmpty() ? "No hay ventas en el período seleccionado." : "Reporte actualizado.");
        } catch (SQLException | IllegalArgumentException e) { lblEstado.setText("No se pudo generar el reporte: " + e.getMessage()); }
    }

    @FXML private void volver() { Main.cambiarVista("/org/paginalib3/view/dashboard_admin.fxml", "Pagina-Libreria | Administración", 1180, 720); }
    private String moneda(double v) { return String.format("Q%,.2f", v); }
}
