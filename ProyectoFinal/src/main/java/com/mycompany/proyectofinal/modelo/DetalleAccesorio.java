package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;

public class DetalleAccesorio {

    private final Accesorio accesorio;
    private final int cantidad;
    private int extraviados;

    public DetalleAccesorio(Accesorio accesorio, int cantidad) {
        this.accesorio = accesorio;
        this.cantidad = cantidad;
    }

    public Accesorio getAccesorio() {
        return accesorio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getExtraviados() {
        return extraviados;
    }

    public void setExtraviados(int extraviados) {
        this.extraviados = extraviados;
    }

    public int getDevueltos() {
        return cantidad - extraviados;
    }

    public BigDecimal getSubtotal() {
        return accesorio.getTarifa().multiply(BigDecimal.valueOf(cantidad));
    }

    public BigDecimal getReposicion() {
        return accesorio.getPrecioReposicion().multiply(BigDecimal.valueOf(extraviados));
    }
}
