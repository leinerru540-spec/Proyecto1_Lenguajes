package com.spring.app.dto;

public class ConsultoriaDTO {
    private Long id;
    private String tipo;
    private String estado;
    private String descripcion;
    private Long clienteId;

    public ConsultoriaDTO(Long id, String tipo, String estado, String descripcion, Long clienteId) {
        this.id = id;
        this.tipo = tipo;
        this.estado = estado;
        this.descripcion = descripcion;
        this.clienteId = clienteId;
    }

    // Getters y setters
    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public String getEstado() { return estado; }
    public String getDescripcion() { return descripcion; }
    public Long getClienteId() { return clienteId; }
}
