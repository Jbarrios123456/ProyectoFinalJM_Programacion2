package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;

public class LineaEquipo {

    private static final BigDecimal FACTOR_RECARGO = new BigDecimal("1.5");

    private final Equipo equipo;
    private final int horas;
    private final int horasRetraso;
    private final boolean danado;

    public LineaEquipo(Equipo equipo, int horas, int horasRetraso, boolean danado) {
        this.equipo = equipo;
        this.horas = horas;
        this.horasRetraso = horasRetraso;
        this.danado = danado;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public int getHoras() {
        return horas;
    }

    public int getHorasRetraso() {
        return horasRetraso;
    }

    public boolean isDanado() {
        return danado;
    }

    public String getEstadoDevolucion() {
        return danado ? "DANADO" : "BUENO";
    }

    public BigDecimal getSubtotal() {
        return equipo.getTarifaHora().multiply(BigDecimal.valueOf(horas));
    }

    public BigDecimal getRecargo() {
        return equipo.getTarifaHora().multiply(BigDecimal.valueOf(horasRetraso)).multiply(FACTOR_RECARGO);
    }

    public BigDecimal getReposicion() {
        return danado ? equipo.getPrecioReposicion() : BigDecimal.ZERO;
    }
}
