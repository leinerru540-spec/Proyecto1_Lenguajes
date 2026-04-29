package com.spring.app.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.dto.UsuarioForm;
import com.spring.app.entity.NombreRol;
import com.spring.app.entity.Rol;
import com.spring.app.entity.Usuario;
import com.spring.app.service.RolService;
import com.spring.app.service.UsuarioService;

@Controller
public class RegistroController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;

    public RegistroController(
            UsuarioService usuarioService,
            RolService rolService,
            PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/registro")
    public String crearCliente(@ModelAttribute UsuarioForm usuarioForm, RedirectAttributes redirectAttributes) {
        Rol rolCliente = rolService.findByNombre(NombreRol.CLIENTE)
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado."));

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioForm.getNombre());
        usuario.setEmail(usuarioForm.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuarioForm.getPassword()));
        usuario.setRol(rolCliente);

        usuarioService.save(usuario);
        redirectAttributes.addFlashAttribute("successMessage", "Cuenta creada correctamente. Ya puedes iniciar sesion como cliente.");
        return "redirect:/login";
    }
}
