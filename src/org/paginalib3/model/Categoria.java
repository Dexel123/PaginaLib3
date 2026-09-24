package org.paginalib3.model;

public class Categoria {

    private int idCategoria;
    private String nombreCategoria;
    private boolean activo = true;

    public Categoria() {
    }

    public Categoria(int idCategoria, String nombreCategoria) {
        this(idCategoria, nombreCategoria, true);
    }

    public Categoria(int idCategoria, String nombreCategoria, boolean activo) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.activo = activo;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getEstadoTexto() {
        return activo ? "Activa" : "Inactiva";
    }

    @Override
    public String toString() {
        return nombreCategoria == null ? "" : nombreCategoria;
    }
}
