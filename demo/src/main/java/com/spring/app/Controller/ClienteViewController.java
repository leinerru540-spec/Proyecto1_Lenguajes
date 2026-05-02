package com.spring.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.dto.ClienteForm;
import com.spring.app.entity.Cliente;
import com.spring.app.service.ClienteService;

@Controller
@RequestMapping("/vista/clientes")
public class ClienteViewController {

    private final ClienteService clienteService;

    public ClienteViewController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        return "clientes";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("clienteForm", new ClienteForm());
        model.addAttribute("formTitle", "Nuevo cliente");
        model.addAttribute("formAction", "/vista/clientes/nuevo");
        return "cliente-form";
    }

    @PostMapping("/nuevo")
    public String crear(@ModelAttribute ClienteForm clienteForm, RedirectAttributes redirectAttributes) {
        Cliente cliente = new Cliente();
        cliente.setNombre(clienteForm.getNombre());
        cliente.setTelefono(clienteForm.getTelefono());
        cliente.setEmpresa(clienteForm.getEmpresa());
        cliente.setCorreo(clienteForm.getCorreo());
        clienteService.save(cliente);
        redirectAttributes.addFlashAttribute("successMessage", "Cliente creado correctamente.");
        return "redirect:/vista/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));

        ClienteForm clienteForm = new ClienteForm();
        clienteForm.setNombre(cliente.getNombre());
        clienteForm.setTelefono(cliente.getTelefono());
        clienteForm.setEmpresa(cliente.getEmpresa());
        clienteForm.setCorreo(cliente.getCorreo());

        model.addAttribute("clienteForm", clienteForm);
        model.addAttribute("formTitle", "Editar cliente");
        model.addAttribute("formAction", "/vista/clientes/editar/" + id);
        return "cliente-form";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute ClienteForm clienteForm, RedirectAttributes redirectAttributes) {
        Cliente cliente = new Cliente();
        cliente.setNombre(clienteForm.getNombre());
        cliente.setTelefono(clienteForm.getTelefono());
        cliente.setEmpresa(clienteForm.getEmpresa());
        cliente.setCorreo(clienteForm.getCorreo());
        clienteService.update(id, cliente);
        redirectAttributes.addFlashAttribute("successMessage", "Cliente actualizado correctamente.");
        return "redirect:/vista/clientes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clienteService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Cliente eliminado correctamente.");
        return "redirect:/vista/clientes";
    }
}
