package org.paginalib3.controller;

import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalib3.dao.CategoriaDAO;
import org.paginalib3.dao.LibroDAO;
import org.paginalib3.dao.impl.CategoriaDAOImpl;
import org.paginalib3.dao.impl.LibroDAOImpl;
import org.paginalib3.model.Categoria;
import org.paginalib3.model.Libro;
import org.paginalib3.model.Usuario;
import org.paginalib3.util.Permisos;
import org.paginalib3.util.Sesion;

public class LibroController {

    @FXML private TextField txtBuscar, txtIsbn, txtTitulo, txtPrecio, txtNitEditorial, txtStockMinimo, txtStockInicial;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private DatePicker dpFechaPublicacion;
    @FXML private CheckBox chkActivo;
    @FXML private Label lblEstado, lblStockActual;
    @FXML private Button btnGuardar, btnEstado;
    @FXML private TableView<Libro> tblLibros;
    @FXML private TableColumn<Libro, String> colIsbn, colTitulo, colCategoria, colEstado;
    @FXML private TableColumn<Libro, Double> colPrecio;
    @FXML private TableColumn<Libro, Integer> colStockActual, colStockMinimo;

    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
    private Libro seleccionado;

    @FXML
    private void initialize() {
        if (!Permisos.puedeInventario()) {
            Permisos.volverDashboardSegunRol();
            return;
        }
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoTexto"));
        txtStockMinimo.setDisable(true);
        chkActivo.setDisable(true);
        limpiar();
        cargarCategorias(null);
        cargarLibros();
    }

    @FXML
    private void buscar() {
        try {
            String texto = texto(txtBuscar);
            List<Libro> filtrados = texto.isEmpty() ? libroDAO.listarTodos() : libroDAO.listarTodos().stream()
                    .filter(l -> contiene(l.getIsbn(), texto) || contiene(l.getTitulo(), texto)
                    || contiene(l.getNombreCategoria(), texto) || contiene(l.getAutores(), texto))
                    .toList();
            tblLibros.setItems(FXCollections.observableArrayList(filtrados));
            lblEstado.setText(filtrados.size() + " libro(s) encontrado(s).");
        } catch (SQLException e) { error("No se pudo buscar: " + mensaje(e)); }
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
        txtNitEditorial.setText(libro.getNitEditorial());
        txtStockMinimo.setText(String.valueOf(libro.getStockMinimo()));
        txtStockInicial.setText("0");
        txtStockInicial.setDisable(true);
        lblStockActual.setText(String.valueOf(libro.getStockActual()));
        chkActivo.setSelected(libro.isActivo());
        seleccionarCategoria(libro.getIdCategoria());
        btnGuardar.setText("Actualizar ficha");
        btnEstado.setText(libro.isActivo() ? "Desactivar libro" : "Activar libro");
        lblEstado.setText("Editando: " + libro.getTitulo() + ". El stock se modifica solo mediante movimientos de inventario.");
    }

    @FXML
    private void guardar() {
        try {
            Libro libro = leerFormulario();
            if (seleccionado == null) {
                if (libroDAO.buscarPorIsbn(libro.getIsbn()) != null) {
                    advertencia("Ya existe un libro con ese ISBN."); return;
                }
                int stockInicial = parseEntero(txtStockInicial, "El stock inicial debe ser un entero mayor o igual a 0.");
                if (stockInicial < 0) throw new IllegalArgumentException("El stock inicial no puede ser negativo.");
                Usuario u = Sesion.getUsuarioActual();
                libroDAO.insertarConStockInicial(libro, stockInicial, u.getId());
                informacion("Libro registrado con " + stockInicial + " unidad(es) disponibles para venta.");
            } else {
                libroDAO.actualizar(libro);
                informacion("Ficha del libro actualizada. El stock mínimo y el stock actual no fueron modificados.");
            }
            limpiar();
            cargarCategorias(null);
            cargarLibros();
        } catch (IllegalArgumentException e) { advertencia(e.getMessage()); }
          catch (SQLException e) { error("No se pudo guardar el libro: " + mensaje(e)); }
    }

    @FXML
    private void crearCategoria() {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Nueva categoría");
        dialogo.setHeaderText("Crear una categoría para el catálogo");
        dialogo.setContentText("Nombre:");
        dialogo.showAndWait().ifPresent(nombre -> {
            try {
                categoriaDAO.insertar(nombre);
                cargarCategorias(nombre.trim());
                lblEstado.setText("Categoría creada y seleccionada.");
            } catch (Exception e) { error("No se pudo crear la categoría: " + e.getMessage()); }
        });
    }

