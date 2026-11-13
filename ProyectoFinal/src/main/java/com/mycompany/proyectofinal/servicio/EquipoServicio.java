package com.mycompany.proyectofinal.servicio;

import com.mycompany.proyectofinal.dao.EquipoDAO;
import com.mycompany.proyectofinal.modelo.BicicletaMontana;
import com.mycompany.proyectofinal.modelo.BicicletaUrbana;
import com.mycompany.proyectofinal.modelo.Equipo;
import com.mycompany.proyectofinal.modelo.Scooter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class EquipoServicio {

    private final EquipoDAO dao = new EquipoDAO();

    public List<Equipo> listar() throws SQLException {
        return dao.listar();
    }

    public Equipo buscar(int codigo) throws SQLException {
        return dao.buscarPorId(codigo);
    }

    public void registrar(String tipo, Integer codigo, String modelo, BigDecimal tarifa, BigDecimal reposicion, Integer bateria) throws SQLException {
        if (codigo == null || codigo <= 0) {
            throw new IllegalArgumentException("El codigo debe ser un numero positivo");
        }
        if (dao.existe(codigo)) {
            throw new IllegalArgumentException("Ya existe un equipo con el codigo " + codigo);
        }
        dao.insertar(construir(tipo, codigo, modelo, tarifa, reposicion, bateria));
    }

    public void actualizar(String tipo, Integer codigo, String modelo, BigDecimal tarifa, BigDecimal reposicion, Integer bateria) throws SQLException {
        if (codigo == null || !dao.existe(codigo)) {
            throw new IllegalArgumentException("El equipo no existe");
        }
        dao.actualizar(construir(tipo, codigo, modelo, tarifa, reposicion, bateria));
    }

    public void darDeBaja(int codigo) throws SQLException {
        Equipo equipo = dao.buscarPorId(codigo);
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo no existe");
        }
        if ("RESERVADO".equals(equipo.getEstado()) || "ALQUILADO".equals(equipo.getEstado())) {
            throw new IllegalArgumentException("No se puede dar de baja un equipo reservado o alquilado");
        }
        dao.cambiarEstado(codigo, "DADO_DE_BAJA");
    }

    private Equipo construir(String tipo, int codigo, String modelo, BigDecimal tarifa, BigDecimal reposicion, Integer bateria) {
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new IllegalArgumentException("El modelo es obligatorio");
        }
        if (tarifa == null || tarifa.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La tarifa por hora debe ser mayor a cero");
        }
        if (reposicion == null || reposicion.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de reposicion debe ser mayor a cero");
        }
        Equipo equipo;
        if ("BICICLETA_URBANA".equals(tipo)) {
            equipo = new BicicletaUrbana();
        } else if ("BICICLETA_MONTANA".equals(tipo)) {
            equipo = new BicicletaMontana();
        } else if ("SCOOTER".equals(tipo)) {
            if (bateria == null || bateria < 0 || bateria > 100) {
                throw new IllegalArgumentException("La bateria del scooter debe estar entre 0 y 100");
            }
            Scooter scooter = new Scooter();
            scooter.setBateria(bateria);
            equipo = scooter;
        } else {
            throw new IllegalArgumentException("Tipo de equipo invalido");
        }
        equipo.setCodigo(codigo);
        equipo.setModelo(modelo.trim());
        equipo.setTarifaHora(tarifa);
        equipo.setPrecioReposicion(reposicion);
        return equipo;
    }
}
