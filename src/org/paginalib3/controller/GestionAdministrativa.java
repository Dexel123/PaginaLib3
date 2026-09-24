package org.paginalib3.controller;

import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalib3.dao.AdminDAO;
import org.paginalib3.dao.CategoriaDAO;
import org.paginalib3.dao.LibroDAO;
import org.paginalib3.dao.ProveedorDAO;
import org.paginalib3.dao.impl.AdminDAOImpl;
import org.paginalib3.dao.impl.CategoriaDAOImpl;
import org.paginalib3.dao.impl.LibroDAOImpl;
import org.paginalib3.dao.impl.ProveedorDAOImpl;
import org.paginalib3.model.Categoria;
import org.paginalib3.model.Libro;
import org.paginalib3.model.Proveedor;
import org.paginalib3.system.Main;
import org.paginalib3.util.MensajesUI;
import org.paginalib3.util.Permisos;
import org.paginalib3.util.Sesion;

public class GestionAdministrativaController {

    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Categoria, Integer> colCategoriaId;
    @FXML private TableColumn<Categoria, String> colCategoriaNombre;
    @FXML private TextField txtCategoria;

    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, String> colNit, colProveedor, colTelefono, colCorreo, colEstadoProveedor;
    @FXML private TextField txtNit, txtProveedor, txtTelefono, txtCorreo;
    @FXML private TextArea txtDireccion;

    @FXML private TableView<Libro> tablaLibros;
    @FXML private TableColumn<Libro, String> colLibroIsbn, colLibroTitulo;
    @FXML private TableColumn<Libro, Double> colLibroPrecio;
    @FXML private TextField txtNuevoPrecio, txtMotivo;
    @FXML private Label lblLibroSeleccionado, lblEstado;

    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
    private final ProveedorDAO proveedorDAO = new ProveedorDAOImpl();
    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final AdminDAO adminDAO = new AdminDAOImpl();

    private Categoria categoriaSeleccionada;
    private Proveedor proveedorSeleccionado;
    private Libro libroSeleccionado;

