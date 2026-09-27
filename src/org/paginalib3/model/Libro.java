package org.paginalib3.model;

import java.time.LocalDate;

public class Libro {

    private String isbn;
    private String titulo;
    private LocalDate fechaPublicacion;
    private double precio;
    private int idCategoria;
    private String nitEditorial;
    private int stockActual;
    private int stockMinimo;
    private boolean activo;
    private String autores;
    private String nombreCategoria;

    public Libro() {
    }

    public Libro(String isbn, String titulo, double precio, int stockActual, int stockMinimo, boolean activo, String autores) {
        this(isbn, titulo, null, precio, 0, null, stockActual, stockMinimo, activo, autores, null);
    }

    public Libro(String isbn, String titulo, LocalDate fechaPublicacion, double precio, int idCategoria,
            String nitEditorial, int stockActual, int stockMinimo, boolean activo) {
        this(isbn, titulo, fechaPublicacion, precio, idCategoria, nitEditorial, stockActual, stockMinimo, activo, "", null);
    }

    public Libro(String isbn, String titulo, LocalDate fechaPublicacion, double precio, int idCategoria,
            String nitEditorial, int stockActual, int stockMinimo, boolean activo,
            String autores, String nombreCategoria) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.fechaPublicacion = fechaPublicacion;
        this.precio = precio;
        this.idCategoria = idCategoria;
        this.nitEditorial = nitEditorial;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.activo = activo;
        this.autores = autores == null ? "" : autores;
        this.nombreCategoria = nombreCategoria;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNitEditorial() {
        return nitEditorial;
    }

    public void setNitEditorial(String nitEditorial) {
        this.nitEditorial = nitEditorial;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getAutores() {
        return autores;
    }

    public void setAutores(String autores) {
        this.autores = autores;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getPrecioFormateado() {
        return String.format("Q%.2f", precio);
    }

    public String getEstadoTexto() {
        return activo ? "ACTIVO" : "INACTIVO";
    }

    @Override
    public String toString() {
        return titulo + " (" + isbn + ")";
    }
}
