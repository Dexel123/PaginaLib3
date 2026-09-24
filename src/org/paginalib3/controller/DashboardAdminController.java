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

    @FXML
    private Label lblVentasHoy, lblVentasTotales, lblTransaccionesHoy;
    @FXML
    private Label lblLibrosActivos, lblUnidadesStock, lblUsuariosActivos;
    @FXML
    private Label lblStockCritico, lblValorInventario, lblEstadoDashboard;
    private final AdminDAO adminDAO = new AdminDAOImpl();

    @Override
    protected String rolPermitido() {
        return "admin";
    }

    @Override
    protected String mensajeRol() {
        return "Visión global del negocio: ventas, inventario y usuarios.";
    }

    @Override
    @FXML
    protected void initialize() {
        super.initialize();
        if (Sesion.getUsuarioActual() != null && "admin".equalsIgnoreCase(Sesion.getUsuarioActual().getRol())) {
            actualizarIndicadores();
        }
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

    @FXML
    private void irAUsuarios() {
        Main.cambiarVista("/org/paginalib3/view/usuarios.fxml", "Pagina-Libreria | Usuarios", 1050, 690);
    }

    private String moneda(double v) {
        return String.format("Q%,.2f", v);
    }
}
