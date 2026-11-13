package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;

public class BicicletaUrbana extends Equipo {

    @Override
    public String getTipo() {
        return "BICICLETA_URBANA";
    }

    @Override
    public BigDecimal getDeposito() {
        return new BigDecimal("100.00");
    }
}
