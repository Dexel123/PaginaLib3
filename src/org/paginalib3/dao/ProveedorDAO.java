package org.paginalib3.dao;

import java.sql.SQLException;
import java.util.List;
import org.paginalib3.model.Proveedor;

public interface ProveedorDAO {
    List<Proveedor> listar() throws SQLException;
    boolean insertar(Proveedor proveedor) throws SQLException;
    boolean actualizar(Proveedor proveedor) throws SQLException;
    boolean desactivar(String nit) throws SQLException;
}
