package org.paginalib3.controller;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
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
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private ComboBox<String> cmbTipoDescuento;
    @FXML private TextField txtCuiCliente, txtFiltroLibro, txtCantidad, txtDescuento, txtUsuarioAutoriza;
    @FXML private PasswordField txtClaveAutoriza;
    @FXML private TableView<Libro> tblLibros;
    @FXML private TableColumn<Libro, String> colLibroIsbn, colLibroTitulo;
    @FXML private TableColumn<Libro, Double> colLibroPrecio;
    @FXML private TableColumn<Libro, Integer> colLibroStock;
    @FXML private TableView<DetalleVenta> tblCarrito;
    @FXML private TableColumn<DetalleVenta, String> colCarritoIsbn;
    @FXML private TableColumn<DetalleVenta, Integer> colCarritoCantidad;
    @FXML private TableColumn<DetalleVenta, Double> colCarritoPrecio, colCarritoSubtotal;
    @FXML private Label lblSubtotal, lblDescuento, lblTotal, lblEstado;
    @FXML private Button btnLimpiar, btnAgregar;

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
        cmbTipoDescuento.setItems(FXCollections.observableArrayList("MONTO", "PORCENTAJE"));
        cmbTipoDescuento.setValue("MONTO");
        cargarClientes();
        cargarLibros();
        refrescarTotales();

        // Bloquea el botón cuando no hay selección o el libro ya no tiene stock disponible.
        tblLibros.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            actualizarEstadoBotonAgregar(actual);
        });
        actualizarEstadoBotonAgregar(null);

        configurarProteccionCierre();
    }
    
    private void cargarClientes() {
    try {
        List<Cliente> clientes = clienteDAO.listar();

        cmbCliente.setItems(
            FXCollections.observableArrayList(clientes)
        );

    } catch (Exception e) {
        e.printStackTrace();
    }
}
  

    private void cargarLibros() {
        try {
            libros = aplicarStockReservado(libroDAO.listar());
            tblLibros.setItems(FXCollections.observableArrayList(libros));
            actualizarEstadoBotonAgregar(tblLibros.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            lblEstado.setText("No se pudieron cargar libros: " + e.getMessage());
        }
    }

    @FXML private void buscarLibro() {
        try {
            String t = txtFiltroLibro.getText();
            List<Libro> resultado = aplicarStockReservado(libroDAO.buscar(t));
            tblLibros.setItems(FXCollections.observableArrayList(resultado));
            actualizarEstadoBotonAgregar(tblLibros.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            lblEstado.setText("Error al buscar: " + e.getMessage());
        }
    }

    @FXML private void seleccionarCliente() {
        if (ventaEnCurso) {
            cmbCliente.setValue(clienteVenta);
            txtCuiCliente.setText(cuiVenta);
            return;
        }
        Cliente c = cmbCliente.getValue();
        if (c != null) txtCuiCliente.setText(c.getCuiCliente());
    }

    @FXML private void agregar() {
        Libro l = tblLibros.getSelectionModel().getSelectedItem();
        if (l == null) {
            alert(Alert.AlertType.WARNING, "Selecciona un libro.");
            return;
        }

        if (l.getStockActual() <= 0) {
            alert(Alert.AlertType.WARNING, "Este libro ya no tiene stock disponible para esta venta.");
            actualizarEstadoBotonAgregar(l);
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
            int c = Integer.parseInt(txtCantidad.getText().trim());
            if (c <= 0) throw new NumberFormatException();

            // stockActual aquí representa lo que todavía queda disponible en esta venta.
            if (c > l.getStockActual()) {
                alert(Alert.AlertType.WARNING,
                        "Stock insuficiente. Disponible para esta venta: " + l.getStockActual());
                return;
            }

            DetalleVenta d = carrito.stream()
                    .filter(x -> x.getIsbn().equals(l.getIsbn()))
                    .findFirst()
                    .orElse(null);

            int total = c + (d == null ? 0 : d.getCantidad());

            if (d == null) {
                carrito.add(new DetalleVenta(l.getIsbn(), c, l.getPrecio(), c * l.getPrecio()));
            } else {
                d.setCantidad(total);
                d.setSubtotal(total * d.getPrecioUnitario());
            }

            // Reserva visualmente esas unidades sin tocar todavía MySQL.
            l.setStockActual(l.getStockActual() - c);
            tblLibros.refresh();
            actualizarEstadoBotonAgregar(l);

            iniciarVenta();
            refrescarCarrito();
            txtCantidad.setText("1");
        } catch (NumberFormatException e) {
            alert(Alert.AlertType.WARNING, "La cantidad debe ser un entero mayor a 0.");
        }
    }

    @FXML private void eliminar() {
        DetalleVenta d = tblCarrito.getSelectionModel().getSelectedItem();
        if (d == null) {
            alert(Alert.AlertType.WARNING, "Selecciona un producto del carrito.");
            return;
        }

        carrito.remove(d);
        refrescarCarrito();

        // Recarga el stock de BD y descuenta únicamente lo que todavía siga en el carrito.
        refrescarCatalogoVisible();
    }

    /**
     * Convierte el stock almacenado en BD en stock disponible para la venta actual.
     * No modifica MySQL: solamente descuenta temporalmente lo que ya está en el carrito.
     */
    private List<Libro> aplicarStockReservado(List<Libro> lista) {
        for (Libro libro : lista) {
            int reservado = cantidadEnCarrito(libro.getIsbn());
            libro.setStockActual(Math.max(0, libro.getStockActual() - reservado));
        }
        return lista;
    }

    private int cantidadEnCarrito(String isbn) {
        return carrito.stream()
                .filter(d -> d.getIsbn().equals(isbn))
                .mapToInt(DetalleVenta::getCantidad)
                .sum();
    }

    private void refrescarCatalogoVisible() {
        String filtro = txtFiltroLibro.getText() == null ? "" : txtFiltroLibro.getText().trim();
        if (filtro.isEmpty()) {
            cargarLibros();
        } else {
            buscarLibro();
        }
    }

    private void actualizarEstadoBotonAgregar(Libro libro) {
        if (btnAgregar == null) return;

        boolean sinStock = libro == null || libro.getStockActual() <= 0;
        btnAgregar.setDisable(sinStock);

        if (libro != null && libro.getStockActual() <= 0) {
            btnAgregar.setText("Sin stock");
        } else {
            btnAgregar.setText("Agregar seleccionado");
        }
    }

    private double calcularSubtotal() { return carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum(); }

    private double calcularDescuento(double subtotal) {
        if (txtDescuento.getText() == null || txtDescuento.getText().isBlank()) return 0;
        double valor = Double.parseDouble(txtDescuento.getText().trim());
        if (valor < 0) throw new IllegalArgumentException("El descuento no puede ser negativo.");
        String tipo = cmbTipoDescuento.getValue();
        double descuento = "PORCENTAJE".equals(tipo) ? subtotal * valor / 100.0 : valor;
        if ("PORCENTAJE".equals(tipo) && valor > 100) throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100.");
        if (descuento > subtotal) throw new IllegalArgumentException("El descuento no puede superar el subtotal.");
        return Math.round(descuento * 100.0) / 100.0;
    }

    @FXML private void actualizarDescuento() {
        try { refrescarTotales(); }
        catch (Exception e) { lblEstado.setText(e.getMessage()); }
    }

    private void refrescarCarrito() { tblCarrito.setItems(FXCollections.observableArrayList(carrito)); refrescarTotales(); }

    private void refrescarTotales() {
        double subtotal = calcularSubtotal();
        double descuento = 0;
        try { descuento = calcularDescuento(subtotal); } catch (Exception ignored) { }
        lblSubtotal.setText(String.format("Q%.2f", subtotal));
        lblDescuento.setText(String.format("Q%.2f", descuento));
        lblTotal.setText(String.format("Q%.2f", subtotal - descuento));
    }

    @FXML private void registrar() {
        if (carrito.isEmpty()) { alert(Alert.AlertType.WARNING, "El carrito está vacío."); return; }
        Usuario u = Sesion.getUsuarioActual();
        if (u == null || !"cajero".equalsIgnoreCase(u.getRol())) { alert(Alert.AlertType.ERROR, "Se requiere una sesión activa de cajero."); return; }
        String cui = ventaEnCurso ? cuiVenta : textoCuiActual();
        if (!cui.isEmpty()) try { Long.parseLong(cui); } catch (NumberFormatException e) { alert(Alert.AlertType.WARNING, "El CUI debe ser numérico."); return; }

        try {
            double subtotal = calcularSubtotal();
            double descuento = calcularDescuento(subtotal);
            Integer autorizador = null;
            if (descuento > 0) autorizador = validarAutorizacionAdmin();
            Venta v = new Venta(subtotal, descuento, subtotal - descuento, "COMPLETADA", cui, u.getId());
            if (!ventaDAO.registrarVenta(v, new ArrayList<>(carrito), autorizador)) return;
            int id = v.getIdVenta();
            alert(Alert.AlertType.INFORMATION, "Venta #" + id + " registrada correctamente.");
            carrito.clear();
            finalizarVenta();
            refrescarCarrito();
            cargarLibros();
            quitarProteccionCierre();
            Main.cambiarVista("/org/paginalib3/view/comprobante.fxml", "Pagina-Libreria | Comprobante", 900, 760);
            Main.configurarVistaActual(x -> { if (x instanceof ComprobanteController c) c.cargarVenta(id); });
        } catch (Exception e) { alert(Alert.AlertType.ERROR, "No se pudo registrar la venta: " + e.getMessage()); }
    }

    private int validarAutorizacionAdmin() throws Exception {
        String username = txtUsuarioAutoriza.getText() == null ? "" : txtUsuarioAutoriza.getText().trim();
        String password = txtClaveAutoriza.getText() == null ? "" : txtClaveAutoriza.getText();
        if (username.isBlank() || password.isBlank()) throw new IllegalArgumentException("Un descuento requiere usuario y contraseña de administrador.");
        Usuario admin = usuarioDAO.iniciarSesion(username, Seguridad.sha256(password));
        if (admin == null || !"admin".equalsIgnoreCase(admin.getRol())) throw new IllegalArgumentException("La autorización no corresponde a un administrador activo.");
        return admin.getId();
    }

    @FXML private void limpiar() {
        if (ventaEnCurso && !confirmarCancelacion()) return;
        boolean fueCancelada = ventaEnCurso;
        carrito.clear();
        finalizarVenta();
        refrescarCatalogoVisible();
        txtCuiCliente.clear(); cmbCliente.getSelectionModel().clearSelection(); txtCantidad.setText("1");
        txtDescuento.clear(); txtUsuarioAutoriza.clear(); txtClaveAutoriza.clear(); cmbTipoDescuento.setValue("MONTO"); refrescarCarrito();
        lblEstado.setText(fueCancelada ? "Venta cancelada. Ya puedes seleccionar otro cliente." : "Formulario limpio.");
    }

    @FXML private void volver() {
        if (ventaEnCurso) {
            alert(Alert.AlertType.WARNING, "Hay una venta en curso. Regístrala o usa 'Cancelar venta' antes de volver.");
            return;
        }
        quitarProteccionCierre();
        Main.cambiarVista("/org/paginalib3/view/dashboard_cajero.fxml", "Pagina-Libreria | Dashboard Caja", 1100, 680);
    }

    private void iniciarVenta() {
        if (ventaEnCurso) return;

        ventaEnCurso = true;
        clienteVenta = cmbCliente.getValue();
        cuiVenta = textoCuiActual();
        cmbCliente.setDisable(true);
        txtCuiCliente.setDisable(true);
        btnLimpiar.setText("Cancelar venta");
        lblEstado.setText("Venta en curso: el cliente permanecerá fijo hasta registrar o cancelar la venta.");
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
        return txtCuiCliente.getText() == null ? "" : txtCuiCliente.getText().trim();
    }

    private boolean confirmarCancelacion() {
        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Se eliminarán todos los libros agregados y se liberará el cliente seleccionado. ¿Deseas cancelar la venta?",
                ButtonType.YES,
                ButtonType.NO);
        confirmacion.setTitle("Cancelar venta");
        confirmacion.setHeaderText("Hay una venta en curso");
        return confirmacion.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void configurarProteccionCierre() {
        if (Main.getStagePrincipal() == null) return;
        Main.getStagePrincipal().setOnCloseRequest(event -> {
            if (ventaEnCurso) {
                event.consume();
                alert(Alert.AlertType.WARNING, "Hay una venta en curso. Regístrala o cancélala antes de cerrar.");
            }
        });
    }

    private void quitarProteccionCierre() {
        if (Main.getStagePrincipal() != null) Main.getStagePrincipal().setOnCloseRequest(null);
    }

    private void alert(Alert.AlertType t, String m) { new Alert(t, m, ButtonType.OK).showAndWait(); }
}
