package org.paginalib3.dao;

import java.sql.SQLException;
import java.util.List;
import org.paginalib3.model.Editorial;

public interface EditorialDAO {

    List<Editorial> listarActivas() throws SQLException;
}
