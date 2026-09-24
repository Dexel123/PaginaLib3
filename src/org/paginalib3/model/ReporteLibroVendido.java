package org.paginalib3.model;

public class ReporteLibroVendido {

    private String isbn;
    private String titulo;
    private int unidadesNetas;
    private double ingresosBrutos;

    public ReporteLibroVendido(String isbn, String titulo, int unidadesNetas, double ingresosBrutos) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.unidadesNetas = unidadesNetas;
        this.ingresosBrutos = ingresosBrutos;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getUnidadesNetas() {
        return unidadesNetas;
    }

    public double getIngresosBrutos() {
        return ingresosBrutos;
    }
}
