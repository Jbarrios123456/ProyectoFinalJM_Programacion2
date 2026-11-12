package com.mycompany.proyectofinal;

import com.mycompany.proyectofinal.dao.clienteDAO;
import com.mycompany.proyectofinal.modelo.Cliente;
import java.math.BigDecimal;
import java.sql.SQLException;

public class Pruebacliente {

    public static void main(String[] args) {
        clienteDAO dao = new clienteDAO();
        try {
            System.out.println("1. LISTAR");
            for (Cliente c : dao.listar()) {
                System.out.println(c.getIdCliente() + " | " + c.getNombre() + " | " + c.getTelefono() + " | " + c.getEstado() + " | Q" + c.getSaldoPendiente());
            }

            int id = 9999;
            if (dao.existe(id)) {
                System.out.println("El cliente de prueba " + id + " ya existe, no se inserta de nuevo");
            } else {
                System.out.println("2. INSERTAR");
                dao.insertar(new Cliente(id, "Cliente Prueba", "55500000", "ACTIVO", BigDecimal.ZERO));
                System.out.println("Cliente " + id + " insertado");
            }

            System.out.println("3. BUSCAR");
            Cliente buscado = dao.buscarPorId(id);
            System.out.println(buscado.getIdCliente() + " | " + buscado.getNombre() + " | " + buscado.getEstado());

            System.out.println("4. ACTUALIZAR");
            buscado.setNombre("Cliente Prueba Editado");
            buscado.setTelefono("55511111");
            dao.actualizar(buscado);
            Cliente editado = dao.buscarPorId(id);
            System.out.println(editado.getNombre() + " | " + editado.getTelefono());

            System.out.println("5. DESACTIVAR");
            dao.cambiarEstado(id, "INACTIVO");
            System.out.println("Estado ahora: " + dao.buscarPorId(id).getEstado());

            System.out.println("6. ID DUPLICADO");
            try {
                dao.insertar(new Cliente(id, "Duplicado", "55522222", "ACTIVO", BigDecimal.ZERO));
                System.out.println("ERROR: debio rechazar el duplicado");
            } catch (SQLException e) {
                System.out.println("Correcto, MySQL rechazo el duplicado: " + e.getMessage());
            }

            System.out.println("PRUEBA TERMINADA");
        } catch (SQLException e) {
            System.err.println("FALLO LA PRUEBA");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Codigo de error: " + e.getErrorCode());
        }
    }
}