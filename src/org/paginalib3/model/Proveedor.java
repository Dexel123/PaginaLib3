package org.paginalib3.model;

public class Proveedor {

    private String nit;
    private String nombre;
    private String telefono;
    private String direccion;
    private String correo;
    private boolean activo;

    public Proveedor() {
    }

    public Proveedor(String nit, String nombre, String telefono, String direccion, String correo, boolean activo) {
        this.nit = nit;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.correo = correo;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getEstadoTexto() {
        return activo ? "ACTIVO" : "INACTIVO";
    }

    @Override
    public String toString() {
        return nombre + " (" + nit + ")";
    }
}