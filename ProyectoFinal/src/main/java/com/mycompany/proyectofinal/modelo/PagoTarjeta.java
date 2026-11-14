package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;

public class PagoTarjeta extends Pago {

    private final String numeroTarjeta;

    public PagoTarjeta(BigDecimal monto, String numeroTarjeta) {
        super(monto);
        this.numeroTarjeta = numeroTarjeta == null ? "" : numeroTarjeta.replace(" ", "");
    }

    @Override
    public String getMetodo() {
        return "TARJETA";
    }

    @Override
    public void procesar() {
        if (!numeroTarjeta.matches("\\d{16}")) {
            throw new IllegalArgumentException("El numero de tarjeta debe tener 16 digitos");
        }
        setReferencia("REF-" + System.currentTimeMillis());
        setAprobado(!numeroTarjeta.endsWith("0000"));
    }
}
