package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;

public abstract class Pago {

    private final BigDecimal monto;
    private boolean aprobado;
    private BigDecimal cambio = BigDecimal.ZERO;
    private String referencia;

    protected Pago(BigDecimal monto) {
        this.monto = monto;
    }

    public abstract String getMetodo();

    public abstract void procesar();

    public BigDecimal getMonto() {
        return monto;
    }

    public boolean isAprobado() {
        return aprobado;
    }

    protected void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }

    public BigDecimal getCambio() {
        return cambio;
    }

    protected void setCambio(BigDecimal cambio) {
        this.cambio = cambio;
    }

    public String getReferencia() {
        return referencia;
    }

    protected void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
