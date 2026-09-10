package org.paginalib3.model;

public class Libro {

    private String isbn;
    private String titulo;
    private String autores;
    private double precio;
    private int stockActual;
    private int stockMinimo;
    private boolean activo;

    public Libro() {}

    public Libro(String isbn, String titulo, double precio, int stockActual, int stockMinimo, boolean activo, String autores) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.precio = precio;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.activo = activo;
        this.autores = autores;
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
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

    public String getPrecioFormateado() {
        return String.format("Q%.2f", precio);
    }

    @Override
    public String toString() {
        return titulo + " (" + isbn + ")";
    }
}