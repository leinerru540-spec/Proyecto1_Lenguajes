package com.spring.app.controller;

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
    public List<Solicitud> listar() {
        return solicitudRepository.findAll();
    }

    @GetMapping("/{id}")
    public Solicitud obtener(@PathVariable Long id) {
        return solicitudRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Solicitud crear(@RequestBody Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    @PutMapping("/{id}")
    public Solicitud actualizar(@PathVariable Long id, @RequestBody Solicitud solicitud) {
        solicitud.setId(id);
        return solicitudRepository.save(solicitud);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        solicitudRepository.deleteById(id);
    }
}

