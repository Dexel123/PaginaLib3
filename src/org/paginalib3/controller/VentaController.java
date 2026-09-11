package org.paginalib3.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    private final List<DetalleVenta> carrito = new ArrayList<>();
    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private List<Libro> libros = new ArrayList<>();

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
        try { libros = libroDAO.listar(); tblLibros.setItems(FXCollections.observableArrayList(libros)); }
        catch (Exception e) { lblEstado.setText("No se pudieron cargar libros: " + e.getMessage()); }
    }

    @FXML private void buscarLibro() {
        try {
            String t = txtFiltroLibro.getText();
            tblLibros.setItems(FXCollections.observableArrayList(libroDAO.buscar(t)));
        } catch (Exception e) { lblEstado.setText("Error al buscar: " + e.getMessage()); }
    }

    @FXML private void seleccionarCliente() {
        Cliente c = cmbCliente.getValue();
        if (c != null) txtCuiCliente.setText(c.getCuiCliente());
    }

    @FXML private void agregar() {
        Libro l = tblLibros.getSelectionModel().getSelectedItem();
        if (l == null) { alert(Alert.AlertType.WARNING, "Selecciona un libro."); return; }
        try {
            int c = Integer.parseInt(txtCantidad.getText().trim());
            if (c <= 0) throw new NumberFormatException();
            DetalleVenta d = carrito.stream().filter(x -> x.getIsbn().equals(l.getIsbn())).findFirst().orElse(null);
            int total = c + (d == null ? 0 : d.getCantidad());
            if (total > l.getStockActual()) { alert(Alert.AlertType.WARNING, "Stock insuficiente. Disponible: " + l.getStockActual()); return; }
            if (d == null) carrito.add(new DetalleVenta(l.getIsbn(), c, l.getPrecio(), c * l.getPrecio()));
            else { d.setCantidad(total); d.setSubtotal(total * d.getPrecioUnitario()); }
            refrescarCarrito();
            txtCantidad.setText("1");
        } catch (NumberFormatException e) { alert(Alert.AlertType.WARNING, "La cantidad debe ser un entero mayor a 0."); }
    }

    @FXML private void eliminar() {
        DetalleVenta d = tblCarrito.getSelectionModel().getSelectedItem();
        if (d == null) { alert(Alert.AlertType.WARNING, "Selecciona un producto del carrito."); return; }
        carrito.remove(d); refrescarCarrito();
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
        String cui = txtCuiCliente.getText().trim();
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
            refrescarCarrito();
            cargarLibros();
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
        carrito.clear(); txtCuiCliente.clear(); cmbCliente.getSelectionModel().clearSelection(); txtCantidad.setText("1");
        txtDescuento.clear(); txtUsuarioAutoriza.clear(); txtClaveAutoriza.clear(); cmbTipoDescuento.setValue("MONTO"); refrescarCarrito();
    }

    @FXML private void volver() { Main.cambiarVista("/org/paginalib3/view/dashboard_cajero.fxml", "Pagina-Libreria | Dashboard Caja", 1100, 680); }

    private void alert(Alert.AlertType t, String m) { new Alert(t, m, ButtonType.OK).showAndWait(); }
}
