package org.paginalib3.model;
 
public class Cliente {
 
    private String cui, nombre, apellido, correo;
 
    public Cliente() {
    }
 
    public Cliente(String cui, String nombre, String apellido, String correo) {
        this.cui = cui;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
    }
 
    public String getCuiCliente() {
        return cui;
    }
 
    public void setCuiCliente(String v) {
        cui = v;
    }
 
    public String getNombreCliente() {
        return nombre;
    }
 
    public void setNombreCliente(String v) {
        nombre = v;
    }
 
    public String getApellidoCliente() {
        return apellido;
    }
 
    public void setApellidoCliente(String v) {
        apellido = v;
    }
 
    public String getCorreo() {
        return correo;
    }
 
    public void setCorreo(String v) {
        correo = v;
    }
 
    public String getNombreCompleto() {
        return (nombre + " " + apellido).trim();
    }
 
    @Override
    public String toString() {
        return getNombreCompleto() + " - CUI " + cui;
    }
}