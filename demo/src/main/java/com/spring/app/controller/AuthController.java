package com.spring.app.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.spring.app.dto.AuthResponse;
import com.spring.app.dto.LoginRequest;
import com.spring.app.security.JwtUtil;
import com.spring.app.service.UsuarioDetailsService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // AuthenticationManager ejecuta el proceso de autenticacion de Spring Security.
    // Usa UsuarioDetailsService para buscar el usuario y PasswordEncoder para comparar la contrasena.
    private final AuthenticationManager authenticationManager;

    // Servicio propio del proyecto que carga usuarios desde la tabla usuarios usando el email.
    private final UsuarioDetailsService usuarioDetailsService;

    // Clase utilitaria encargada de generar y validar tokens JWT.
    private final JwtUtil jwtUtil;

    public AuthController(
            AuthenticationManager authenticationManager,
            UsuarioDetailsService usuarioDetailsService,
            JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.usuarioDetailsService = usuarioDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        // Endpoint de login:
        // Recibe un JSON con email y password, por ejemplo:
        // { "email": "admin@demo.com", "password": "admin123" }

        // Este objeto representa las credenciales enviadas por el usuario.
        // Spring Security lo usa para intentar autenticar la solicitud.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Si las credenciales son incorrectas, Spring lanza una excepcion y no se genera token.
        // Si llegan hasta aqui, significa que el email y la contrasena son validos.

        // Se vuelve a cargar el usuario para obtener sus datos y roles como UserDetails.
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(request.getEmail());

        // Con los datos del usuario autenticado se genera el token JWT.
        // El token llevara el email como subject y una fecha de expiracion.
        String token = jwtUtil.generateToken(userDetails);

        // El cliente debe enviar este token en las siguientes solicitudes:
        // Authorization: Bearer <token>
        return new AuthResponse(token);
    }
}

