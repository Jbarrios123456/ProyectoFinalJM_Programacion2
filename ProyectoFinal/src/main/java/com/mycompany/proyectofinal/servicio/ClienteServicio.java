package com.mycompany.proyectofinal.servicio;

import com.mycompany.proyectofinal.dao.clienteDAO;
import com.mycompany.proyectofinal.modelo.Cliente;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ClienteServicio {

    private final clienteDAO dao = new clienteDAO();

    public List<Cliente> listar() throws SQLException {
        return dao.listar();
    }

    public Cliente buscar(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public void registrar(Cliente c) throws SQLException {
        validar(c);
        if (c.getIdCliente() == null || c.getIdCliente() <= 0) {
            throw new IllegalArgumentException("El identificador debe ser un numero positivo");
        }
        if (dao.existe(c.getIdCliente())) {
            throw new IllegalArgumentException("Ya existe un cliente con el identificador " + c.getIdCliente());
        }
        c.setEstado("ACTIVO");
        c.setSaldoPendiente(BigDecimal.ZERO);
        dao.insertar(c);
    }

    public void actualizar(Cliente c) throws SQLException {
        validar(c);
        if (c.getIdCliente() == null || !dao.existe(c.getIdCliente())) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        dao.actualizar(c);
    }

    public void desactivar(int id) throws SQLException {
        if (!dao.existe(id)) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        dao.cambiarEstado(id, "INACTIVO");
    }

    public void activar(int id) throws SQLException {
        if (!dao.existe(id)) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        dao.cambiarEstado(id, "ACTIVO");
    }

    private void validar(Cliente c) {
        if (c.getNombre() == null || c.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (c.getTelefono() == null || c.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("El telefono es obligatorio");
        }
    }
}
