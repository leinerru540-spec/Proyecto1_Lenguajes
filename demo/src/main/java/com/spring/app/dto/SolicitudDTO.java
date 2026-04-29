package com.spring.app.dto;

public class SolicitudDTO {
    private Long id;
    private String nombreSolicitante;
    private String correoSolicitante;
    private String descripcion;
    private String estado;
    private String fecha;
    private Long clienteId;
    private Long consultoriaId;
    private Long usuarioId;

    public SolicitudDTO(
            Long id,
            String nombreSolicitante,
            String correoSolicitante,
            String descripcion,
            String estado,
            String fecha,
            Long clienteId,
            Long consultoriaId,
            Long usuarioId) {
        this.id = id;
        this.nombreSolicitante = nombreSolicitante;
        this.correoSolicitante = correoSolicitante;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fecha = fecha;
        this.clienteId = clienteId;
        this.consultoriaId = consultoriaId;
        this.usuarioId = usuarioId;
    }

    // Getters
    public Long getId() { return id; }
    public String getNombreSolicitante() { return nombreSolicitante; }
    public String getCorreoSolicitante() { return correoSolicitante; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
    public String getFecha() { return fecha; }
    public Long getClienteId() { return clienteId; }
    public Long getConsultoriaId() { return consultoriaId; }
    public Long getUsuarioId() { return usuarioId; }
}

