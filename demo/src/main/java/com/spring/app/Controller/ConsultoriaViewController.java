package com.spring.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.dto.ConsultoriaForm;
import com.spring.app.entity.Consultoria;
import com.spring.app.service.ConsultoriaService;

@Controller
@RequestMapping({"/vista/consultorias", "/vista/consultoria"})
public class ConsultoriaViewController {

    private final ConsultoriaService consultoriaService;

    public ConsultoriaViewController(ConsultoriaService consultoriaService) {
        this.consultoriaService = consultoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("consultorias", consultoriaService.findAll());
        return "consultorias";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        cargarDatosFormulario(model);
        model.addAttribute("consultoriaForm", new ConsultoriaForm());
        model.addAttribute("formTitle", "Nueva consultoria");
        model.addAttribute("formAction", "/vista/consultorias/nueva");
        return "consultoria-form";
    }

    @PostMapping("/nueva")
    public String crear(@ModelAttribute ConsultoriaForm consultoriaForm, RedirectAttributes redirectAttributes) {
        consultoriaService.save(mapToEntity(consultoriaForm));
        redirectAttributes.addFlashAttribute("successMessage", "Consultoria creada correctamente.");
        return "redirect:/vista/consultorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Consultoria consultoria = consultoriaService.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultoria no encontrada con id: " + id));

        ConsultoriaForm consultoriaForm = new ConsultoriaForm();
        consultoriaForm.setTipo(consultoria.getTipo());
        consultoriaForm.setDescripcion(consultoria.getDescripcion());

        cargarDatosFormulario(model);
        model.addAttribute("consultoriaForm", consultoriaForm);
        model.addAttribute("formTitle", "Editar consultoria");
        model.addAttribute("formAction", "/vista/consultorias/editar/" + id);
        return "consultoria-form";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute ConsultoriaForm consultoriaForm, RedirectAttributes redirectAttributes) {
        consultoriaService.update(id, mapToEntity(consultoriaForm));
        redirectAttributes.addFlashAttribute("successMessage", "Consultoria actualizada correctamente.");
        return "redirect:/vista/consultorias";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean eliminada = consultoriaService.deleteById(id);
        if (eliminada) {
            redirectAttributes.addFlashAttribute("successMessage", "Consultoria eliminada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "No se encontro la consultoria indicada.");
        }
        return "redirect:/vista/consultorias";
    }

    private void cargarDatosFormulario(Model model) {
    }

    private Consultoria mapToEntity(ConsultoriaForm consultoriaForm) {
        Consultoria consultoria = new Consultoria();
        consultoria.setTipo(consultoriaForm.getTipo());
        consultoria.setDescripcion(consultoriaForm.getDescripcion());
        return consultoria;
    }
}
