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

    @FXML
    private void seleccionarVenta() {
        Venta v = tblVentas.getSelectionModel().getSelectedItem();
        if (v != null) {
            txtIdVenta.setText(String.valueOf(v.getIdVenta()));
            lblEstado.setText("Venta #" + v.getIdVenta() + " seleccionada.");
        }
    }

    @FXML
    private void ejecutar() {
        Usuario u = Sesion.getUsuarioActual();
        if (u == null) {
            MensajesUI.advertencia("Sesión requerida", "No hay una sesión activa.");
            return;
        }

        try {
            String idTexto = txtIdVenta.getText() == null ? "" : txtIdVenta.getText().trim();
            if (idTexto.isBlank()) throw new IllegalArgumentException("Selecciona o escribe el número de venta.");
            int id = Integer.parseInt(idTexto);
            String motivo = txtMotivo.getText() == null ? "" : txtMotivo.getText().trim();
            if (motivo.isBlank()) throw new IllegalArgumentException("El motivo es obligatorio.");

            Venta venta = dao.buscarPorId(id);
            if (venta == null) throw new IllegalArgumentException("La venta no existe.");
            if (!"COMPLETADA".equalsIgnoreCase(venta.getEstado())) {
                throw new IllegalArgumentException("La venta ya no está disponible para esta operación.");
            }

            String operacion = cmbOperacion.getValue();
            String pregunta = "DEVOLVER".equals(operacion)
                    ? "¿Confirmas la devolución de la venta #" + id + "? El stock será repuesto."
                    : "¿Confirmas la anulación de la venta #" + id + "? El stock será repuesto.";
            if (!MensajesUI.confirmar("Confirmar operación", pregunta)) return;

            boolean ok = "DEVOLVER".equals(operacion)
                    ? dao.devolverVenta(id, u.getId(), motivo)
                    : dao.anularVenta(id, u.getId(), motivo);

            if (ok) {
                MensajesUI.informacion("Operación completada", "La operación sobre la venta #" + id + " se realizó correctamente.");
                txtIdVenta.clear();
                txtMotivo.clear();
                cargarRecientes();
            } else {
                MensajesUI.advertencia("Sin cambios", "La operación no pudo aplicarse. Actualiza la lista e inténtalo nuevamente.");
            }
        } catch (NumberFormatException e) {
            MensajesUI.advertencia("Dato inválido", "El número de venta debe ser un entero válido.");
        } catch (IllegalArgumentException e) {
            MensajesUI.advertencia("Revisa los datos", e.getMessage());
        } catch (Exception e) {
            lblEstado.setText("No se pudo completar la operación.");
            MensajesUI.error("Anulaciones y devoluciones",
                    "No se pudo completar la operación.\nDetalle: " + MensajesUI.mensajeTecnico(e), e);
        }
    }

    @FXML
    private void volver() {
        Permisos.volverDashboardSegunRol();
    }
}