package com.spring.app.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.spring.app.entity.Solicitud;
import com.spring.app.repository.SolicitudRepository;
@Service
public class SolicitudService {
    
    private final SolicitudRepository solicitudRepository;

    public SolicitudService(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }
    
    public List<Solicitud> findAll() {
        return solicitudRepository.findAll();
    }

    public List<Solicitud> findBySolicitante(String email) {
        return solicitudRepository.findByUsuario_EmailOrCorreoSolicitante(email, email);
    }

    public Solicitud save(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    public Optional<Solicitud> findById(Long id) {
        return solicitudRepository.findById(id);
    }

    public void deleteById(Long id) {
        solicitudRepository.deleteById(id);
    }

    public void detachUsuarioAndCliente(Long usuarioId, String correoCliente) {
        solicitudRepository.findAll().stream()
                .filter(solicitud -> referencesUsuarioOrCliente(solicitud, usuarioId, correoCliente))
                .forEach(solicitud -> {
                    if (solicitud.getUsuario() != null && Objects.equals(solicitud.getUsuario().getId(), usuarioId)) {
                        solicitud.setUsuario(null);
                    }

                    if (solicitud.getCliente() != null && Objects.equals(solicitud.getCliente().getCorreo(), correoCliente)) {
                        solicitud.setCliente(null);
                    }

                    solicitudRepository.save(solicitud);
                });
    }

    private boolean referencesUsuarioOrCliente(Solicitud solicitud, Long usuarioId, String correoCliente) {
        boolean sameUsuario = solicitud.getUsuario() != null
                && Objects.equals(solicitud.getUsuario().getId(), usuarioId);
        boolean sameCliente = solicitud.getCliente() != null
                && Objects.equals(solicitud.getCliente().getCorreo(), correoCliente);

        return sameUsuario || sameCliente;
    }

    public Solicitud update(Long id, Solicitud solicitudActualizada) {
        Solicitud solicitudExistente = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + id));

        solicitudExistente.setDescripcion(solicitudActualizada.getDescripcion());
        solicitudExistente.setNombreSolicitante(solicitudActualizada.getNombreSolicitante());
        solicitudExistente.setCorreoSolicitante(solicitudActualizada.getCorreoSolicitante());
        solicitudExistente.setEstado(solicitudActualizada.getEstado());
        solicitudExistente.setFecha(solicitudActualizada.getFecha());
        solicitudExistente.setCliente(solicitudActualizada.getCliente());
        solicitudExistente.setConsultoria(solicitudActualizada.getConsultoria());
        solicitudExistente.setUsuario(solicitudActualizada.getUsuario());

        return solicitudRepository.save(solicitudExistente);
    }
    
}
