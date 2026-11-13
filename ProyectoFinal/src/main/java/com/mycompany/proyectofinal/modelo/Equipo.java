package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;

public abstract class Equipo {

    private int codigo;
    private String modelo;
    private BigDecimal tarifaHora;
    private BigDecimal precioReposicion;
    private String estado;
    private int alquileresDesdeMantenimiento;

    public Equipo() {
        this.estado = "DISPONIBLE";
    }

    public abstract String getTipo();

    public abstract BigDecimal getDeposito();

    public boolean puedeAlquilarse() {
        return "DISPONIBLE".equals(estado);
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public BigDecimal getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(BigDecimal tarifaHora) {
        this.tarifaHora = tarifaHora;
    }

    public BigDecimal getPrecioReposicion() {
        return precioReposicion;
    }

    public void setPrecioReposicion(BigDecimal precioReposicion) {
        this.precioReposicion = precioReposicion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getAlquileresDesdeMantenimiento() {
        return alquileresDesdeMantenimiento;
    }

    public void setAlquileresDesdeMantenimiento(int alquileresDesdeMantenimiento) {
        this.alquileresDesdeMantenimiento = alquileresDesdeMantenimiento;
    }
}
