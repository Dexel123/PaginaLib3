package org.paginalib3.model;

public class StockValorizado {

    private String isbn;
    private String titulo;
    private int stockActual;
    private double costoPromedio;
    private double precio;
    private double valorCosto;
    private double valorVenta;
    private double margenPotencial;

    public StockValorizado(String isbn, String titulo, int stockActual, double costoPromedio, double precio, double valorCosto, double valorVenta, double margenPotencial) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.stockActual = stockActual;
        this.costoPromedio = costoPromedio;
        this.precio = precio;
        this.valorCosto = valorCosto;
        this.valorVenta = valorVenta;
        this.margenPotencial = margenPotencial;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getStockActual() {
        return stockActual;
    }

    public double getCostoPromedio() {
        return costoPromedio;
    }

    public double getPrecio() {
        return precio;
    }

    public double getValorCosto() {
        return valorCosto;
    }

    public double getValorVenta() {
        return valorVenta;
    }

    public double getMargenPotencial() {
        return margenPotencial;
    }
}
