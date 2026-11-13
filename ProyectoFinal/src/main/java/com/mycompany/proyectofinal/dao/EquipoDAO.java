package com.mycompany.proyectofinal.dao;

import com.mycompany.proyectofinal.config.Conexion;
import com.mycompany.proyectofinal.modelo.BicicletaMontana;
import com.mycompany.proyectofinal.modelo.BicicletaUrbana;
import com.mycompany.proyectofinal.modelo.Equipo;
import com.mycompany.proyectofinal.modelo.Scooter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO implements crudDAO<Equipo> {

    private static final String COLUMNAS = "codigo, tipo, modelo, tarifa_hora, precio_reposicion, estado, bateria, alquileres_desde_mantenimiento";

    @Override
    public List<Equipo> listar() throws SQLException {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM equipo ORDER BY codigo";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Equipo> listarDisponiblesPorTipo(String tipo) throws SQLException {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM equipo WHERE tipo = ? AND estado = 'DISPONIBLE' ORDER BY codigo";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public Equipo buscarPorId(int codigo) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM equipo WHERE codigo = ?";
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
    public void insertar(Equipo equipo) throws SQLException {
        String sql = "INSERT INTO equipo (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, equipo.getCodigo());
            ps.setString(2, equipo.getTipo());
            ps.setString(3, equipo.getModelo());
            ps.setBigDecimal(4, equipo.getTarifaHora());
            ps.setBigDecimal(5, equipo.getPrecioReposicion());
            ps.setString(6, equipo.getEstado());
            if (equipo instanceof Scooter scooter) {
                ps.setInt(7, scooter.getBateria());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setInt(8, equipo.getAlquileresDesdeMantenimiento());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Equipo equipo) throws SQLException {
        String sql = "UPDATE equipo SET modelo = ?, tarifa_hora = ?, precio_reposicion = ?, bateria = ? WHERE codigo = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, equipo.getModelo());
            ps.setBigDecimal(2, equipo.getTarifaHora());
            ps.setBigDecimal(3, equipo.getPrecioReposicion());
            if (equipo instanceof Scooter scooter) {
                ps.setInt(4, scooter.getBateria());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, equipo.getCodigo());
            ps.executeUpdate();
        }
    }

    public void cambiarEstado(int codigo, String estado) throws SQLException {
        String sql = "UPDATE equipo SET estado = ? WHERE codigo = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, codigo);
            ps.executeUpdate();
        }
    }

    private Equipo mapear(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        Equipo equipo;
        switch (tipo) {
            case "BICICLETA_URBANA":
                equipo = new BicicletaUrbana();
                break;
            case "BICICLETA_MONTANA":
                equipo = new BicicletaMontana();
                break;
            case "SCOOTER":
                Scooter scooter = new Scooter();
                scooter.setBateria(rs.getInt("bateria"));
                equipo = scooter;
                break;
            default:
                throw new SQLException("Tipo de equipo desconocido: " + tipo);
        }
        equipo.setCodigo(rs.getInt("codigo"));
        equipo.setModelo(rs.getString("modelo"));
        equipo.setTarifaHora(rs.getBigDecimal("tarifa_hora"));
        equipo.setPrecioReposicion(rs.getBigDecimal("precio_reposicion"));
        equipo.setEstado(rs.getString("estado"));
        equipo.setAlquileresDesdeMantenimiento(rs.getInt("alquileres_desde_mantenimiento"));
        return equipo;
    }
}