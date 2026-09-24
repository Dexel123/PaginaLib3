package org.paginalib3.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.paginalib3.model.ReporteVenta;

public interface ReporteDAO {

    List<ReporteVenta> ventasPorPeriodo(LocalDate desde, LocalDate hasta) throws SQLException;
}
