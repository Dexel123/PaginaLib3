package org.paginalib3.controller;

import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalib3.dao.LibroDAO;
import org.paginalib3.dao.impl.LibroDAOImpl;
import org.paginalib3.model.Libro;
import org.paginalib3.system.Main;
import org.paginalib3.util.Sesion;

public class LibroController {

    @FXML private TextField txtBuscar, txtIsbn, txtTitulo, txtPrecio, txtIdCategoria, txtNitEditorial, txtStockMinimo;
    @FXML private DatePicker dpFechaPublicacion;
    @FXML private CheckBox chkActivo;
    @FXML private Label lblEstado, lblStockActual;
    @FXML private Button btnGuardar, btnEstado;
    @FXML private TableView<Libro> tblLibros;
    @FXML private TableColumn<Libro, String> colIsbn, colTitulo, colEstado;
    @FXML private TableColumn<Libro, Double> colPrecio;
    @FXML private TableColumn<Libro, Integer> colStockActual, colStockMinimo;

    private final LibroDAO libroDAO = new LibroDAOImpl();
    private Libro seleccionado;

    @FXML
    private void initialize() {
        if (!esBodega()) return;
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoTexto"));
        chkActivo.setSelected(true);
        cargarLibros();
    }
 
    @FXML
    private void buscar() {
        try {
            String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim();
            if (texto.isEmpty()) {
                cargarLibros();
                return;
            }
            List<Libro> filtrados = libroDAO.listarTodos().stream()
                    .filter(l -> l.getIsbn().toLowerCase().contains(texto.toLowerCase())
                    || l.getTitulo().toLowerCase().contains(texto.toLowerCase()))
                    .toList();
            tblLibros.setItems(FXCollections.observableArrayList(filtrados));
            lblEstado.setText(filtrados.size() + " libro(s) encontrado(s).");
        } catch (SQLException e) {
            error("No se pudo buscar: " + mensaje(e));
        }
    }

    @FXML
    private void seleccionarLibro() {
        Libro libro = tblLibros.getSelectionModel().getSelectedItem();
        if (libro == null) return;
        seleccionado = libro;
        txtIsbn.setText(libro.getIsbn());
        txtIsbn.setDisable(true);
        txtTitulo.setText(libro.getTitulo());
        dpFechaPublicacion.setValue(libro.getFechaPublicacion());
        txtPrecio.setText(String.valueOf(libro.getPrecio()));
        txtIdCategoria.setText(String.valueOf(libro.getIdCategoria()));
        txtNitEditorial.setText(libro.getNitEditorial());
        txtStockMinimo.setText(String.valueOf(libro.getStockMinimo()));
        lblStockActual.setText(String.valueOf(libro.getStockActual()));
        chkActivo.setSelected(libro.isActivo());
        btnGuardar.setText("Actualizar");
        btnEstado.setText(libro.isActivo() ? "Desactivar" : "Activar");
        lblEstado.setText("Editando: " + libro.getTitulo());
    }

    @FXML
    private void guardar() {
        try {
            Libro libro = leerFormulario();
            if (seleccionado == null) {
                if (libroDAO.buscarPorIsbn(libro.getIsbn()) != null) {
                    advertencia("Ya existe un libro con ese ISBN.");
                    return;
                }
                libroDAO.insertar(libro);
                informacion("Libro registrado correctamente.");
            } else {
                libroDAO.actualizar(libro);
                informacion("Libro actualizado correctamente.");
            }
            limpiar();
            cargarLibros();
        } catch (IllegalArgumentException e) {
            advertencia(e.getMessage());
        } catch (SQLException e) {
            error("No se pudo guardar el libro: " + mensaje(e));
        }
    }

    @FXML
    private void cambiarEstado() {
        Libro libro = tblLibros.getSelectionModel().getSelectedItem();
        if (libro == null) {
            advertencia("Selecciona un libro.");
            return;
        }
        boolean nuevoEstado = !libro.isActivo();
        String accion = nuevoEstado ? "activar" : "desactivar";
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas " + accion + " '" + libro.getTitulo() + "'?", ButtonType.YES, ButtonType.NO);
        confirmacion.setHeaderText(null);
        if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        try {
            libroDAO.cambiarEstado(libro.getIsbn(), libro.getStockMinimo(), nuevoEstado);
            limpiar();
            cargarLibros();
            lblEstado.setText("Estado actualizado correctamente.");
        } catch (SQLException e) {
            error("No se pudo cambiar el estado: " + mensaje(e));
        }
    }

