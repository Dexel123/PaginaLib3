package org.paginalib3.controller;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.paginalib3.dao.ComprobanteVentaDAO;
import org.paginalib3.dao.impl.ComprobanteVentaDAOImpl;
import org.paginalib3.model.DetalleVenta;
import org.paginalib3.model.Venta;
import org.paginalib3.system.Main;

public class ComprobanteController {
    @FXML private VBox comprobante;
    @FXML private Label lblVenta, lblFecha, lblCliente, lblCajero, lblEstado, lblSubtotal, lblDescuento, lblTotal, lblMensaje;
    @FXML private TableView<DetalleVenta> tablaDetalles;
    @FXML private TableColumn<DetalleVenta, String> colIsbn, colPrecio, colImporte;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidad;

    private final ComprobanteVentaDAO dao = new ComprobanteVentaDAOImpl();

    @FXML
    private void initialize() {
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrecioUnitarioFormateado()));
        colImporte.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSubtotalFormateado()));
    }

    public void cargarVenta(int idVenta) {
        try {
            Venta venta = dao.buscarVenta(idVenta);
            if (venta == null) {
                mostrarMensaje("No se encontró la venta #" + idVenta + ".");
                return;
            }
            List<DetalleVenta> detalles = dao.listarDetalles(idVenta);
            tablaDetalles.setItems(FXCollections.observableArrayList(detalles));
            lblVenta.setText("Venta #" + venta.getIdVenta());
            lblFecha.setText(venta.getFechaVenta() == null ? "" : venta.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            lblCliente.setText(venta.getCuiCliente() == null || venta.getCuiCliente().isBlank() ? "Consumidor final" : "CUI " + venta.getCuiCliente());
            lblCajero.setText("Usuario #" + venta.getIdUsuario());
            lblEstado.setText(venta.getEstado());
            lblSubtotal.setText(venta.getSubtotalFormateado());
            lblDescuento.setText(String.format("Q%.2f", venta.getDescuento()));
            lblTotal.setText(venta.getTotalFormateado());
            lblMensaje.setText("Comprobante generado correctamente.");
        } catch (SQLException e) {
            mostrarMensaje("No fue posible cargar el comprobante: " + e.getMessage());
        }
    }

    private void mostrarMensaje(String mensaje) {
        tablaDetalles.getItems().clear();
        lblMensaje.setText(mensaje);
    }

    @FXML
    private void imprimir() {
        if (tablaDetalles.getItems().isEmpty()) {
            lblMensaje.setText("No hay detalles de venta para imprimir.");
            return;
        }
        PrinterJob trabajo = PrinterJob.createPrinterJob();
        if (trabajo == null) {
            lblMensaje.setText("No hay una impresora disponible.");
            return;
        }
        if (!trabajo.showPrintDialog(comprobante.getScene().getWindow())) return;
        PageLayout pagina = trabajo.getPrinter().createPageLayout(Paper.A4, PageOrientation.PORTRAIT, javafx.print.Printer.MarginType.DEFAULT);
        boolean impreso = trabajo.printPage(pagina, (Node) comprobante);
        if (impreso) {
            trabajo.endJob();
            lblMensaje.setText("Comprobante enviado a la impresora.");
        } else {
            trabajo.cancelJob();
            lblMensaje.setText("No fue posible imprimir el comprobante.");
        }
    }

    @FXML private void nuevaVenta() { Main.cambiarVista("/org/paginalib3/view/venta.fxml", "Pagina-Libreria | Registrar venta", 1200, 760); }
    @FXML private void volver() { Main.cambiarVista("/org/paginalib3/view/dashboard_cajero.fxml", "Pagina-Libreria | Dashboard Caja", 1100, 680); }
}
