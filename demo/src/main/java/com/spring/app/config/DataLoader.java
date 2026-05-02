package com.spring.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.spring.app.entity.NombreRol;
import com.spring.app.entity.Rol;
import com.spring.app.entity.Usuario;
import com.spring.app.repository.RolRepository;
import com.spring.app.repository.UsuarioRepository;

@Component
public class DataLoader implements CommandLineRunner {

    // Repositorios usados para consultar y guardar roles/usuarios al iniciar la aplicacion.
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    // Codificador usado para guardar contrasenas cifradas con BCrypt.
    private final PasswordEncoder passwordEncoder;

    public DataLoader(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Busca el rol ADMINISTRADOR. Si no existe, lo crea.
        Rol administrador = rolRepository.findByNombre(NombreRol.ADMINISTRADOR)
                .orElseGet(() -> {
                    Rol rol = new Rol();
                    rol.setNombre(NombreRol.ADMINISTRADOR);
                    rol.setDescripcion("Administrador del sistema");
                    return rolRepository.save(rol);
                });

        // Busca el rol CLIENTE. Si no existe, lo crea.
        Rol cliente = rolRepository.findByNombre(NombreRol.CLIENTE)
                .orElseGet(() -> {
                    Rol rol = new Rol();
                    rol.setNombre(NombreRol.CLIENTE);
                    rol.setDescripcion("Cliente del sistema");
                    return rolRepository.save(rol);
                });

        // Crea un usuario administrador de prueba si aun no existe.
        usuarioRepository.findByEmail("admin@demo.com")
                .ifPresentOrElse(usuario -> {
                    usuario.setRol(administrador);
                    if (!passwordEncoder.matches("admin123", usuario.getPassword())) {
                        usuario.setPassword(passwordEncoder.encode("admin123"));
                    }
                    usuarioRepository.save(usuario);
                }, () -> {
                    Usuario usuario = new Usuario();
                    usuario.setNombre("Administrador");
                    usuario.setEmail("admin@demo.com");
                    // La contrasena se guarda cifrada, no en texto plano.
                    usuario.setPassword(passwordEncoder.encode("admin123"));
                    usuario.setRol(administrador);
                    usuarioRepository.save(usuario);
                });

        // Crea un usuario cliente de prueba si aun no existe.
        usuarioRepository.findByEmail("cliente@demo.com")
                .ifPresentOrElse(usuario -> {
                    usuario.setRol(cliente);
                    if (!passwordEncoder.matches("cliente123", usuario.getPassword())) {
                        usuario.setPassword(passwordEncoder.encode("cliente123"));
                    }
                    usuarioRepository.save(usuario);
                }, () -> {
                    Usuario usuario = new Usuario();
                    usuario.setNombre("Cliente");
                    usuario.setEmail("cliente@demo.com");
                    // La contrasena se guarda cifrada, no en texto plano.
                    usuario.setPassword(passwordEncoder.encode("cliente123"));
                    usuario.setRol(cliente);
                    usuarioRepository.save(usuario);
                });
    }
}

