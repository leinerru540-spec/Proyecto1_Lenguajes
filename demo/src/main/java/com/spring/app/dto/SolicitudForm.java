package com.spring.app.dto;

public class SolicitudForm {
    private String descripcion;
    private String estado;
    private String fecha;
    private Long consultoriaId;
    private Long usuarioId;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Long getConsultoriaId() {
        return consultoriaId;
    }

    public void setConsultoriaId(Long consultoriaId) {
        this.consultoriaId = consultoriaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
