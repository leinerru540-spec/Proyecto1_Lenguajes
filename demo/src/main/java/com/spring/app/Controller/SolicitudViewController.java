package com.spring.app.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.dto.SolicitudForm;
import com.spring.app.entity.Cliente;
import com.spring.app.entity.Consultoria;
import com.spring.app.entity.EstadoSolicitud;
import com.spring.app.entity.Solicitud;
import com.spring.app.entity.Usuario;
import com.spring.app.service.ClienteService;
import com.spring.app.service.ConsultoriaService;
import com.spring.app.service.SolicitudService;
import com.spring.app.service.UsuarioService;

@Controller
@RequestMapping("/vista/solicitudes")
public class SolicitudViewController {

    private final SolicitudService solicitudService;
    private final ConsultoriaService consultoriaService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    public SolicitudViewController(
            SolicitudService solicitudService,
            ConsultoriaService consultoriaService,
            ClienteService clienteService,
            UsuarioService usuarioService) {
        this.solicitudService = solicitudService;
        this.consultoriaService = consultoriaService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        boolean admin = isAdmin();
        model.addAttribute("isAdmin", admin);
        model.addAttribute("solicitudes", admin
                ? solicitudService.findAll()
                : solicitudService.findBySolicitante(currentEmail()));
        return "solicitudes";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        cargarDatosFormulario(model);
        SolicitudForm solicitudForm = new SolicitudForm();
        solicitudForm.setEstado(EstadoSolicitud.PENDIENTE.name());
        solicitudForm.setFecha(LocalDate.now().toString());

        currentUsuario().ifPresent(usuario -> {
            solicitudForm.setNombreSolicitante(usuario.getNombre());
            solicitudForm.setCorreoSolicitante(usuario.getEmail());
            solicitudForm.setUsuarioId(usuario.getId());
            clienteService.findByCorreo(usuario.getEmail())
                    .ifPresent(cliente -> solicitudForm.setClienteId(cliente.getId()));
        });

        model.addAttribute("solicitudForm", solicitudForm);
        model.addAttribute("formTitle", "Nueva solicitud");
        model.addAttribute("formAction", "/vista/solicitudes/nueva");
        model.addAttribute("isEdit", false);
        return "solicitud-form";
    }

    @PostMapping("/nueva")
    public String crear(@ModelAttribute SolicitudForm solicitudForm, RedirectAttributes redirectAttributes) {
        solicitudForm.setEstado(EstadoSolicitud.PENDIENTE.name());
        solicitudForm.setFecha(LocalDate.now().toString());
        currentUsuario().ifPresent(usuario -> solicitudForm.setUsuarioId(usuario.getId()));

        solicitudService.save(mapToEntity(solicitudForm));
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud creada correctamente.");
        return "redirect:/vista/solicitudes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        requireAdmin();

        Solicitud solicitud = solicitudService.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + id));

        SolicitudForm solicitudForm = new SolicitudForm();
        solicitudForm.setNombreSolicitante(solicitud.getNombreSolicitante());
        solicitudForm.setCorreoSolicitante(solicitud.getCorreoSolicitante());
        solicitudForm.setDescripcion(solicitud.getDescripcion());
        solicitudForm.setEstado(solicitud.getEstado().name());
        solicitudForm.setFecha(solicitud.getFecha().toString());
        if (solicitud.getCliente() != null) {
            solicitudForm.setClienteId(solicitud.getCliente().getId());
        }
        solicitudForm.setConsultoriaId(solicitud.getConsultoria().getId());
        if (solicitud.getUsuario() != null) {
            solicitudForm.setUsuarioId(solicitud.getUsuario().getId());
        }

        cargarDatosFormulario(model);
        model.addAttribute("solicitudForm", solicitudForm);
        model.addAttribute("formTitle", "Gestionar solicitud");
        model.addAttribute("formAction", "/vista/solicitudes/editar/" + id);
        model.addAttribute("isEdit", true);
        return "solicitud-form";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute SolicitudForm solicitudForm, RedirectAttributes redirectAttributes) {
        requireAdmin();

        solicitudService.update(id, mapToEntity(solicitudForm));
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud actualizada correctamente.");
        return "redirect:/vista/solicitudes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        requireAdmin();

        solicitudService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud eliminada correctamente.");
        return "redirect:/vista/solicitudes";
    }

    private void cargarDatosFormulario(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("consultorias", consultoriaService.findAll());
        model.addAttribute("estadosSolicitud", EstadoSolicitud.values());
        model.addAttribute("isAdmin", isAdmin());
    }

    private Solicitud mapToEntity(SolicitudForm solicitudForm) {
        Consultoria consultoria = consultoriaService.findById(solicitudForm.getConsultoriaId())
                .orElseThrow(() -> new RuntimeException("Consultoria no encontrada con id: " + solicitudForm.getConsultoriaId()));

        Solicitud solicitud = new Solicitud();
        solicitud.setDescripcion(solicitudForm.getDescripcion());
        solicitud.setEstado(EstadoSolicitud.valueOf(solicitudForm.getEstado()));
        solicitud.setFecha(LocalDate.parse(solicitudForm.getFecha()));
        solicitud.setConsultoria(consultoria);

        if (solicitudForm.getClienteId() != null) {
            Cliente cliente = clienteService.findById(solicitudForm.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + solicitudForm.getClienteId()));
            solicitud.setCliente(cliente);
            solicitud.setNombreSolicitante(cliente.getNombre());
            solicitud.setCorreoSolicitante(cliente.getCorreo());
        } else {
            solicitud.setNombreSolicitante(solicitudForm.getNombreSolicitante());
            solicitud.setCorreoSolicitante(solicitudForm.getCorreoSolicitante());
        }

        if (solicitudForm.getUsuarioId() != null) {
            Usuario usuario = usuarioService.findById(solicitudForm.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + solicitudForm.getUsuarioId()));
            solicitud.setUsuario(usuario);
        }

        return solicitud;
    }

    private Optional<Usuario> currentUsuario() {
        String email = currentEmail();
        if (email == null) {
            return Optional.empty();
        }

        return usuarioService.findByEmail(email);
    }

    private String currentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return null;
        }

        return authentication.getName();
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
}
