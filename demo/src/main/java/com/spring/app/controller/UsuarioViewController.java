package com.spring.app.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.dto.UsuarioForm;
import com.spring.app.entity.NombreRol;
import com.spring.app.entity.Rol;
import com.spring.app.entity.Usuario;
import com.spring.app.service.ClienteService;
import com.spring.app.service.RolService;
import com.spring.app.service.SolicitudService;
import com.spring.app.service.UsuarioService;

@Controller
@RequestMapping("/vista/usuarios")
public class UsuarioViewController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final ClienteService clienteService;
    private final SolicitudService solicitudService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioViewController(
            UsuarioService usuarioService,
            RolService rolService,
            ClienteService clienteService,
            SolicitudService solicitudService,
            PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.clienteService = clienteService;
        this.solicitudService = solicitudService;
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
        model.addAttribute("formTitle", "Crear usuario");
        model.addAttribute("formAction", "/vista/usuarios/nuevo");
        model.addAttribute("isEdit", false);
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

        Usuario saved = usuarioService.save(usuario);
        clienteService.ensureClienteForUsuario(saved);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario creado correctamente.");
        return "redirect:/vista/usuarios";
    }

    private void cargarDatosFormulario(Model model) {
        model.addAttribute("roles", rolService.findAll());
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        cargarDatosFormulario(model);
        UsuarioForm usuarioForm = new UsuarioForm();
        usuarioForm.setNombre(usuario.getNombre());
        usuarioForm.setEmail(usuario.getEmail());
        usuarioForm.setRolId(usuario.getRol().getId());
        model.addAttribute("usuarioForm", usuarioForm);
        model.addAttribute("formTitle", "Editar usuario");
        model.addAttribute("formAction", "/vista/usuarios/editar/" + id);
        model.addAttribute("isEdit", true);
        return "usuario-form";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(
            @PathVariable Long id,
            @ModelAttribute UsuarioForm usuarioForm,
            RedirectAttributes redirectAttributes,
            Authentication authentication,
            HttpServletResponse response) {
        Usuario usuarioExistente = usuarioService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        String previousEmail = usuarioExistente.getEmail();
        boolean wasCliente = usuarioExistente.getRol().getNombre() == NombreRol.CLIENTE;
        boolean isCurrentUser = authentication != null
                && authentication.getName() != null
                && authentication.getName().equalsIgnoreCase(previousEmail);

        Rol rol = rolService.findById(usuarioForm.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + usuarioForm.getRolId()));

        usuarioExistente.setNombre(usuarioForm.getNombre());
        usuarioExistente.setEmail(usuarioForm.getEmail());
        if (usuarioForm.getPassword() != null && !usuarioForm.getPassword().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioForm.getPassword()));
        }
        usuarioExistente.setRol(rol);

        Usuario updated = usuarioService.save(usuarioExistente);
        if (updated.getRol().getNombre() == NombreRol.CLIENTE) {
            clienteService.syncClienteForUsuario(updated, previousEmail);
        } else if (wasCliente) {
            solicitudService.detachUsuarioAndCliente(updated.getId(), previousEmail);
            clienteService.deleteByCorreo(previousEmail);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado correctamente.");

        if (isCurrentUser && requiresNewSession(updated, previousEmail)) {
            clearJwtCookie(response);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Tu cuenta fue actualizada. Inicia sesion nuevamente para continuar con los cambios aplicados.");
            return "redirect:/login";
        }

        return "redirect:/vista/usuarios";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        solicitudService.detachUsuarioAndCliente(usuario.getId(), usuario.getEmail());
        clienteService.deleteByCorreo(usuario.getEmail());
        usuarioService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario eliminado correctamente.");
        return "redirect:/vista/usuarios";
    }

    private boolean requiresNewSession(Usuario updated, String previousEmail) {
        return updated != null
                && (updated.getRol().getNombre() != NombreRol.ADMINISTRADOR
                || !updated.getEmail().equalsIgnoreCase(previousEmail));
    }

    private void clearJwtCookie(HttpServletResponse response) {
        ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
    }
}
