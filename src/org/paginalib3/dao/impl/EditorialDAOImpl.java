package org.paginalib3.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.paginalib3.dao.EditorialDAO;
import org.paginalib3.model.Editorial;
import org.paginalib3.util.Conexion;

public class EditorialDAOImpl implements EditorialDAO {


    @Override
    public List<Editorial> listar() throws SQLException {

        List<Editorial> lista = new ArrayList<>();

        String sql = """
                SELECT nit, nombre_editorial
                FROM editoriales
                WHERE activo = TRUE
                ORDER BY nombre_editorial
                """;


        try(
            Connection c = Conexion.getInstancia().conectar();
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ){


            while(rs.next()){

                Editorial editorial = new Editorial(
                        rs.getString("nit"),
                        rs.getString("nombre_editorial")
                );


                lista.add(editorial);

            }

        }


        return lista;
    }

}