package org.paginalib3.model;

import java.time.LocalDateTime;

public class Venta {

    private int idVenta, idUsuario;
    private LocalDateTime fechaVenta;
    private double subtotal, descuento, total;
    private String estado, cuiCliente;

    public Venta() {
    }

    public Venta(int id, LocalDateTime f, double s, double d, double t, String e, String c, int u) {
        idVenta = id;
        fechaVenta = f;
        subtotal = s;
        descuento = d;
        total = t;
        estado = e;
        cuiCliente = c;
        idUsuario = u;
    }

    public Venta(double s, double d, double t, String e, String c, int u) {
        subtotal = s;
        descuento = d;
        total = t;
        estado = e;
        cuiCliente = c;
        idUsuario = u;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int v) {
        idVenta = v;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime v) {
        fechaVenta = v;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double v) {
        subtotal = v;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double v) {
        descuento = v;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double v) {
        total = v;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String v) {
        estado = v;
    }

    public String getCuiCliente() {
        return cuiCliente;
    }

    public void setCuiCliente(String v) {
        cuiCliente = v;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int v) {
        idUsuario = v;
    }

    public String getTotalFormateado() {
        return String.format("Q%.2f", total);
    }

    public String getSubtotalFormateado() {
        return String.format("Q%.2f", subtotal);
    }
}
