package org.paginalib3.model;

public class DetalleVenta {

    private int idDetalle, idVenta, cantidad;
    private String isbn;
    private double precioUnitario, subtotal;

    public DetalleVenta() {
    }

    public DetalleVenta(int id, int venta, String i, int c, double p, double s) {
        idDetalle = id;
        idVenta = venta;
        isbn = i;
        cantidad = c;
        precioUnitario = p;
        subtotal = s;
    }

    public DetalleVenta(String i, int c, double p, double s) {
        isbn = i;
        cantidad = c;
        precioUnitario = p;
        subtotal = s;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int v) {
        idDetalle = v;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int v) {
        idVenta = v;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String v) {
        isbn = v;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int v) {
        cantidad = v;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double v) {
        precioUnitario = v;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double v) {
        subtotal = v;
    }

    public String getPrecioUnitarioFormateado() {
        return String.format("Q%.2f", precioUnitario);
    }

    public String getSubtotalFormateado() {
        return String.format("Q%.2f", subtotal);
    }
}
