package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Alquiler {

    public static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int idAlquiler;
    private Cliente cliente;
    private String estado;
    private LocalDateTime horaEstimada;
    private LocalDateTime horaEntrega;
    private LocalDateTime horaDevolucion;
    private BigDecimal deposito = BigDecimal.ZERO;
    private BigDecimal recargo = BigDecimal.ZERO;
    private BigDecimal reposicion = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal depositoDevuelto = BigDecimal.ZERO;
    private BigDecimal saldoGenerado = BigDecimal.ZERO;
    private final List<Equipo> equipos = new ArrayList<>();
    private final List<DetalleAccesorio> accesorios = new ArrayList<>();

    public int getIdAlquiler() {
        return idAlquiler;
    }

    public void setIdAlquiler(int idAlquiler) {
        this.idAlquiler = idAlquiler;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getHoraEstimada() {
        return horaEstimada;
    }

    public void setHoraEstimada(LocalDateTime horaEstimada) {
        this.horaEstimada = horaEstimada;
    }

    public LocalDateTime getHoraEntrega() {
        return horaEntrega;
    }

    public void setHoraEntrega(LocalDateTime horaEntrega) {
        this.horaEntrega = horaEntrega;
    }

    public LocalDateTime getHoraDevolucion() {
        return horaDevolucion;
    }

    public void setHoraDevolucion(LocalDateTime horaDevolucion) {
        this.horaDevolucion = horaDevolucion;
    }

    public BigDecimal getDeposito() {
        return deposito;
    }

    public void setDeposito(BigDecimal deposito) {
        this.deposito = deposito;
    }

    public BigDecimal getRecargo() {
        return recargo;
    }

    public void setRecargo(BigDecimal recargo) {
        this.recargo = recargo;
    }

    public BigDecimal getReposicion() {
        return reposicion;
    }

    public void setReposicion(BigDecimal reposicion) {
        this.reposicion = reposicion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
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

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public List<DetalleAccesorio> getAccesorios() {
        return accesorios;
    }

    public String getHoraEstimadaTexto() {
        return formatear(horaEstimada);
    }

    public String getHoraEntregaTexto() {
        return formatear(horaEntrega);
    }

    public String getHoraDevolucionTexto() {
        return formatear(horaDevolucion);
    }

    public String getEquiposTexto() {
        if (equipos.isEmpty()) {
            return "-";
        }
        return equipos.stream().map(e -> e.getCodigo() + " " + e.getModelo()).collect(Collectors.joining(", "));
    }

    public String getAccesoriosTexto() {
        if (accesorios.isEmpty()) {
            return "-";
        }
        return accesorios.stream().map(d -> d.getAccesorio().getNombre() + " x" + d.getCantidad()).collect(Collectors.joining(", "));
    }

    private String formatear(LocalDateTime fecha) {
        return fecha == null ? "-" : fecha.format(FORMATO);
    }
}
