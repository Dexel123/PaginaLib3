package org.paginalib3.model;

import java.time.LocalDate;

public class ReporteVenta {

    private LocalDate fecha;
    private int cantidadVentas;
    private double subtotal;
    private double descuentos;
    private double total;

    public ReporteVenta(LocalDate fecha, int cantidadVentas, double subtotal, double descuentos, double total) {
        this.fecha = fecha;
        this.cantidadVentas = cantidadVentas;
        this.subtotal = subtotal;
        this.descuentos = descuentos;
        this.total = total;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDescuentos() {
        return descuentos;
    }

    public double getTotal() {
        return total;
    }
}
