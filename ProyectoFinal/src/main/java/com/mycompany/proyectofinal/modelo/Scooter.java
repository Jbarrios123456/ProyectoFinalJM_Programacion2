package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;

public class Scooter extends Equipo {

    public static final int BATERIA_MINIMA = 30;

    private int bateria;

    @Override
    public String getTipo() {
        return "SCOOTER";
    }

    @Override
    public BigDecimal getDeposito() {
        return new BigDecimal("200.00");
    }

    @Override
    public boolean puedeAlquilarse() {
        return super.puedeAlquilarse() && bateria >= BATERIA_MINIMA;
    }

    public int getBateria() {
        return bateria;
    }

    public void setBateria(int bateria) {
        this.bateria = bateria;
    }
}
