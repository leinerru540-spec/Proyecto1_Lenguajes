package com.spring.app.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.dto.UsuarioForm;
import com.spring.app.entity.Rol;
import com.spring.app.entity.Usuario;
import com.spring.app.service.RolService;
import com.spring.app.service.UsuarioService;

@Controller
@RequestMapping("/vista/usuarios")
public class UsuarioViewController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioViewController(
            UsuarioService usuarioService,
            RolService rolService,
            PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuarios";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        cargarDatosFormulario(model);
        model.addAttribute("usuarioForm", new UsuarioForm());
        return "usuario-form";
    }

    @PostMapping("/nuevo")
    public String crear(@ModelAttribute UsuarioForm usuarioForm, RedirectAttributes redirectAttributes) {
        Rol rol = rolService.findById(usuarioForm.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + usuarioForm.getRolId()));

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioForm.getNombre());
        usuario.setEmail(usuarioForm.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuarioForm.getPassword()));
        usuario.setRol(rol);

        usuarioService.save(usuario);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario creado correctamente.");
        return "redirect:/vista/usuarios";
    }

    private void cargarDatosFormulario(Model model) {
        model.addAttribute("roles", rolService.findAll());
    }
}