    @FXML
    private void cambiarEstado() {
        Libro libro = tblLibros.getSelectionModel().getSelectedItem();
        if (libro == null) { advertencia("Selecciona un libro."); return; }
        boolean nuevoEstado = !libro.isActivo();
        String accion = nuevoEstado ? "activar" : "desactivar";
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas " + accion + " '" + libro.getTitulo() + "'?", ButtonType.YES, ButtonType.NO);
        confirmacion.setHeaderText(null);
        if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        try {
            libroDAO.cambiarEstado(libro.getIsbn(), nuevoEstado);
            limpiar(); cargarLibros();
            lblEstado.setText("Estado actualizado correctamente.");
        } catch (SQLException e) { error("No se pudo cambiar el estado: " + mensaje(e)); }
    }

    @FXML private void nuevo() { limpiar(); cargarLibros(); lblEstado.setText("Formulario listo para un nuevo libro."); }
    @FXML private void volver() { Permisos.volverDashboardSegunRol(); }

    private void cargarLibros() {
        try {
            List<Libro> libros = libroDAO.listarTodos();
            tblLibros.setItems(FXCollections.observableArrayList(libros));
            lblEstado.setText(libros.size() + " libro(s) cargado(s).");
        } catch (SQLException e) { error("No se pudieron cargar los libros: " + mensaje(e)); }
    }

    private void cargarCategorias(String seleccionarNombre) {
        try {
            List<Categoria> categorias = categoriaDAO.listar();
            cmbCategoria.setItems(FXCollections.observableArrayList(categorias));
            if (seleccionarNombre != null) categorias.stream()
                    .filter(c -> c.getNombreCategoria().equalsIgnoreCase(seleccionarNombre))
                    .findFirst().ifPresent(cmbCategoria::setValue);
        } catch (SQLException e) { error("No se pudieron cargar categorías: " + mensaje(e)); }
    }

    private void seleccionarCategoria(int id) {
        if (cmbCategoria.getItems() == null) return;
        cmbCategoria.getItems().stream().filter(c -> c.getIdCategoria() == id).findFirst().ifPresent(cmbCategoria::setValue);
    }

    private Libro leerFormulario() {
        String isbn = texto(txtIsbn);
        String titulo = texto(txtTitulo);
        String nitEditorial = texto(txtNitEditorial);
        Categoria categoria = cmbCategoria.getValue();
        if (!isbn.matches("[0-9Xx-]{8,20}")) throw new IllegalArgumentException("El ISBN debe tener entre 8 y 20 caracteres y usar solo números, X o guiones.");
        if (titulo.isEmpty()) throw new IllegalArgumentException("El título es obligatorio.");
        if (nitEditorial.isEmpty()) throw new IllegalArgumentException("El NIT de editorial es obligatorio.");
        if (categoria == null) throw new IllegalArgumentException("Selecciona una categoría.");
        double precio;
        try { precio = Double.parseDouble(texto(txtPrecio)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("El precio debe ser numérico."); }
        if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        int stockActual = seleccionado == null ? 0 : seleccionado.getStockActual();
        int stockMinimo = seleccionado == null ? 0 : seleccionado.getStockMinimo();
        boolean activo = seleccionado == null || seleccionado.isActivo();
        return new Libro(isbn, titulo, dpFechaPublicacion.getValue(), precio, categoria.getIdCategoria(),
                nitEditorial, stockActual, stockMinimo, activo, seleccionado == null ? "" : seleccionado.getAutores(), categoria.getNombreCategoria());
    }

    private int parseEntero(TextField campo, String mensaje) {
        try { return Integer.parseInt(texto(campo)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(mensaje); }
    }

    private void limpiar() {
        seleccionado = null;
        txtIsbn.setDisable(false); txtIsbn.clear(); txtTitulo.clear(); dpFechaPublicacion.setValue(null);
        txtPrecio.clear(); txtNitEditorial.clear(); txtStockMinimo.setText("0"); txtStockInicial.setText("0");
        txtStockInicial.setDisable(false); lblStockActual.setText("0"); chkActivo.setSelected(true);
        cmbCategoria.setValue(null); btnGuardar.setText("Guardar libro"); btnEstado.setText("Activar / Desactivar");
        if (tblLibros != null) tblLibros.getSelectionModel().clearSelection();
    }

    private boolean contiene(String base, String texto) { return base != null && base.toLowerCase().contains(texto.toLowerCase()); }
    private String texto(TextField campo) { return campo.getText() == null ? "" : campo.getText().trim(); }
    private String mensaje(SQLException e) { return e.getMessage() == null ? "Error de base de datos" : e.getMessage(); }
    private void informacion(String m) { new Alert(Alert.AlertType.INFORMATION, m, ButtonType.OK).showAndWait(); }
    private void advertencia(String m) { new Alert(Alert.AlertType.WARNING, m, ButtonType.OK).showAndWait(); }
    private void error(String m) { if (lblEstado != null) lblEstado.setText(m); new Alert(Alert.AlertType.ERROR, m, ButtonType.OK).showAndWait(); }
}
