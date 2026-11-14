package com.mycompany.proyectofinal.dao;

import com.mycompany.proyectofinal.config.Conexion;
import com.mycompany.proyectofinal.modelo.Accesorio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccesorioDAO implements crudDAO<Accesorio> {

    private static final String COLUMNAS = "codigo, nombre, tarifa, precio_reposicion, existencia";

    @Override
    public List<Accesorio> listar() throws SQLException {
        List<Accesorio> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM accesorio ORDER BY codigo";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    @Override
    public Accesorio buscarPorId(int codigo) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM accesorio WHERE codigo = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public boolean existe(int codigo) throws SQLException {
        return buscarPorId(codigo) != null;
    }

    @Override
    public void insertar(Accesorio accesorio) throws SQLException {
        String sql = "INSERT INTO accesorio (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accesorio.getCodigo());
            ps.setString(2, accesorio.getNombre());
            ps.setBigDecimal(3, accesorio.getTarifa());
            ps.setBigDecimal(4, accesorio.getPrecioReposicion());
            ps.setInt(5, accesorio.getExistencia());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Accesorio accesorio) throws SQLException {
        String sql = "UPDATE accesorio SET nombre = ?, tarifa = ?, precio_reposicion = ? WHERE codigo = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accesorio.getNombre());
            ps.setBigDecimal(2, accesorio.getTarifa());
            ps.setBigDecimal(3, accesorio.getPrecioReposicion());
            ps.setInt(4, accesorio.getCodigo());
            ps.executeUpdate();
        }
    }

    private Accesorio mapear(ResultSet rs) throws SQLException {
        return new Accesorio(
                rs.getInt("codigo"),
                rs.getString("nombre"),
                rs.getBigDecimal("tarifa"),
                rs.getBigDecimal("precio_reposicion"),
                rs.getInt("existencia"));
    }
}
