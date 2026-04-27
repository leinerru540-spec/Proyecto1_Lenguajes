package com.spring.app.Controller;

import com.spring.app.dto.UsuarioDTO;
import com.spring.app.entity.Usuario;
import com.spring.app.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

   @GetMapping
public List<UsuarioDTO> listar() {
    return usuarioRepository.findAll().stream()
        .map(u -> new UsuarioDTO(
            u.getId(),
            u.getEmail(),
            u.getNombre(),
            u.getRol().getNombre().name()
        ))
        .toList();
}

@GetMapping("/{id}")
public UsuarioDTO obtener(@PathVariable Long id) {
    return usuarioRepository.findById(id)
        .map(u -> new UsuarioDTO(
            u.getId(),
            u.getEmail(),
            u.getNombre(),
            u.getRol().getNombre().name()
        ))
        .orElse(null);
}


    @PostMapping
    public Usuario crear(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        usuario.setId(id);
        return usuarioRepository.save(usuario);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
    }
}

