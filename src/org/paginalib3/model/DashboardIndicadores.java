package org.paginalib3.model;

public class DashboardIndicadores {

    private double ventasTotales;
    private double ventasHoy;
    private int transaccionesHoy;
    private int librosActivos;
    private int unidadesEnStock;
    private double inventarioValorizadoCosto;
    private int usuariosActivos;
    private int librosStockCritico;

    public double getVentasTotales() {
        return ventasTotales;
    }

    public void setVentasTotales(double ventasTotales) {
        this.ventasTotales = ventasTotales;
    }

    public double getVentasHoy() {
        return ventasHoy;
    }

    public void setVentasHoy(double ventasHoy) {
        this.ventasHoy = ventasHoy;
    }

    public int getTransaccionesHoy() {
        return transaccionesHoy;
    }
  
    public void setTransaccionesHoy(int transaccionesHoy) {
        this.transaccionesHoy = transaccionesHoy;
    }

    public int getLibrosActivos() {
        return librosActivos;
    }

    public void setLibrosActivos(int librosActivos) {
        this.librosActivos = librosActivos;
    }

    public int getUnidadesEnStock() {
        return unidadesEnStock;
    }

    public void setUnidadesEnStock(int unidadesEnStock) {
        this.unidadesEnStock = unidadesEnStock;
    }

    public double getInventarioValorizadoCosto() {
        return inventarioValorizadoCosto;
    }

    public void setInventarioValorizadoCosto(double inventarioValorizadoCosto) {
        this.inventarioValorizadoCosto = inventarioValorizadoCosto;
    }

    public int getUsuariosActivos() {
        return usuariosActivos;
    }

    public void setUsuariosActivos(int usuariosActivos) {
        this.usuariosActivos = usuariosActivos;
    }

    public int getLibrosStockCritico() {
        return librosStockCritico;
    }

    public void setLibrosStockCritico(int librosStockCritico) {
        this.librosStockCritico = librosStockCritico;
    }
}
