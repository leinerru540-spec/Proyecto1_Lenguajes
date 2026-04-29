package com.spring.app.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.spring.app.service.UsuarioDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    // Utilidad que permite extraer y validar datos del token JWT.
    private final JwtUtil jwtUtil;

    // Servicio que carga el usuario desde la base de datos usando el email.
    private final UsuarioDetailsService usuarioDetailsService;

    public JwtRequestFilter(JwtUtil jwtUtil, UsuarioDetailsService usuarioDetailsService) {
        this.jwtUtil = jwtUtil;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Se lee el encabezado Authorization enviado por el cliente.
        // El formato esperado es: Authorization: Bearer <token>
        String authorizationHeader = request.getHeader("Authorization");

        String token = null;
        String email = null;

        // Si el encabezado existe y empieza con "Bearer ", extraemos el token.
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        } else if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwtToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            email = jwtUtil.extractUsername(token);
        }

        // Si se logro extraer un email y todavia no hay usuario autenticado en esta peticion,
        // se valida el token y se registra la autenticacion en el contexto de Spring Security.
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = usuarioDetailsService.loadUserByUsername(email);

            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Agrega detalles de la solicitud actual, como IP y sesion.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Marca al usuario como autenticado para que Spring pueda aplicar reglas por rol.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continua con el resto de filtros y finalmente con el controlador correspondiente.
        filterChain.doFilter(request, response);
    }
}
