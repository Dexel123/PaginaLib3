package org.paginalib3.model;

public class Editorial {

    private String nit;
    private String nombre;
    private boolean activo;

    public Editorial() {
    }

    public Editorial(String nit, String nombre, boolean activo) {
        this.nit = nit;
        this.nombre = nombre;
        this.activo = activo;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        String nombreSeguro = nombre == null ? "" : nombre;
        String nitSeguro = nit == null ? "" : nit;
        return nombreSeguro + " (" + nitSeguro + ")";
    }
}