    @FXML
    private void initialize() {
        if (!Permisos.requerirAdmin("Gestión administrativa")) {
            return;
        }

        tablaCategorias.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaProveedores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaLibros.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colCategoriaId.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        colCategoriaNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        colNit.setCellValueFactory(new PropertyValueFactory<>("nit"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colEstadoProveedor.setCellValueFactory(new PropertyValueFactory<>("estadoTexto"));
        colLibroIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colLibroTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colLibroPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        tablaCategorias.getSelectionModel().selectedItemProperty()
                .addListener((o, anterior, actual) -> seleccionarCategoria(actual));
        tablaProveedores.getSelectionModel().selectedItemProperty()
                .addListener((o, anterior, actual) -> seleccionarProveedor(actual));
        tablaLibros.getSelectionModel().selectedItemProperty()
                .addListener((o, anterior, actual) -> seleccionarLibro(actual));

        recargarTodo();
    }

    private void recargarTodo() {
        cargarCategorias();
        cargarProveedores();
        cargarLibros();
    }

    private void cargarCategorias() {
        try {
            tablaCategorias.setItems(FXCollections.observableArrayList(categoriaDAO.listar()));
        } catch (SQLException e) {
            error("No se pudieron cargar las categorías.", e);
        }
    }

    private void cargarProveedores() {
        try {
            tablaProveedores.setItems(FXCollections.observableArrayList(proveedorDAO.listar()));
        } catch (SQLException e) {
            error("No se pudieron cargar los proveedores.", e);
        }
    }

    private void cargarLibros() {
        try {
            tablaLibros.setItems(FXCollections.observableArrayList(libroDAO.listarTodos()));
        } catch (SQLException e) {
            error("No se pudieron cargar los libros.", e);
        }
    }

    private void seleccionarCategoria(Categoria categoria) {
        categoriaSeleccionada = categoria;
        txtCategoria.setText(categoria == null ? "" : categoria.getNombreCategoria());
    }

    @FXML
    private void nuevaCategoria() {
        categoriaSeleccionada = null;
        tablaCategorias.getSelectionModel().clearSelection();
        txtCategoria.clear();
        txtCategoria.requestFocus();
        estado("Formulario preparado para una categoría nueva.");
    }

    @FXML
    private void guardarCategoria() {
        try {
            if (categoriaSeleccionada == null) {
                categoriaDAO.insertar(txtCategoria.getText());
                estado("Categoría creada correctamente.");
            } else {
                categoriaDAO.actualizar(categoriaSeleccionada.getIdCategoria(), txtCategoria.getText());
                estado("Categoría actualizada correctamente.");
            }
            nuevaCategoria();
            cargarCategorias();
        } catch (Exception e) {
            error("No se pudo guardar la categoría.", e);
        }
    }

    @FXML
    private void eliminarCategoria() {
        if (categoriaSeleccionada == null) {
            MensajesUI.advertencia("Categorías", "Selecciona una categoría antes de eliminar.");
            return;
        }
        if (!MensajesUI.confirmar(
                "Eliminar categoría",
                "¿Deseas eliminar la categoría '" + categoriaSeleccionada.getNombreCategoria() + "'?\n"
                + "Si está asignada a libros, MySQL impedirá la eliminación.")) {
            return;
        }
        try {
            categoriaDAO.eliminar(categoriaSeleccionada.getIdCategoria());
            estado("Categoría eliminada correctamente.");
            nuevaCategoria();
            cargarCategorias();
        } catch (Exception e) {
            error("No se pudo eliminar la categoría. Puede estar asignada a uno o más libros.", e);
        }
    }

    private void seleccionarProveedor(Proveedor proveedor) {
        proveedorSeleccionado = proveedor;
        if (proveedor == null) {
            limpiarProveedor();
            return;
        }
        txtNit.setText(proveedor.getNit());
        txtNit.setDisable(true);
        txtProveedor.setText(proveedor.getNombre());
        txtTelefono.setText(proveedor.getTelefono());
        txtDireccion.setText(proveedor.getDireccion());
        txtCorreo.setText(proveedor.getCorreo());
    }

    @FXML
    private void nuevoProveedor() {
        proveedorSeleccionado = null;
        tablaProveedores.getSelectionModel().clearSelection();
        limpiarProveedor();
        txtNit.setDisable(false);
        txtNit.requestFocus();
        estado("Formulario preparado para un proveedor nuevo.");
    }

    private void limpiarProveedor() {
        txtNit.clear();
        txtProveedor.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtCorreo.clear();
    }

    @FXML
    private void guardarProveedor() {
        try {
            Proveedor proveedor = new Proveedor(
                    texto(txtNit),
                    texto(txtProveedor),
                    texto(txtTelefono),
                    texto(txtDireccion),
                    texto(txtCorreo),
                    true);

            if (proveedorSeleccionado == null) {
                proveedorDAO.insertar(proveedor);
                estado("Proveedor registrado correctamente.");
            } else {
                proveedorDAO.actualizar(proveedor);
                estado("Proveedor actualizado correctamente.");
            }
            nuevoProveedor();
            cargarProveedores();
        } catch (Exception e) {
            error("No se pudo guardar el proveedor.", e);
        }
    }

    @FXML
    private void desactivarProveedor() {
        if (proveedorSeleccionado == null) {
            MensajesUI.advertencia("Proveedores", "Selecciona un proveedor antes de desactivar.");
            return;
        }
        if (!proveedorSeleccionado.isActivo()) {
            MensajesUI.advertencia("Proveedores", "El proveedor seleccionado ya está inactivo.");
            return;
        }
        if (!MensajesUI.confirmar(
                "Desactivar proveedor",
                "¿Deseas desactivar a '" + proveedorSeleccionado.getNombre() + "'?")) {
            return;
        }
        try {
            proveedorDAO.desactivar(proveedorSeleccionado.getNit());
            estado("Proveedor desactivado correctamente.");
            nuevoProveedor();
            cargarProveedores();
        } catch (Exception e) {
            error("No se pudo desactivar el proveedor.", e);
        }
    }

    private void seleccionarLibro(Libro libro) {
        libroSeleccionado = libro;
        lblLibroSeleccionado.setText(libro == null
                ? "Selecciona un libro de la tabla"
                : libro.getTitulo() + " · " + libro.getIsbn()
                + " · precio actual Q" + String.format("%,.2f", libro.getPrecio()));
        txtNuevoPrecio.clear();
        txtMotivo.clear();
    }

    @FXML
    private void actualizarPrecio() {
        if (libroSeleccionado == null) {
            MensajesUI.advertencia("Precios", "Selecciona un libro para modificar su precio.");
            return;
        }
        try {
            double precio = Double.parseDouble(texto(txtNuevoPrecio));
            if (precio <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor que cero.");
            }
            if (Sesion.getUsuarioActual() == null) {
                throw new IllegalStateException("No hay una sesión administrativa activa.");
            }

            adminDAO.actualizarPrecio(
                    libroSeleccionado.getIsbn(),
                    precio,
                    Sesion.getUsuarioActual().getId(),
                    texto(txtMotivo));

            estado("Precio actualizado y registrado en el historial.");
            txtNuevoPrecio.clear();
            txtMotivo.clear();
            cargarLibros();
        } catch (NumberFormatException e) {
            MensajesUI.advertencia("Precio inválido", "Ingresa un precio numérico válido.");
        } catch (Exception e) {
            error("No se pudo actualizar el precio.", e);
        }
    }

    @FXML
    private void volver() {
        Main.cambiarVista(
                "/org/paginalib3/view/dashboard_admin.fxml",
                "Pagina-Libreria | Administración", 1180, 720);
    }

    private String texto(TextField campo) {
        return campo.getText() == null ? "" : campo.getText().trim();
    }

    private String texto(TextArea campo) {
        return campo.getText() == null ? "" : campo.getText().trim();
    }

    private void estado(String mensaje) {
        lblEstado.setText(mensaje);
    }

    private void error(String contexto, Exception error) {
        String detalle = MensajesUI.mensajeTecnico(error);
        lblEstado.setText(contexto + " " + detalle);
        MensajesUI.error("Gestión administrativa", contexto + "\n\nDetalle: " + detalle, error);
    }
}