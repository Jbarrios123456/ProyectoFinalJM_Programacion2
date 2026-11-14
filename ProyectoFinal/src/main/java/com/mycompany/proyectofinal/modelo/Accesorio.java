package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;

public class Accesorio {

    private int codigo;
    private String nombre;
    private BigDecimal tarifa;
    private BigDecimal precioReposicion;
    private int existencia;

    public Accesorio() {
    }

    public Accesorio(int codigo, String nombre, BigDecimal tarifa, BigDecimal precioReposicion, int existencia) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tarifa = tarifa;
        this.precioReposicion = precioReposicion;
        this.existencia = existencia;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getTarifa() {
        return tarifa;
    }

    public void setTarifa(BigDecimal tarifa) {
        this.tarifa = tarifa;
    }

    public BigDecimal getPrecioReposicion() {
        return precioReposicion;
    }

    public void setPrecioReposicion(BigDecimal precioReposicion) {
        this.precioReposicion = precioReposicion;
    }

    public int getExistencia() {
        return existencia;
    }

    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }
}
