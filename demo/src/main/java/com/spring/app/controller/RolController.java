package com.spring.app.controller;

import com.spring.app.dto.RolDTO;
import com.spring.app.entity.Rol;
import com.spring.app.repository.RolRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolController {

    private final RolRepository rolRepository;

    public RolController(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @GetMapping
    public List<RolDTO> listar() {
        return rolRepository.findAll().stream()
                .map(r -> new RolDTO(r.getId(), r.getNombre(), r.getDescripcion()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> obtener(@PathVariable Long id) {
        return rolRepository.findById(id)
                .map(r -> ResponseEntity.ok(new RolDTO(r.getId(), r.getNombre(), r.getDescripcion())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RolDTO> crear(@RequestBody RolDTO rolDTO) {
        Rol rol = new Rol();
        rol.setNombre(rolDTO.getNombre());
        rol.setDescripcion(rolDTO.getDescripcion());

        Rol saved = rolRepository.save(rol);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RolDTO(saved.getId(), saved.getNombre(), saved.getDescripcion()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> actualizar(@PathVariable Long id, @RequestBody RolDTO rolDTO) {
        if (!rolRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Rol rol = new Rol();
        rol.setId(id);
        rol.setNombre(rolDTO.getNombre());
        rol.setDescripcion(rolDTO.getDescripcion());

        Rol updated = rolRepository.save(rol);
        return ResponseEntity.ok(new RolDTO(updated.getId(), updated.getNombre(), updated.getDescripcion()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!rolRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        rolRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


