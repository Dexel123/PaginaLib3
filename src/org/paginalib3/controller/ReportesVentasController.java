package org.paginalib3.controller;

import java.io.File;
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
import javafx.stage.FileChooser;
import org.paginalib3.dao.ReporteDAO;
import org.paginalib3.dao.impl.ReporteDAOImpl;
import org.paginalib3.model.ReporteVenta;
import org.paginalib3.system.Main;
import org.paginalib3.util.ExportadorCSV;
import org.paginalib3.util.Permisos;
import org.paginalib3.util.MensajesUI;

public class ReportesVentasController {

    @FXML
    private ComboBox<String> cmbPeriodo;
    @FXML
    private DatePicker dpDesde, dpHasta;
    @FXML
    private TableView<ReporteVenta> tablaVentas;
    @FXML
    private TableColumn<ReporteVenta, LocalDate> colFecha;
    @FXML
    private TableColumn<ReporteVenta, Integer> colCantidad;
    @FXML
    private TableColumn<ReporteVenta, Double> colSubtotal, colDescuento, colTotal;
    @FXML
    private Label lblTotalVentas, lblCantidadVentas, lblEstado;

    private final ReporteDAO reporteDAO = new ReporteDAOImpl();
    private List<ReporteVenta> datosActuales = new ArrayList<>();

    @FXML
    private void initialize() {
        if (!Permisos.requerirAdmin("Reportes de ventas")) {
            return;
        }
        tablaVentas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
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

    @FXML
    private void aplicarPeriodo() {
        String p = cmbPeriodo.getValue();
        LocalDate hoy = LocalDate.now();
        if ("Hoy".equals(p)) {
            dpDesde.setValue(hoy);
            dpHasta.setValue(hoy);
        } else if ("Esta semana".equals(p)) {
            dpDesde.setValue(hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
            dpHasta.setValue(hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));
        } else if ("Este mes".equals(p)) {
            dpDesde.setValue(hoy.withDayOfMonth(1));
            dpHasta.setValue(hoy.withDayOfMonth(hoy.lengthOfMonth()));
        }
        boolean personal = "Personalizado".equals(p);
        dpDesde.setDisable(!personal);
        dpHasta.setDisable(!personal);
    }

    @FXML
    private void consultar() {
        try {
            datosActuales = reporteDAO.ventasPorPeriodo(dpDesde.getValue(), dpHasta.getValue());
            tablaVentas.setItems(FXCollections.observableArrayList(datosActuales));
            double total = datosActuales.stream().mapToDouble(ReporteVenta::getTotal).sum();
            int cantidad = datosActuales.stream().mapToInt(ReporteVenta::getCantidadVentas).sum();
            lblTotalVentas.setText(moneda(total));
            lblCantidadVentas.setText(String.valueOf(cantidad));
            lblEstado.setText(datosActuales.isEmpty() ? "No hay ventas en el período seleccionado." : "Reporte actualizado.");
        } catch (SQLException | IllegalArgumentException e) {
            lblEstado.setText("No se pudo generar el reporte: " + MensajesUI.mensajeTecnico(e));
            MensajesUI.error("Reportes de ventas", "No se pudo generar el reporte.\n\nDetalle: " + MensajesUI.mensajeTecnico(e), e);
        }
    }

    @FXML
    private void exportarExcel() {
        if (datosActuales.isEmpty()) {
            lblEstado.setText("Primero genera un reporte con datos.");
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Exportar reporte de ventas");
        fc.setInitialFileName("reporte_ventas_" + dpDesde.getValue() + "_" + dpHasta.getValue() + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo CSV compatible con Excel", "*.csv"));
        File f = fc.showSaveDialog(Main.getStagePrincipal());
        if (f == null) {
            return;
        }
        try {
            List<String[]> filas = new ArrayList<>();
            filas.add(new String[]{"Fecha", "Cantidad de ventas", "Subtotal", "Descuentos", "Total"});
            for (ReporteVenta r : datosActuales) {
                filas.add(new String[]{r.getFecha().toString(), String.valueOf(r.getCantidadVentas()),
                    num(r.getSubtotal()), num(r.getDescuentos()), num(r.getTotal())});
            }
            ExportadorCSV.guardar(f, filas);
            lblEstado.setText("Reporte exportado: " + f.getName());
        } catch (Exception e) {
            lblEstado.setText("No se pudo exportar: " + MensajesUI.mensajeTecnico(e));
            MensajesUI.error("Exportar reporte", "No se pudo exportar el archivo.\n\nDetalle: " + MensajesUI.mensajeTecnico(e), e);
        }
    }

    @FXML
    private void volver() {
        Main.cambiarVista("/org/paginalib3/view/dashboard_admin.fxml", "Pagina-Libreria | Administración", 1180, 720);
    }

    private String moneda(double v) {
        return String.format("Q%,.2f", v);
    }

    private String num(double v) {
        return String.format(java.util.Locale.US, "%.2f", v);
    }
}
