package org.paginalib3.controller;

import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.paginalib3.dao.AdminDAO;
import org.paginalib3.dao.impl.AdminDAOImpl;
import org.paginalib3.model.DashboardIndicadores;
import org.paginalib3.system.Main;
import org.paginalib3.util.Sesion;
import org.paginalib3.util.MensajesUI;

public class DashboardAdminController extends DashboardBaseController {

    @FXML private Label lblVentasHoy, lblVentasTotales, lblTransaccionesHoy;
    @FXML private Label lblLibrosActivos, lblUnidadesStock, lblUsuariosActivos;
    @FXML private Label lblStockCritico, lblValorInventario, lblEstadoDashboard;

    private final AdminDAO adminDAO = new AdminDAOImpl();

    @Override protected String rolPermitido() { return "admin"; }
    @Override protected String mensajeRol() { return "Visión global del negocio: ventas, inventario, personal y reportes."; }

    @Override
    @FXML
    protected void initialize() {
        super.initialize();
        if (Sesion.getUsuarioActual() == null || !"admin".equalsIgnoreCase(Sesion.getUsuarioActual().getRol())) return;
        actualizarIndicadores();
    }

    @FXML
    private void actualizarIndicadores() {
        try {
            DashboardIndicadores d = adminDAO.obtenerIndicadores();
            lblVentasHoy.setText(moneda(d.getVentasHoy()));
            lblVentasTotales.setText(moneda(d.getVentasTotales()));
            lblTransaccionesHoy.setText(String.valueOf(d.getTransaccionesHoy()));
            lblLibrosActivos.setText(String.valueOf(d.getLibrosActivos()));
            lblUnidadesStock.setText(String.valueOf(d.getUnidadesEnStock()));
            lblUsuariosActivos.setText(String.valueOf(d.getUsuariosActivos()));
            lblStockCritico.setText(String.valueOf(d.getLibrosStockCritico()));
            lblValorInventario.setText(moneda(d.getInventarioValorizadoCosto()));
            lblEstadoDashboard.setText("Indicadores actualizados.");
        } catch (SQLException e) {
            MensajesUI.registrarError(e);
            lblEstadoDashboard.setText("No se pudieron cargar los indicadores: " + MensajesUI.mensajeTecnico(e));
        }
    }

    @FXML private void irAUsuarios() { abrir("/org/paginalib3/view/usuarios.fxml", "Usuarios", 1050, 690); }
    @FXML private void irAVentas() { abrir("/org/paginalib3/view/venta.fxml", "Caja / Ventas", 1180, 720); }
    @FXML private void irADevoluciones() { abrir("/org/paginalib3/view/devolucion.fxml", "Anulaciones y devoluciones", 1050, 700); }
    @FXML private void irALibros() { abrir("/org/paginalib3/view/libros.fxml", "Catálogo de libros", 1180, 720); }
    @FXML private void irAIngreso() { abrir("/org/paginalib3/view/ingreso_inventario.fxml", "Ingreso de inventario", 1120, 700); }
    @FXML private void irASalida() { abrir("/org/paginalib3/view/salida_inventario.fxml", "Salida de inventario", 1120, 700); }
    @FXML private void irAStockCritico() { abrir("/org/paginalib3/view/stock_critico.fxml", "Stock crítico", 1050, 680); }
    @FXML private void irAReportesVentas() { abrir("/org/paginalib3/view/reportes_ventas.fxml", "Reportes de ventas", 1120, 700); }
    @FXML private void irAReportesInventario() { abrir("/org/paginalib3/view/reportes_inventario.fxml", "Reportes de inventario", 1160, 720); }
    @FXML private void irAGestionAdministrativa() { abrir("/org/paginalib3/view/gestion_administrativa.fxml", "Gestión administrativa", 1180, 720); }

    private void abrir(String ruta, String titulo, double ancho, double alto) {
        Main.cambiarVista(ruta, "Pagina-Libreria | " + titulo, ancho, alto);
    }
    private String moneda(double v) { return String.format("Q%,.2f", v); }
}
