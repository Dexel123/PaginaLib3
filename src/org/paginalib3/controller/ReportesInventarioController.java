package org.paginalib3.controller;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.paginalib3.dao.ReporteDAO;
import org.paginalib3.dao.impl.ReporteDAOImpl;
import org.paginalib3.model.ReporteLibroVendido;
import org.paginalib3.model.StockValorizado;
import org.paginalib3.system.Main;
import org.paginalib3.util.ExportadorCSV;
import org.paginalib3.util.Permisos;
import org.paginalib3.util.MensajesUI;

public class ReportesInventarioController {
    @FXML private DatePicker dpDesde, dpHasta;
    @FXML private TableView<ReporteLibroVendido> tablaMasVendidos;
    @FXML private TableColumn<ReporteLibroVendido, String> colIsbnV, colTituloV;
    @FXML private TableColumn<ReporteLibroVendido, Integer> colUnidadesV;
    @FXML private TableColumn<ReporteLibroVendido, Double> colIngresosV;
    @FXML private TableView<StockValorizado> tablaStock;
    @FXML private TableColumn<StockValorizado, String> colIsbnS, colTituloS;
    @FXML private TableColumn<StockValorizado, Integer> colStockS;
    @FXML private TableColumn<StockValorizado, Double> colCostoS, colPrecioS, colValorCostoS, colValorVentaS;
    @FXML private Label lblValorCosto, lblValorVenta, lblEstado;

    private final ReporteDAO dao = new ReporteDAOImpl();
    private List<ReporteLibroVendido> vendidos = new ArrayList<>();
    private List<StockValorizado> stock = new ArrayList<>();

    @FXML private void initialize() {
        if (!Permisos.requerirAdmin("Reportes de inventario")) return;
        tablaMasVendidos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaStock.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colIsbnV.setCellValueFactory(new PropertyValueFactory<>("isbn")); colTituloV.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colUnidadesV.setCellValueFactory(new PropertyValueFactory<>("unidadesNetas")); colIngresosV.setCellValueFactory(new PropertyValueFactory<>("ingresosBrutos"));
        colIsbnS.setCellValueFactory(new PropertyValueFactory<>("isbn")); colTituloS.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colStockS.setCellValueFactory(new PropertyValueFactory<>("stockActual")); colCostoS.setCellValueFactory(new PropertyValueFactory<>("costoPromedio"));
        colPrecioS.setCellValueFactory(new PropertyValueFactory<>("precio")); colValorCostoS.setCellValueFactory(new PropertyValueFactory<>("valorCosto"));
        colValorVentaS.setCellValueFactory(new PropertyValueFactory<>("valorVenta"));
        LocalDate hoy = LocalDate.now(); dpDesde.setValue(hoy.minusDays(30)); dpHasta.setValue(hoy); consultar();
    }

    @FXML private void consultar() {
        try {
            vendidos = dao.librosMasVendidos(dpDesde.getValue(), dpHasta.getValue(), 20);
            stock = dao.stockValorizado();
            tablaMasVendidos.setItems(FXCollections.observableArrayList(vendidos)); tablaStock.setItems(FXCollections.observableArrayList(stock));
            lblValorCosto.setText(moneda(stock.stream().mapToDouble(StockValorizado::getValorCosto).sum()));
            lblValorVenta.setText(moneda(stock.stream().mapToDouble(StockValorizado::getValorVenta).sum()));
            lblEstado.setText("Reporte de inventario actualizado.");
        } catch (SQLException | IllegalArgumentException e) {
            lblEstado.setText("No se pudo generar el reporte: " + MensajesUI.mensajeTecnico(e));
            MensajesUI.error("Reportes de inventario", "No se pudo generar el reporte.\n\nDetalle: " + MensajesUI.mensajeTecnico(e), e);
        }
    }

    @FXML private void exportarExcel() {
        if (stock.isEmpty() && vendidos.isEmpty()) { lblEstado.setText("Primero genera el reporte."); return; }
        FileChooser fc = new FileChooser(); fc.setTitle("Exportar reporte de inventario"); fc.setInitialFileName("reporte_inventario.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo CSV compatible con Excel", "*.csv"));
        File f = fc.showSaveDialog(Main.getStagePrincipal()); if (f == null) return;
        try {
            List<String[]> filas = new ArrayList<>();
            filas.add(new String[]{"LIBROS MÁS VENDIDOS"}); filas.add(new String[]{"ISBN","Título","Unidades netas","Ingresos brutos"});
            for (ReporteLibroVendido r: vendidos) filas.add(new String[]{r.getIsbn(),r.getTitulo(),String.valueOf(r.getUnidadesNetas()),num(r.getIngresosBrutos())});
            filas.add(new String[]{}); filas.add(new String[]{"STOCK VALORIZADO"});
            filas.add(new String[]{"ISBN","Título","Stock","Costo promedio","Precio","Valor costo","Valor venta"});
            for (StockValorizado r: stock) filas.add(new String[]{r.getIsbn(),r.getTitulo(),String.valueOf(r.getStockActual()),num(r.getCostoPromedio()),num(r.getPrecio()),num(r.getValorCosto()),num(r.getValorVenta())});
            ExportadorCSV.guardar(f, filas); lblEstado.setText("Reporte exportado: " + f.getName());
        } catch (Exception e) {
            lblEstado.setText("No se pudo exportar: " + MensajesUI.mensajeTecnico(e));
            MensajesUI.error("Exportar reporte", "No se pudo exportar el archivo.\n\nDetalle: " + MensajesUI.mensajeTecnico(e), e);
        }
    }

    @FXML private void volver() { Main.cambiarVista("/org/paginalib3/view/dashboard_admin.fxml", "Pagina-Libreria | Administración", 1180, 720); }
    private String moneda(double v){return String.format("Q%,.2f",v);} private String num(double v){return String.format(java.util.Locale.US,"%.2f",v);}
}
