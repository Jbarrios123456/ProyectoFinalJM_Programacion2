package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Liquidacion {

    private final Alquiler alquiler;
    private final LocalDateTime horaReal;
    private final List<LineaEquipo> lineas = new ArrayList<>();
    private final Map<Integer, String> estados = new LinkedHashMap<>();
    private final Map<Integer, Integer> extraviados = new LinkedHashMap<>();
    private long minutosRetraso;
    private BigDecimal totalHoras = BigDecimal.ZERO;
    private BigDecimal totalAccesorios = BigDecimal.ZERO;
    private BigDecimal totalRecargo = BigDecimal.ZERO;
    private BigDecimal totalReposicion = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal depositoAplicado = BigDecimal.ZERO;
    private BigDecimal depositoDevuelto = BigDecimal.ZERO;
    private BigDecimal saldoGenerado = BigDecimal.ZERO;
    private BigDecimal montoAPagar = BigDecimal.ZERO;

    public Liquidacion(Alquiler alquiler, LocalDateTime horaReal) {
        this.alquiler = alquiler;
        this.horaReal = horaReal;
    }

    public Alquiler getAlquiler() {
        return alquiler;
    }

    public LocalDateTime getHoraReal() {
        return horaReal;
    }

    public String getHoraRealTexto() {
        return horaReal.format(Alquiler.FORMATO);
    }

    public String getHoraRealIso() {
        return horaReal.toString();
    }

    public List<LineaEquipo> getLineas() {
        return lineas;
    }

    public Map<Integer, String> getEstados() {
        return estados;
    }

    public Map<Integer, Integer> getExtraviados() {
        return extraviados;
    }

    public long getMinutosRetraso() {
        return minutosRetraso;
    }

    public void setMinutosRetraso(long minutosRetraso) {
        this.minutosRetraso = minutosRetraso;
    }

    public BigDecimal getTotalHoras() {
        return totalHoras;
    }

    public void setTotalHoras(BigDecimal totalHoras) {
        this.totalHoras = totalHoras;
    }

    public BigDecimal getTotalAccesorios() {
        return totalAccesorios;
    }

    public void setTotalAccesorios(BigDecimal totalAccesorios) {
        this.totalAccesorios = totalAccesorios;
    }

    public BigDecimal getTotalRecargo() {
        return totalRecargo;
    }

    public void setTotalRecargo(BigDecimal totalRecargo) {
        this.totalRecargo = totalRecargo;
    }

    public BigDecimal getTotalReposicion() {
        return totalReposicion;
    }

    public void setTotalReposicion(BigDecimal totalReposicion) {
        this.totalReposicion = totalReposicion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getDepositoAplicado() {
        return depositoAplicado;
    }

    public void setDepositoAplicado(BigDecimal depositoAplicado) {
        this.depositoAplicado = depositoAplicado;
    }

    public BigDecimal getDepositoDevuelto() {
        return depositoDevuelto;
    }

    public void setDepositoDevuelto(BigDecimal depositoDevuelto) {
        this.depositoDevuelto = depositoDevuelto;
    }

    public BigDecimal getSaldoGenerado() {
        return saldoGenerado;
    }

    public void setSaldoGenerado(BigDecimal saldoGenerado) {
        this.saldoGenerado = saldoGenerado;
    }

    public BigDecimal getMontoAPagar() {
        return montoAPagar;
    }

    public void setMontoAPagar(BigDecimal montoAPagar) {
        this.montoAPagar = montoAPagar;
    }
}
