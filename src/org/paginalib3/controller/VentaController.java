package org.paginalib3.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalib3.dao.ClienteDAO;
import org.paginalib3.dao.LibroDAO;
import org.paginalib3.dao.UsuarioDAO;
import org.paginalib3.dao.VentaDAO;
import org.paginalib3.dao.impl.ClienteDAOImpl;
import org.paginalib3.dao.impl.LibroDAOImpl;
import org.paginalib3.dao.impl.UsuarioDAOImpl;
import org.paginalib3.dao.impl.VentaDAOImpl;
import org.paginalib3.model.Cliente;
import org.paginalib3.model.DetalleVenta;
import org.paginalib3.model.Libro;
import org.paginalib3.model.Usuario;
import org.paginalib3.model.Venta;
import org.paginalib3.system.Main;
import org.paginalib3.util.Seguridad;
import org.paginalib3.util.Sesion;

public class VentaController {

    @FXML
    private ComboBox<Cliente> cmbCliente;

    @FXML
    private ComboBox<String> cmbTipoDescuento;

    @FXML
    private TextField txtCuiCliente;
    @FXML
    private TextField txtFiltroLibro;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtDescuento;
    @FXML
    private TextField txtUsuarioAutoriza;

    @FXML
    private PasswordField txtClaveAutoriza;

    @FXML
    private TableView<Libro> tblLibros;

    @FXML
    private TableColumn<Libro, String> colLibroIsbn;
    @FXML
    private TableColumn<Libro, String> colLibroTitulo;
    @FXML
    private TableColumn<Libro, Double> colLibroPrecio;
    @FXML
    private TableColumn<Libro, Integer> colLibroStock;

    @FXML
    private TableView<DetalleVenta> tblCarrito;

    @FXML
    private TableColumn<DetalleVenta, String> colCarritoIsbn;
    @FXML
    private TableColumn<DetalleVenta, Integer> colCarritoCantidad;
    @FXML
    private TableColumn<DetalleVenta, Double> colCarritoPrecio;
    @FXML
    private TableColumn<DetalleVenta, Double> colCarritoSubtotal;

    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblDescuento;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblEstado;

    @FXML
    private Button btnLimpiar;

    private final List<DetalleVenta> carrito = new ArrayList<>();

    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    private List<Libro> libros = new ArrayList<>();

    private boolean ventaEnCurso;

    private Cliente clienteVenta;
    private String cuiVenta = "";

