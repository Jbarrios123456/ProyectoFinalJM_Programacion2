package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;

public class BicicletaMontana extends Equipo {

    @Override
    public String getTipo() {
        return "BICICLETA_MONTANA";
    }

    @Override
    public BigDecimal getDeposito() {
        return new BigDecimal("100.00");
    }
}
