package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Comprobante {

    private final Alquiler alquiler;
    private final List<LineaEquipo> lineas = new ArrayList<>();
    private String metodo;
    private BigDecimal montoPagado = BigDecimal.ZERO;
    private BigDecimal cambio = BigDecimal.ZERO;
    private String referencia;
    private LocalDateTime fechaPago;

    public Comprobante(Alquiler alquiler) {
        this.alquiler = alquiler;
    }

    public Alquiler getAlquiler() {
        return alquiler;
    }

    public List<LineaEquipo> getLineas() {
        return lineas;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public BigDecimal getCambio() {
        return cambio;
    }

    public void setCambio(BigDecimal cambio) {
        this.cambio = cambio;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getFechaPagoTexto() {
        return fechaPago == null ? "-" : fechaPago.format(Alquiler.FORMATO);
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getTotalHoras() {
        BigDecimal suma = BigDecimal.ZERO;
        for (LineaEquipo linea : lineas) {
            suma = suma.add(linea.getSubtotal());
        }
        return suma;
    }

    public BigDecimal getTotalAccesorios() {
        BigDecimal suma = BigDecimal.ZERO;
        for (DetalleAccesorio detalle : alquiler.getAccesorios()) {
            suma = suma.add(detalle.getSubtotal());
        }
        return suma;
    }

    public BigDecimal getDepositoAplicado() {
        return alquiler.getDeposito().subtract(alquiler.getDepositoDevuelto());
    }
}
