package com.spring.app.controller;

import com.spring.app.dto.SolicitudDTO;
import com.spring.app.entity.Solicitud;
import com.spring.app.repository.SolicitudRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudRepository solicitudRepository;

    public SolicitudController(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    @GetMapping
    public List<SolicitudDTO> listar() {
        return solicitudRepository.findAll().stream()
                .map(s -> new SolicitudDTO(
                        s.getId(),
                        s.getDescripcion(),
                        s.getEstado().name(),
                        s.getFecha().toString(),
                        s.getConsultoria().getId(),
                        s.getUsuario().getId()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public SolicitudDTO obtener(@PathVariable Long id) {
        return solicitudRepository.findById(id)
                .map(s -> new SolicitudDTO(
                        s.getId(),
                        s.getDescripcion(),
                        s.getEstado().name(),
                        s.getFecha().toString(),
                        s.getConsultoria().getId(),
                        s.getUsuario().getId()
                ))
                .orElse(null);
    }

    @PostMapping
    public SolicitudDTO crear(@RequestBody Solicitud solicitud) {
        Solicitud saved = solicitudRepository.save(solicitud);
        return new SolicitudDTO(
                saved.getId(),
                saved.getDescripcion(),
                saved.getEstado().name(),
                saved.getFecha().toString(),
                saved.getConsultoria().getId(),
                saved.getUsuario().getId()
        );
    }

    @PutMapping("/{id}")
    public SolicitudDTO actualizar(@PathVariable Long id, @RequestBody Solicitud solicitud) {
        solicitud.setId(id);
        Solicitud updated = solicitudRepository.save(solicitud);
        return new SolicitudDTO(
                updated.getId(),
                updated.getDescripcion(),
                updated.getEstado().name(),
                updated.getFecha().toString(),
                updated.getConsultoria().getId(),
                updated.getUsuario().getId()
        );
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        solicitudRepository.deleteById(id);
    }
}


