package com.spring.app.controller;

import com.spring.app.dto.SolicitudDTO;
import com.spring.app.dto.SolicitudForm;
import com.spring.app.entity.Cliente;
import com.spring.app.entity.Consultoria;
import com.spring.app.entity.EstadoSolicitud;
import com.spring.app.entity.Solicitud;
import com.spring.app.entity.Usuario;
import com.spring.app.repository.ClienteRepository;
import com.spring.app.repository.ConsultoriaRepository;
import com.spring.app.repository.SolicitudRepository;
import com.spring.app.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudRepository solicitudRepository;
    private final ConsultoriaRepository consultoriaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public SolicitudController(
            SolicitudRepository solicitudRepository,
            ConsultoriaRepository consultoriaRepository,
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository) {
        this.solicitudRepository = solicitudRepository;
        this.consultoriaRepository = consultoriaRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<SolicitudDTO> listar() {
        List<Solicitud> solicitudes = isAdmin()
                ? solicitudRepository.findAll()
                : solicitudRepository.findByUsuario_EmailOrCorreoSolicitante(currentEmail(), currentEmail());

        return solicitudes.stream()
                .map(this::mapToDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudDTO> obtener(@PathVariable Long id) {
        return solicitudRepository.findById(id)
                .map(solicitud -> {
                    requireOwnerOrAdmin(solicitud);
                    return ResponseEntity.ok(mapToDto(solicitud));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SolicitudDTO> crear(@RequestBody SolicitudForm solicitudForm) {
        if (!isAdmin()) {
            solicitudForm.setEstado(EstadoSolicitud.PENDIENTE.name());
            solicitudForm.setFecha(LocalDate.now().toString());
            currentUsuario().ifPresent(usuario -> solicitudForm.setUsuarioId(usuario.getId()));
            clienteRepository.findByCorreo(currentEmail())
                    .ifPresent(cliente -> solicitudForm.setClienteId(cliente.getId()));
        }

        Solicitud saved = solicitudRepository.save(mapToEntity(solicitudForm));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitudDTO> actualizar(@PathVariable Long id, @RequestBody SolicitudForm solicitudForm) {
        requireAdmin();

        if (!solicitudRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Solicitud solicitud = mapToEntity(solicitudForm);
        solicitud.setId(id);
        Solicitud updated = solicitudRepository.save(solicitud);
        return ResponseEntity.ok(mapToDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        requireAdmin();

        if (!solicitudRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        solicitudRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Solicitud mapToEntity(SolicitudForm solicitudForm) {
        Consultoria consultoria = consultoriaRepository.findById(solicitudForm.getConsultoriaId())
                .orElseThrow(() -> new RuntimeException("Consultoria no encontrada con id: " + solicitudForm.getConsultoriaId()));

        Solicitud solicitud = new Solicitud();
        solicitud.setDescripcion(solicitudForm.getDescripcion());
        solicitud.setEstado(EstadoSolicitud.valueOf(solicitudForm.getEstado()));
        solicitud.setFecha(LocalDate.parse(solicitudForm.getFecha()));
        solicitud.setConsultoria(consultoria);

        if (solicitudForm.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(solicitudForm.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + solicitudForm.getClienteId()));
            solicitud.setCliente(cliente);
            solicitud.setNombreSolicitante(cliente.getNombre());
            solicitud.setCorreoSolicitante(cliente.getCorreo());
        } else {
            solicitud.setNombreSolicitante(solicitudForm.getNombreSolicitante());
            solicitud.setCorreoSolicitante(solicitudForm.getCorreoSolicitante());
        }

        if (solicitudForm.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(solicitudForm.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + solicitudForm.getUsuarioId()));
            solicitud.setUsuario(usuario);
        }

        return solicitud;
    }

    private SolicitudDTO mapToDto(Solicitud solicitud) {
        return new SolicitudDTO(
                solicitud.getId(),
                solicitud.getNombreSolicitante(),
                solicitud.getCorreoSolicitante(),
                solicitud.getDescripcion(),
                solicitud.getEstado().name(),
                solicitud.getFecha().toString(),
                solicitud.getCliente() != null ? solicitud.getCliente().getId() : null,
                solicitud.getConsultoria().getId(),
                solicitud.getUsuario() != null ? solicitud.getUsuario().getId() : null
        );
    }

    private java.util.Optional<Usuario> currentUsuario() {
        return usuarioRepository.findByEmail(currentEmail());
    }

    private String currentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMINISTRADOR".equals(authority.getAuthority()));
    }

    private void requireAdmin() {
        if (!isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private void requireOwnerOrAdmin(Solicitud solicitud) {
        if (isAdmin()) {
            return;
        }

        String email = currentEmail();
        boolean ownsByUser = solicitud.getUsuario() != null && email.equals(solicitud.getUsuario().getEmail());
        boolean ownsByEmail = email.equals(solicitud.getCorreoSolicitante());
        if (!ownsByUser && !ownsByEmail) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
