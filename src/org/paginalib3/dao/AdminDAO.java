package org.paginalib3.dao;

import java.sql.SQLException;
import org.paginalib3.model.DashboardIndicadores;

public interface AdminDAO {
    DashboardIndicadores obtenerIndicadores() throws SQLException;
}
 