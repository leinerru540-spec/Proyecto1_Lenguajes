package com.spring.app.controller;

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
    public List<Consultoria> listar() {
        return consultoriaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Consultoria obtener(@PathVariable Long id) {
        return consultoriaRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Consultoria crear(@RequestBody Consultoria consultoria) {
        return consultoriaRepository.save(consultoria);
    }

    @PutMapping("/{id}")
    public Consultoria actualizar(@PathVariable Long id, @RequestBody Consultoria consultoria) {
        consultoria.setId(id);
        return consultoriaRepository.save(consultoria);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        consultoriaRepository.deleteById(id);
    }
}

