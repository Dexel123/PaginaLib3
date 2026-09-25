package org.paginalib3.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.paginalib3.model.ReporteLibroVendido;
import org.paginalib3.model.ReporteVenta;
import org.paginalib3.model.StockValorizado;

public interface ReporteDAO {
    List<ReporteVenta> ventasPorPeriodo(LocalDate desde, LocalDate hasta) throws SQLException;
    List<ReporteLibroVendido> librosMasVendidos(LocalDate desde, LocalDate hasta, int limite) throws SQLException;
    List<StockValorizado> stockValorizado() throws SQLException;
}