    @FXML
    private void nuevo() {
        limpiar();
        cargarLibros();
        lblEstado.setText("Formulario listo para un nuevo libro.");
    }

    @FXML
    private void volver() {
        Main.cambiarVista("/org/paginalib3/view/dashboard_bodega.fxml", "Pagina-Libreria | Dashboard Bodega", 1180, 720);
    }

    private void cargarLibros() {
        try {
            List<Libro> libros = libroDAO.listarTodos();
            tblLibros.setItems(FXCollections.observableArrayList(libros));
            lblEstado.setText(libros.size() + " libro(s) cargado(s).");
        } catch (SQLException e) {
            error("No se pudieron cargar los libros: " + mensaje(e));
        }
    }

    private Libro leerFormulario() {
        String isbn = texto(txtIsbn);
        String titulo = texto(txtTitulo);
        String nitEditorial = texto(txtNitEditorial);
        if (!isbn.matches("[0-9Xx-]{8,20}")) throw new IllegalArgumentException("El ISBN debe tener entre 8 y 20 caracteres y usar solo números, X o guiones.");
        if (titulo.isEmpty()) throw new IllegalArgumentException("El título es obligatorio.");
        if (nitEditorial.isEmpty()) throw new IllegalArgumentException("El NIT de editorial es obligatorio.");
        double precio;
        int categoria;
        int stockMinimo;
        try { precio = Double.parseDouble(texto(txtPrecio)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("El precio debe ser numérico."); }
        try { categoria = Integer.parseInt(texto(txtIdCategoria)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("La categoría debe ser un ID numérico."); }
        try { stockMinimo = Integer.parseInt(texto(txtStockMinimo)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("El stock mínimo debe ser un entero."); }
        if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        if (categoria <= 0) throw new IllegalArgumentException("La categoría debe ser mayor a 0.");
        if (stockMinimo < 0) throw new IllegalArgumentException("El stock mínimo no puede ser negativo.");
        int stockActual = seleccionado == null ? 0 : seleccionado.getStockActual();
        return new Libro(isbn, titulo, dpFechaPublicacion.getValue(), precio, categoria,
                nitEditorial, stockActual, stockMinimo, chkActivo.isSelected());
    }

    private void limpiar() {
        seleccionado = null;
        txtIsbn.setDisable(false);
        txtIsbn.clear();
        txtTitulo.clear();
        dpFechaPublicacion.setValue(null);
        txtPrecio.clear();
        txtIdCategoria.clear();
        txtNitEditorial.clear();
        txtStockMinimo.clear();
        lblStockActual.setText("0");
        chkActivo.setSelected(true);
        btnGuardar.setText("Guardar");
        btnEstado.setText("Activar / Desactivar");
        tblLibros.getSelectionModel().clearSelection();
    }

    private boolean esBodega() {
        if (Sesion.getUsuarioActual() != null && "bodega".equalsIgnoreCase(Sesion.getUsuarioActual().getRol())) return true;
        Main.cambiarVista("/org/paginalib3/view/login.fxml", "Pagina-Libreria | Iniciar sesión", 760, 560);
        return false;
    }

    private String texto(TextField campo) { return campo.getText() == null ? "" : campo.getText().trim(); }
    private String mensaje(SQLException e) { return e.getMessage() == null ? "Error de base de datos" : e.getMessage(); }
    private void informacion(String m) { new Alert(Alert.AlertType.INFORMATION, m, ButtonType.OK).showAndWait(); }
    private void advertencia(String m) { new Alert(Alert.AlertType.WARNING, m, ButtonType.OK).showAndWait(); }
    private void error(String m) { lblEstado.setText(m); new Alert(Alert.AlertType.ERROR, m, ButtonType.OK).showAndWait(); }
}
 