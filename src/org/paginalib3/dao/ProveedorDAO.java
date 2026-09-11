package org.paginalib3.dao;

import org.paginalib3.model.Proveedor;
import java.sql.SQLException;
import java.util.List;

public interface ProveedorDAO {

    List<Proveedor> listarActivos() throws SQLException;
}
