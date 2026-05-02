package com.spring.app.controller;

import com.spring.app.dto.ConsultoriaForm;
import com.spring.app.dto.ConsultoriaDTO;
import com.spring.app.entity.Consultoria;
import com.spring.app.repository.ConsultoriaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultorias")
public class ConsultoriaController {

    private final ConsultoriaRepository consultoriaRepository;

    public ConsultoriaController(ConsultoriaRepository consultoriaRepository) {
        this.consultoriaRepository = consultoriaRepository;
    }

    @GetMapping
    public List<ConsultoriaDTO> listar() {
        return consultoriaRepository.findAll().stream()
                .map(c -> new ConsultoriaDTO(
                        c.getId(),
                        c.getTipo(),
                        c.getDescripcion()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultoriaDTO> obtener(@PathVariable Long id) {
        return consultoriaRepository.findById(id)
                .map(c -> ResponseEntity.ok(new ConsultoriaDTO(
                        c.getId(),
                        c.getTipo(),
                        c.getDescripcion()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ConsultoriaDTO> crear(@RequestBody ConsultoriaForm consultoriaForm) {
        Consultoria consultoria = mapToEntity(consultoriaForm);
        Consultoria saved = consultoriaRepository.save(consultoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ConsultoriaDTO(
                saved.getId(),
                saved.getTipo(),
                saved.getDescripcion()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultoriaDTO> actualizar(@PathVariable Long id, @RequestBody ConsultoriaForm consultoriaForm) {
        if (!consultoriaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Consultoria consultoria = mapToEntity(consultoriaForm);
        consultoria.setId(id);
        Consultoria updated = consultoriaRepository.save(consultoria);
        return ResponseEntity.ok(new ConsultoriaDTO(
                updated.getId(),
                updated.getTipo(),
                updated.getDescripcion()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!consultoriaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        consultoriaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Consultoria mapToEntity(ConsultoriaForm consultoriaForm) {
        Consultoria consultoria = new Consultoria();
        consultoria.setTipo(consultoriaForm.getTipo());
        consultoria.setDescripcion(consultoriaForm.getDescripcion());
        return consultoria;
    }
}


