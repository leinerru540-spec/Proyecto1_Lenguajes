package com.spring.app.Controller;

import com.spring.app.entity.Rol;
import com.spring.app.repository.RolRepository;
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
    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    @GetMapping("/{id}")
    public Rol obtener(@PathVariable Long id) {
        return rolRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Rol crear(@RequestBody Rol rol) {
        return rolRepository.save(rol);
    }

    @PutMapping("/{id}")
    public Rol actualizar(@PathVariable Long id, @RequestBody Rol rol) {
        rol.setId(id);
        return rolRepository.save(rol);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        rolRepository.deleteById(id);
    }
}

