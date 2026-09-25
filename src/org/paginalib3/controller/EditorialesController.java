package org.paginalib3.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalib3.dao.EditorialDAO;
import org.paginalib3.dao.impl.EditorialDAOImpl;
import org.paginalib3.model.Editorial;
import org.paginalib3.system.Main;
import org.paginalib3.util.Conexion;
import org.paginalib3.util.MensajesUI;
import org.paginalib3.util.Permisos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EditorialesController {

    @FXML
    private TextField txtNit;

    @FXML
    private TextField txtNombre;

    @FXML
    private TableView<Editorial> tabla;

    @FXML
    private TableColumn<Editorial, String> colNit;

    @FXML
    private TableColumn<Editorial, String> colNombre;

    @FXML
    private Label lblEstado;

    @FXML
    public void initialize() {

        if (!Permisos.requerirAdmin("Gestión de editoriales")) {
            return;
        }

        colNit.setCellValueFactory(
                new PropertyValueFactory<>("nit")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        cargar();
    }

    private void cargar() {

        try {

            var lista = FXCollections.<Editorial>observableArrayList();

            String sql = """
                SELECT nit, nombre_editorial
                FROM editoriales
                WHERE activo = TRUE
                ORDER BY nombre_editorial
                """;

            try (
                    Connection c = Conexion.getInstancia().conectar(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {

                while (r.next()) {

                    lista.add(
                            new Editorial(
                                    r.getString("nit"),
                                    r.getString("nombre_editorial")
                            )
                    );

                }

            }

            tabla.setItems(lista);

            lblEstado.setText(
                    lista.size() + " editorial(es) activa(s)."
            );

        } catch (Exception e) {

            lblEstado.setText(
                    "Error cargando editoriales."
            );

            MensajesUI.registrarError(e);
        }

    }

    @FXML
    private void guardar() {

        String nit = txtNit.getText() == null
                ? ""
                : txtNit.getText().trim();

        String nombre = txtNombre.getText() == null
                ? ""
                : txtNombre.getText().trim();

        if (nit.isBlank()) {

            advertencia(
                    "El NIT es obligatorio."
            );

            return;
        }

        if (nombre.isBlank()) {

            advertencia(
                    "El nombre es obligatorio."
            );

            return;
        }

        if (nit.length() > 20) {

            advertencia(
                    "El NIT no puede superar 20 caracteres."
            );

            return;
        }

        try (
                Connection c = Conexion.getInstancia().conectar(); CallableStatement cs
                = c.prepareCall(
                        "{CALL sp_insertareditorial(?,?,?,?)}"
                )) {

            cs.setString(1, nit);

            cs.setString(2, nombre);

            cs.setString(3, "");

            cs.setString(4, "");

            cs.execute();

            limpiar();

            cargar();

            lblEstado.setText(
                    "Editorial creada correctamente."
            );

        } catch (Exception e) {

            lblEstado.setText(
                    "No se pudo crear editorial."
            );

            MensajesUI.error(
                    "Editoriales",
                    "Error al crear editorial.\n\n"
                    + MensajesUI.mensajeTecnico(e),
                    e
            );

        }

    }

    private void limpiar() {

        txtNit.clear();

        txtNombre.clear();

    }

    @FXML
    private void volver() {

        Main.cambiarVista(
                "/org/paginalib3/view/dashboard_admin.fxml",
                "Administración",
                1180,
                720
        );

    }

    private void advertencia(String mensaje) {

        new Alert(
                Alert.AlertType.WARNING,
                mensaje
        ).showAndWait();

    }

}
