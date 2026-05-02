package com.spring.app.controller;

import com.spring.app.dto.UsuarioForm;
import com.spring.app.dto.UsuarioDTO;
import com.spring.app.entity.Rol;
import com.spring.app.entity.Usuario;
import com.spring.app.repository.RolRepository;
import com.spring.app.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
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
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Long id) {
        return usuarioRepository.findById(id)
            .map(u -> ResponseEntity.ok(new UsuarioDTO(
                u.getId(),
                u.getEmail(),
                u.getNombre(),
                u.getRol().getNombre().name()
            )))
            .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@RequestBody UsuarioForm usuarioForm) {
        Usuario saved = usuarioRepository.save(mapToEntity(usuarioForm, true));
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioDTO(
                saved.getId(),
                saved.getEmail(),
                saved.getNombre(),
                saved.getRol().getNombre().name()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id, @RequestBody UsuarioForm usuarioForm) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        Rol rol = rolRepository.findById(usuarioForm.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + usuarioForm.getRolId()));

        usuarioExistente.setNombre(usuarioForm.getNombre());
        usuarioExistente.setEmail(usuarioForm.getEmail());
        usuarioExistente.setRol(rol);
        if (usuarioForm.getPassword() != null && !usuarioForm.getPassword().isBlank()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioForm.getPassword()));
        }

        Usuario updated = usuarioRepository.save(usuarioExistente);
        return ResponseEntity.ok(new UsuarioDTO(
                updated.getId(),
                updated.getEmail(),
                updated.getNombre(),
                updated.getRol().getNombre().name()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Usuario mapToEntity(UsuarioForm usuarioForm, boolean encodePassword) {
        Rol rol = rolRepository.findById(usuarioForm.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + usuarioForm.getRolId()));

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioForm.getNombre());
        usuario.setEmail(usuarioForm.getEmail());
        usuario.setPassword(encodePassword ? passwordEncoder.encode(usuarioForm.getPassword()) : usuarioForm.getPassword());
        usuario.setRol(rol);
        return usuario;
    }
}

