package com.spring.app.dto;

public class ConsultoriaDTO {
    private Long id;
    private String tipo;
    private String descripcion;

    public ConsultoriaDTO(Long id, String tipo, String descripcion) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    // Getters y setters
    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
}
