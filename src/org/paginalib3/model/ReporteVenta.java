package org.paginalib3.model;

import java.time.LocalDate;

public class ReporteVenta {
    private final int idVenta;
    private final String numeroComprobante;
    private final LocalDate fecha;
    private final String cliente;
    private final String cajero;
    private final String estado;
    private final int cantidadProductos;
    private final double subtotal;
    private final double descuentos;
    private final double total;

    public ReporteVenta(int idVenta, String numeroComprobante, LocalDate fecha,
                        String cliente, String cajero, String estado,
                        int cantidadProductos, double subtotal,
                        double descuentos, double total) {
        this.idVenta = idVenta;
        this.numeroComprobante = numeroComprobante;
        this.fecha = fecha;
        this.cliente = cliente;
        this.cajero = cajero;
        this.estado = estado;
        this.cantidadProductos = cantidadProductos;
        this.subtotal = subtotal;
        this.descuentos = descuentos;
        this.total = total;
    }

    public int getIdVenta() { return idVenta; }
    public String getNumeroComprobante() { return numeroComprobante; }
    public LocalDate getFecha() { return fecha; }
    public String getCliente() { return cliente; }
    public String getCajero() { return cajero; }
    public String getEstado() { return estado; }
    public int getCantidadProductos() { return cantidadProductos; }
    public double getSubtotal() { return subtotal; }
    public double getDescuentos() { return descuentos; }
    public double getTotal() { return total; }
}
