package com.mycompany.proyectofinal.modelo;

import java.math.BigDecimal;

public class Cliente {

    private Integer idCliente;
    private String nombre;
    private String telefono;
    private String estado;
    private BigDecimal saldoPendiente;

    public Cliente() {
        this.saldoPendiente = BigDecimal.ZERO;
    }

    public Cliente(Integer idCliente, String nombre, String telefono, String estado, BigDecimal saldoPendiente) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.telefono = telefono;
        this.estado = estado;
        this.saldoPendiente = saldoPendiente;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public boolean isActivo() {
        return "ACTIVO".equals(estado);
    }
}