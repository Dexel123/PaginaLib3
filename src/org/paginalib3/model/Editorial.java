package org.paginalib3.model;

public class Editorial {

    private String nit;
    private String nombre;

    public Editorial(String nit, String nombre) {
        this.nit = nit;
        this.nombre = nombre;
    }

    public String getNit() {
        return nit;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
