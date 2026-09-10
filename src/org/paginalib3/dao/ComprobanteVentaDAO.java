package org.paginalib3.dao;

import java.sql.SQLException;
import java.util.List;
import org.paginalib3.model.DetalleVenta;
import org.paginalib3.model.Venta;

public interface ComprobanteVentaDAO {
    Venta buscarVenta(int idVenta) throws SQLException;
    List<DetalleVenta> listarDetalles(int idVenta) throws SQLException;
}
