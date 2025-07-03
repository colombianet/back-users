package com.inmohouse.backend.backend.dto;

import java.math.BigDecimal;

public class PropiedadRequest {
    private String titulo;
    private String descripcion;
    private String tipo;
    private BigDecimal precio;
    private String ubicacion;
    private String estado;

    private Long agenteId;

    public PropiedadRequest() {
    }

    public PropiedadRequest(String titulo, String descripcion, String tipo, BigDecimal precio,
            String ubicacion, String estado, Long agenteId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.precio = precio;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.agenteId = agenteId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getAgenteId() {
        return agenteId;
    }

    public void setAgenteId(Long agenteId) {
        this.agenteId = agenteId;
    }
}
