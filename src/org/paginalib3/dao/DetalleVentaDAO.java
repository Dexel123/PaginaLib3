package org.paginalib3.dao;

import org.paginalib3.model.DetalleVenta;
import java.sql.SQLException;
import java.util.List;

public interface DetalleVentaDAO {

    List<DetalleVenta> listarPorVenta(int idVenta) throws SQLException;
    
}