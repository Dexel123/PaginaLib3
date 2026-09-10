package org.paginalib3.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.paginalib3.dao.VentaDAO;
import org.paginalib3.dao.impl.VentaDAOImpl;
import org.paginalib3.model.Usuario;
import org.paginalib3.model.Venta;
import org.paginalib3.system.Main;
import org.paginalib3.util.Sesion;

import java.sql.SQLException;
import java.util.List;

public class DashboardCajeroController extends DashboardBaseController {

    @FXML
    private Label lblCantidadVentas, lblTotalVendido, lblEstadoVentas;
    @FXML
    private TableView<Venta> tblVentas;
    @FXML
    private TableColumn<Venta, Integer> colVenta;
    @FXML
    private TableColumn<Venta, String> colEstado;
    @FXML
    private TableColumn<Venta, Double> colSubtotal, colTotal;

    private final VentaDAO ventaDAO = new VentaDAOImpl();

    @Override
    protected String rolPermitido() {
        return "cajero";
    }

    @Override
    protected String mensajeRol() {
        return "Caja: búsqueda, registro de ventas y comprobantes.";
    }

    @Override
    @FXML
    protected void initialize() {
        super.initialize();
        if (Sesion.getUsuarioActual() != null) {
            configurarTabla();
            cargarVentas();
        }
    }

    private void configurarTabla() {
        colVenta.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("idVenta"));
        colEstado.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("estado"));
        colSubtotal.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("subtotal"));
        colTotal.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("total"));
    }

    private void cargarVentas() {
        Usuario u = Sesion.getUsuarioActual();
        try {
            List<Venta> v = ventaDAO.listarVentasDelDiaPorUsuario(u.getId());
            tblVentas.setItems(FXCollections.observableArrayList(v));
            
            double total = v.stream()
                    .filter(x -> "COMPLETADA".equalsIgnoreCase(x.getEstado()))
                    .mapToDouble(Venta::getTotal)
                    .sum();
                    
            long cantidad = v.stream()
                    .filter(x -> "COMPLETADA".equalsIgnoreCase(x.getEstado()))
                    .count();
                    
            lblCantidadVentas.setText(String.valueOf(cantidad));
            lblTotalVendido.setText(String.format("Q%.2f", total));
            lblEstadoVentas.setText(v.size() + " venta(s) registrada(s) hoy.");
        } catch (SQLException e) {
            lblEstadoVentas.setText("Error al consultar ventas del día: " + e.getMessage());
        }
    }

    @FXML
    private void irABuscarLibros() {
        Main.cambiarVista("/org/paginalib3/view/buscar_libros.fxml", "Pagina-Libreria | Buscar libros", 1050, 650);
    }

    @FXML
    private void irACaja() {
        Main.cambiarVista("/org/paginalib3/view/venta.fxml", "Pagina-Libreria | Registrar venta", 1200, 760);
    }

    @FXML
    private void irADevolucion() {
        Main.cambiarVista("/org/paginalib3/view/devolucion.fxml", "Pagina-Libreria | Anulación y devolución", 1000, 700);
    }

    @FXML
    private void actualizarVentas() {
        cargarVentas();
    }
}