package com.spring.app.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.spring.app.entity.Usuario;
import com.spring.app.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    // Repositorio usado para buscar usuarios por email en la base de datos.
    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Spring Security llama este metodo durante el login. En este proyecto usamos email como username.
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Spring Security espera que los roles tengan el prefijo ROLE_.
        String rol = "ROLE_" + usuario.getRol().getNombre().name();

        // Se retorna un UserDetails con email, contrasena cifrada y rol del usuario.
        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(rol))
        );
    }
}
