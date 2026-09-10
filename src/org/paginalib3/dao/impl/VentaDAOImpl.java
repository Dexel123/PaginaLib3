package org.paginalib3.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.paginalib3.dao.VentaDAO;
import org.paginalib3.model.DetalleVenta;
import org.paginalib3.model.Venta;
import org.paginalib3.util.Conexion;

public class VentaDAOImpl implements VentaDAO {

    private static final Logger LOG = Logger.getLogger(VentaDAOImpl.class.getName());

    @Override
    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) throws SQLException {
        return registrarVenta(venta, detalles, null);
    }

    @Override
    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles, Integer usuarioAutoriza) throws SQLException {
        if (venta == null || detalles == null || detalles.isEmpty()) {
            return false;
        }
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < detalles.size(); i++) {
            DetalleVenta d = detalles.get(i);
            if (d.getCantidad() <= 0 || d.getIsbn() == null || d.getIsbn().isBlank()) {
                throw new IllegalArgumentException("Detalle de venta inválido.");
            }
            if (i > 0) {
                json.append(',');
            }
            json.append("{\"isbn\":\"").append(esc(d.getIsbn())).append("\",\"cantidad\":").append(d.getCantidad()).append('}');
        }
        json.append(']');

        try (Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall("{CALL sp_registrar_venta(?,?,?,?,?,?)}")) {
            s.setInt(1, venta.getIdUsuario());
            if (venta.getCuiCliente() == null || venta.getCuiCliente().isBlank()) {
                s.setNull(2, Types.BIGINT);
            } else {
                s.setLong(2, Long.parseLong(venta.getCuiCliente()));
            }
            s.setBigDecimal(3, java.math.BigDecimal.valueOf(venta.getDescuento()));
            if (usuarioAutoriza == null) {
                s.setNull(4, Types.INTEGER);
            } else {
                s.setInt(4, usuarioAutoriza);
            }
            s.setString(5, json.toString());
            s.registerOutParameter(6, Types.INTEGER);
            s.execute();
            venta.setIdVenta(s.getInt(6));
            venta.setEstado("COMPLETADA");
            return true;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error registrando venta", e);
            throw e;
        }
    }

    @Override
    public Venta buscarPorId(int id) throws SQLException {
        try (Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall("{CALL sp_buscarventa(?)}")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    return map(r);
                }
            }
        }
        return null;
    }

    @Override
    public List<Venta> listarVentasDelDiaPorUsuario(int idUsuario) throws SQLException {
        List<Venta> l = new ArrayList<>();
        try (Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall("{CALL sp_ventasdeldiaporusuario(?)}")) {
            s.setInt(1, idUsuario);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    l.add(map(r));
                }
            }
        }
        return l;
    }

    @Override
    public boolean anularVenta(int idVenta, int idUsuario, String motivo) throws SQLException {
        return ejecutarCambioEstado("sp_anularventa", idVenta, idUsuario, motivo);
    }

    @Override
    public boolean devolverVenta(int idVenta, int idUsuario, String motivo) throws SQLException {
        return ejecutarCambioEstado("sp_devolverventa", idVenta, idUsuario, motivo);
    }

    private boolean ejecutarCambioEstado(String procedimiento, int idVenta, int idUsuario, String motivo) throws SQLException {
        if (idVenta <= 0 || idUsuario <= 0) {
            throw new IllegalArgumentException("Identificador inválido.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio.");
        }
        try (Connection c = Conexion.getInstancia().conectar(); CallableStatement s = c.prepareCall("{CALL " + procedimiento + "(?,?,?)}")) {
            s.setInt(1, idVenta);
            s.setInt(2, idUsuario);
            s.setString(3, motivo.trim());
            s.execute();
            return true;
        }
    }

    private Venta map(ResultSet r) throws SQLException {
        Timestamp t = r.getTimestamp("fecha_venta");
        return new Venta(r.getInt("id_venta"), t == null ? null : t.toLocalDateTime(),
                r.getDouble("subtotal"), r.getDouble("descuento"), r.getDouble("total"),
                r.getString("estado"), r.getString("cui_cliente"), r.getInt("id_usuario"));
    }

    private String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
