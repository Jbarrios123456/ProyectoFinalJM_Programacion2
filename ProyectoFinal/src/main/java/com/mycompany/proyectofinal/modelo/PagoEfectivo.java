package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;

public class PagoEfectivo extends Pago {

    private final BigDecimal montoRecibido;

    public PagoEfectivo(BigDecimal monto, BigDecimal montoRecibido) {
        super(monto);
        this.montoRecibido = montoRecibido;
    }

    @Override
    public String getMetodo() {
        return "EFECTIVO";
    }

    @Override
    public void procesar() {
        if (montoRecibido == null || montoRecibido.compareTo(getMonto()) < 0) {
            throw new IllegalArgumentException("Monto insuficiente: debe recibir al menos Q" + getMonto());
        }
        setCambio(montoRecibido.subtract(getMonto()));
        setAprobado(true);
    }
}
