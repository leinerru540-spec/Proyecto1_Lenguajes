package com.spring.app.Controller;

import com.spring.app.dto.ConsultoriaDTO;
import com.spring.app.entity.Consultoria;
import com.spring.app.repository.ConsultoriaRepository;
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
                        c.getEstado().name(),
                        c.getDescripcion(),
                        c.getCliente().getId()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ConsultoriaDTO obtener(@PathVariable Long id) {
        return consultoriaRepository.findById(id)
                .map(c -> new ConsultoriaDTO(
                        c.getId(),
                        c.getTipo(),
                        c.getEstado().name(),
                        c.getDescripcion(),
                        c.getCliente().getId()
                ))
                .orElse(null);
    }

    @PostMapping
    public ConsultoriaDTO crear(@RequestBody Consultoria consultoria) {
        Consultoria saved = consultoriaRepository.save(consultoria);
        return new ConsultoriaDTO(
                saved.getId(),
                saved.getTipo(),
                saved.getEstado().name(),
                saved.getDescripcion(),
                saved.getCliente().getId()
        );
    }

    @PutMapping("/{id}")
    public ConsultoriaDTO actualizar(@PathVariable Long id, @RequestBody Consultoria consultoria) {
        consultoria.setId(id);
        Consultoria updated = consultoriaRepository.save(consultoria);
        return new ConsultoriaDTO(
                updated.getId(),
                updated.getTipo(),
                updated.getEstado().name(),
                updated.getDescripcion(),
                updated.getCliente().getId()
        );
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        consultoriaRepository.deleteById(id);
    }
}


