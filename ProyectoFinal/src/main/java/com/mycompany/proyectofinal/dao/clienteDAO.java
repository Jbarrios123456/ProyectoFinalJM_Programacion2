package com.mycompany.proyectofinal.dao;

import com.mycompany.proyectofinal.config.Conexion;
import com.mycompany.proyectofinal.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class clienteDAO implements crudDAO<Cliente> {

    private static final String COLUMNAS = "id_cliente, nombre, telefono, estado, saldo_pendiente";

    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM cliente ORDER BY id_cliente";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM cliente WHERE id_cliente = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public boolean existe(int id) throws SQLException {
        return buscarPorId(id) != null;
    }

    public void insertar(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (id_cliente, nombre, telefono, estado, saldo_pendiente) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCliente());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEstado());
            ps.setBigDecimal(5, c.getSaldoPendiente());
            ps.executeUpdate();
        }
    }

    public void actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nombre = ?, telefono = ? WHERE id_cliente = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setInt(3, c.getIdCliente());
            ps.executeUpdate();
        }
    }

    public void cambiarEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE cliente SET estado = ? WHERE id_cliente = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nombre"),
                rs.getString("telefono"),
                rs.getString("estado"),
                rs.getBigDecimal("saldo_pendiente"));
    }
}
