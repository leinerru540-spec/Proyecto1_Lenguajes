package com.spring.app.Controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.dto.SolicitudForm;
import com.spring.app.entity.Consultoria;
import com.spring.app.entity.EstadoSolicitud;
import com.spring.app.entity.Solicitud;
import com.spring.app.entity.Usuario;
import com.spring.app.service.ConsultoriaService;
import com.spring.app.service.SolicitudService;
import com.spring.app.service.UsuarioService;

@Controller
@RequestMapping("/vista/solicitudes")
public class SolicitudViewController {

    private final SolicitudService solicitudService;
    private final ConsultoriaService consultoriaService;
    private final UsuarioService usuarioService;

    public SolicitudViewController(
            SolicitudService solicitudService,
            ConsultoriaService consultoriaService,
            UsuarioService usuarioService) {
        this.solicitudService = solicitudService;
        this.consultoriaService = consultoriaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("solicitudes", solicitudService.findAll());
        return "solicitudes";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        cargarDatosFormulario(model);
        model.addAttribute("solicitudForm", new SolicitudForm());
        model.addAttribute("formTitle", "Nueva solicitud");
        model.addAttribute("formAction", "/vista/solicitudes/nueva");
        return "solicitud-form";
    }

    @PostMapping("/nueva")
    public String crear(@ModelAttribute SolicitudForm solicitudForm, RedirectAttributes redirectAttributes) {
        solicitudService.save(mapToEntity(solicitudForm));
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud creada correctamente.");
        return "redirect:/vista/solicitudes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Solicitud solicitud = solicitudService.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + id));

        SolicitudForm solicitudForm = new SolicitudForm();
        solicitudForm.setDescripcion(solicitud.getDescripcion());
        solicitudForm.setEstado(solicitud.getEstado().name());
        solicitudForm.setFecha(solicitud.getFecha().toString());
        solicitudForm.setConsultoriaId(solicitud.getConsultoria().getId());
        solicitudForm.setUsuarioId(solicitud.getUsuario().getId());

        cargarDatosFormulario(model);
        model.addAttribute("solicitudForm", solicitudForm);
        model.addAttribute("formTitle", "Editar solicitud");
        model.addAttribute("formAction", "/vista/solicitudes/editar/" + id);
        return "solicitud-form";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute SolicitudForm solicitudForm, RedirectAttributes redirectAttributes) {
        solicitudService.update(id, mapToEntity(solicitudForm));
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud actualizada correctamente.");
        return "redirect:/vista/solicitudes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        solicitudService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud eliminada correctamente.");
        return "redirect:/vista/solicitudes";
    }

    private void cargarDatosFormulario(Model model) {
        model.addAttribute("consultorias", consultoriaService.findAll());
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("estadosSolicitud", EstadoSolicitud.values());
    }

    private Solicitud mapToEntity(SolicitudForm solicitudForm) {
        Consultoria consultoria = consultoriaService.findById(solicitudForm.getConsultoriaId())
                .orElseThrow(() -> new RuntimeException("Consultoria no encontrada con id: " + solicitudForm.getConsultoriaId()));
        Usuario usuario = usuarioService.findById(solicitudForm.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + solicitudForm.getUsuarioId()));

        Solicitud solicitud = new Solicitud();
        solicitud.setDescripcion(solicitudForm.getDescripcion());
        solicitud.setEstado(EstadoSolicitud.valueOf(solicitudForm.getEstado()));
        solicitud.setFecha(LocalDate.parse(solicitudForm.getFecha()));
        solicitud.setConsultoria(consultoria);
        solicitud.setUsuario(usuario);
        return solicitud;
    }
}
