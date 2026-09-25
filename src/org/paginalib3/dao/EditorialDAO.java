package org.paginalib3.dao;

import java.sql.SQLException;
import java.util.List;
import org.paginalib3.model.Editorial;

public interface EditorialDAO {

    List<Editorial> listar() throws SQLException;

    boolean existe(String nit) throws SQLException;
}
