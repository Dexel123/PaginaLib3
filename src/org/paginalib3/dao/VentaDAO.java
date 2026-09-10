package org.paginalib3.dao;

import java.sql.SQLException;
import java.util.List;
import org.paginalib3.model.DetalleVenta;
import org.paginalib3.model.Venta;

public interface VentaDAO {
    boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) throws SQLException;
    boolean registrarVenta(Venta venta, List<DetalleVenta> detalles, Integer usuarioAutoriza) throws SQLException;
    Venta buscarPorId(int idVenta) throws SQLException;
    List<Venta> listarVentasDelDiaPorUsuario(int idUsuario) throws SQLException;
    boolean anularVenta(int idVenta, int idUsuario, String motivo) throws SQLException;
    boolean devolverVenta(int idVenta, int idUsuario, String motivo) throws SQLException;
}
