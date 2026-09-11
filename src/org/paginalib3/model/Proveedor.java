package org.paginalib3.model;

public class Proveedor {

    private String nitProveedor;
    private String nombreProveedor;
    private boolean activo;

    public Proveedor() {}

    public Proveedor(String nitProveedor, String nombreProveedor, boolean activo) {
        this.nitProveedor = nitProveedor;
        this.nombreProveedor = nombreProveedor;
        this.activo = activo;
    }

    public String getNitProveedor() {
        return nitProveedor;
    }

    public void setNitProveedor(String nitProveedor) {
        this.nitProveedor = nitProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return nitProveedor + " - " + nombreProveedor;
    }
}