    @FXML
    private void initialize() {
        colLibroIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colLibroTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colLibroPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colLibroStock.setCellValueFactory(new PropertyValueFactory<>("stockActual"));

        colCarritoIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colCarritoCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCarritoPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colCarritoSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        txtCantidad.setText("1");

        cmbTipoDescuento.setItems(
                FXCollections.observableArrayList(
                        "MONTO",
                        "PORCENTAJE"
                )
        );

        cmbTipoDescuento.setValue("MONTO");

        cargarClientes();
        cargarLibros();
        refrescarTotales();
        configurarProteccionCierre();
    }

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println(">>> ENTRO A VentaController.initialize() <<<");
        cargarClientes();
    }

    private void cargarClientes() {
        System.out.println(">>> ENTRO A cargarClientes() <<<");

        try {
            List<Cliente> clientes = clienteDAO.listar();
            System.out.println("CLIENTES ENCONTRADOS: " + clientes.size());

            cmbCliente.setItems(
                    FXCollections.observableArrayList(clientes)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarLibros() {
        try {
            libros = libroDAO.listar();
            tblLibros.setItems(
                    FXCollections.observableArrayList(libros)
            );
        } catch (Exception e) {
            lblEstado.setText("No se pudieron cargar libros: " + e.getMessage());
        }
    }

    @FXML
    private void buscarLibro() {
        try {
            String textoBusqueda = txtFiltroLibro.getText();
            tblLibros.setItems(
                    FXCollections.observableArrayList(
                            libroDAO.buscar(textoBusqueda)
                    )
            );
        } catch (Exception e) {
            lblEstado.setText("Error al buscar: " + e.getMessage());
        }
    }

    @FXML
    private void seleccionarCliente() {
        if (ventaEnCurso) {
            cmbCliente.setValue(clienteVenta);
            txtCuiCliente.setText(cuiVenta);
            return;
        }

        Cliente cliente = cmbCliente.getValue();

        if (cliente != null) {
            txtCuiCliente.setText(cliente.getCuiCliente());
        }
    }

    @FXML
    private void agregar() {
        Libro libroSeleccionado = tblLibros.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            alert(Alert.AlertType.WARNING, "Selecciona un libro.");
            return;
        }

        String cui = textoCuiActual();

        if (!cui.isEmpty()) {
            try {
                Long.parseLong(cui);
            } catch (NumberFormatException e) {
                alert(Alert.AlertType.WARNING, "El CUI debe ser numérico.");
                return;
            }
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            if (cantidad <= 0) {
                throw new NumberFormatException();
            }

            DetalleVenta detalleExistente = carrito.stream()
                    .filter(detalle -> detalle.getIsbn().equals(libroSeleccionado.getIsbn()))
                    .findFirst()
                    .orElse(null);

            int cantidadTotal = cantidad + (detalleExistente == null ? 0 : detalleExistente.getCantidad());

            if (cantidadTotal > libroSeleccionado.getStockActual()) {
                alert(
                        Alert.AlertType.WARNING,
                        "Stock insuficiente. Disponible: " + libroSeleccionado.getStockActual()
                );
                return;
            }

            if (detalleExistente == null) {
                carrito.add(
                        new DetalleVenta(
                                libroSeleccionado.getIsbn(),
                                cantidad,
                                libroSeleccionado.getPrecio(),
                                cantidad * libroSeleccionado.getPrecio()
                        )
                );
            } else {
                detalleExistente.setCantidad(cantidadTotal);
                detalleExistente.setSubtotal(
                        cantidadTotal * detalleExistente.getPrecioUnitario()
                );
            }

            iniciarVenta();

            refrescarCarrito();
            txtCantidad.setText("1");

        } catch (NumberFormatException e) {
            alert(Alert.AlertType.WARNING, "La cantidad debe ser un entero mayor a 0.");
        }
    }

    @FXML
    private void eliminar() {
        DetalleVenta detalle = tblCarrito.getSelectionModel().getSelectedItem();

        if (detalle == null) {
            alert(Alert.AlertType.WARNING, "Selecciona un producto del carrito.");
            return;
        }

        carrito.remove(detalle);
        refrescarCarrito();
    }

    private double calcularSubtotal() {
        return carrito.stream()
                .mapToDouble(DetalleVenta::getSubtotal)
                .sum();
    }

    private double calcularDescuento(double subtotal) {
        if (txtDescuento.getText() == null || txtDescuento.getText().isBlank()) {
            return 0;
        }

        double valor = Double.parseDouble(txtDescuento.getText().trim());

        if (valor < 0) {
            throw new IllegalArgumentException("El descuento no puede ser negativo.");
        }

        String tipo = cmbTipoDescuento.getValue();

        double descuento = "PORCENTAJE".equals(tipo)
                ? subtotal * valor / 100.0
                : valor;

        if ("PORCENTAJE".equals(tipo) && valor > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100.");
        }

        if (descuento > subtotal) {
            throw new IllegalArgumentException("El descuento no puede superar el subtotal.");
        }

        return Math.round(descuento * 100.0) / 100.0;
    }

    @FXML
    private void actualizarDescuento() {
        try {
            refrescarTotales();
        } catch (Exception e) {
            lblEstado.setText(e.getMessage());
        }
    }

    private void refrescarCarrito() {
        tblCarrito.setItems(
                FXCollections.observableArrayList(carrito)
        );
        refrescarTotales();
    }

    private void refrescarTotales() {
        double subtotal = calcularSubtotal();
        double descuento = 0;

        try {
            descuento = calcularDescuento(subtotal);
        } catch (Exception ignored) {
        }

        lblSubtotal.setText(String.format("Q%.2f", subtotal));
        lblDescuento.setText(String.format("Q%.2f", descuento));
        lblTotal.setText(String.format("Q%.2f", subtotal - descuento));
    }

    @FXML
    private void registrar() {
        if (carrito.isEmpty()) {
            alert(Alert.AlertType.WARNING, "El carrito está vacío.");
            return;
        }

        Usuario usuario = Sesion.getUsuarioActual();

        if (usuario == null || !"cajero".equalsIgnoreCase(usuario.getRol())) {
            alert(Alert.AlertType.ERROR, "Se requiere una sesión activa de cajero.");
            return;
        }

        String cui = ventaEnCurso ? cuiVenta : textoCuiActual();

        if (!cui.isEmpty()) {
            try {
                Long.parseLong(cui);
            } catch (NumberFormatException e) {
                alert(Alert.AlertType.WARNING, "El CUI debe ser numérico.");
                return;
            }
        }

        try {
            double subtotal = calcularSubtotal();
            double descuento = calcularDescuento(subtotal);

            Integer autorizador = null;

            if (descuento > 0) {
                autorizador = validarAutorizacionAdmin();
            }

            Venta venta = new Venta(
                    subtotal,
                    descuento,
                    subtotal - descuento,
                    "COMPLETADA",
                    cui,
                    usuario.getId()
            );

            boolean registrada = ventaDAO.registrarVenta(
                    venta,
                    new ArrayList<>(carrito),
                    autorizador
            );

            if (!registrada) {
                return;
            }

            int idVenta = venta.getIdVenta();

            alert(
                    Alert.AlertType.INFORMATION,
                    "Venta #" + idVenta + " registrada correctamente."
            );

            carrito.clear();

            finalizarVenta();

            refrescarCarrito();
            cargarLibros();
            quitarProteccionCierre();

            Main.cambiarVista(
                    "/org/paginalib3/view/comprobante.fxml",
                    "Pagina-Libreria | Comprobante",
                    900,
                    760
            );

            Main.configurarVistaActual(controlador -> {
                if (controlador instanceof ComprobanteController comprobante) {
                    comprobante.cargarVenta(idVenta);
                }
            });

        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "No se pudo registrar la venta: " + e.getMessage());
        }
    }

    private int validarAutorizacionAdmin() throws Exception {
        String username = txtUsuarioAutoriza.getText() == null
                ? ""
                : txtUsuarioAutoriza.getText().trim();

        String password = txtClaveAutoriza.getText() == null
                ? ""
                : txtClaveAutoriza.getText();

        if (username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Un descuento requiere usuario y contraseña de administrador.");
        }

        Usuario administrador = usuarioDAO.iniciarSesion(
                username,
                Seguridad.sha256(password)
        );

        if (administrador == null || !"admin".equalsIgnoreCase(administrador.getRol())) {
            throw new IllegalArgumentException("La autorización no corresponde a un administrador activo.");
        }

        return administrador.getId();
    }

    @FXML
    private void limpiar() {
        if (ventaEnCurso && !confirmarCancelacion()) {
            return;
        }

        boolean fueCancelada = ventaEnCurso;

        carrito.clear();
        finalizarVenta();

        txtCuiCliente.clear();
        cmbCliente.getSelectionModel().clearSelection();
        txtCantidad.setText("1");
        txtDescuento.clear();
        txtUsuarioAutoriza.clear();
        txtClaveAutoriza.clear();
        cmbTipoDescuento.setValue("MONTO");

        refrescarCarrito();

        lblEstado.setText(
                fueCancelada
                ? "Venta cancelada. Ya puedes seleccionar otro cliente."
                : "Formulario limpio."
        );
    }

    @FXML
    private void volver() {
        if (ventaEnCurso) {
            alert(
                    Alert.AlertType.WARNING,
                    "Hay una venta en curso. Regístrala o usa 'Cancelar venta' antes de volver."
            );
            return;
        }

        quitarProteccionCierre();

        Main.cambiarVista(
                "/org/paginalib3/view/dashboard_cajero.fxml",
                "Pagina-Libreria | Dashboard Caja",
                1100,
                680
        );
    }

    private void iniciarVenta() {
        if (ventaEnCurso) {
            return;
        }

        ventaEnCurso = true;

        clienteVenta = cmbCliente.getValue();
        cuiVenta = textoCuiActual();

        cmbCliente.setDisable(true);
        txtCuiCliente.setDisable(true);

        btnLimpiar.setText("Cancelar venta");

        lblEstado.setText(
                "Venta en curso: el cliente permanecerá fijo hasta registrar o cancelar la venta."
        );
    }

    private void finalizarVenta() {
        ventaEnCurso = false;
        clienteVenta = null;
        cuiVenta = "";

        cmbCliente.setDisable(false);
        txtCuiCliente.setDisable(false);

        btnLimpiar.setText("Limpiar");
    }

    private String textoCuiActual() {
        return txtCuiCliente.getText() == null
                ? ""
                : txtCuiCliente.getText().trim();
    }

    private boolean confirmarCancelacion() {
        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Se eliminarán todos los libros agregados y se liberará el cliente seleccionado. ¿Deseas cancelar la venta?",
                ButtonType.YES,
                ButtonType.NO
        );

        confirmacion.setTitle("Cancelar venta");
        confirmacion.setHeaderText("Hay una venta en curso");

        return confirmacion.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void configurarProteccionCierre() {
        if (Main.getStagePrincipal() == null) {
            return;
        }

        Main.getStagePrincipal().setOnCloseRequest(event -> {
            if (ventaEnCurso) {
                event.consume();
                alert(
                        Alert.AlertType.WARNING,
                        "Hay una venta en curso. Regístrala o cancélala antes de cerrar."
                );
            }
        });
    }

    private void quitarProteccionCierre() {
        if (Main.getStagePrincipal() != null) {
            Main.getStagePrincipal().setOnCloseRequest(null);
        }
    }

    private void alert(Alert.AlertType tipo, String mensaje) {
        new Alert(
                tipo,
                mensaje,
                ButtonType.OK
        ).showAndWait();
    }
}