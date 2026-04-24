package com.spring.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.spring.app.service.UsuarioDetailsService;

@Configuration
public class SecurityConfig {

    // Servicio que carga los usuarios reales desde la base de datos.
    private final UsuarioDetailsService usuarioDetailsService;

    // Filtro que valida el token JWT en cada solicitud.
    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(
            UsuarioDetailsService usuarioDetailsService,
            JwtRequestFilter jwtRequestFilter) {
        this.usuarioDetailsService = usuarioDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Se desactiva CSRF porque JWT se usa principalmente en APIs REST sin sesiones.
                .csrf(csrf -> csrf.disable())

                // Con JWT el servidor no guarda sesion. Cada request debe traer su token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Login y recursos publicos.
                        .requestMatchers("/auth/**", "/", "/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // El administrador tiene acceso completo a los modulos principales.
                        .requestMatchers("/usuarios/**", "/roles/**", "/clientes/**", "/consultorias/**")
                        .hasRole("ADMINISTRADOR")

                        // Los clientes pueden trabajar con solicitudes; el administrador tambien.
                        .requestMatchers("/solicitudes/**", "/vista/solicitudes/**")
                        .hasAnyRole("ADMINISTRADOR", "CLIENTE")

                        // Las vistas administrativas quedan solo para administrador.
                        .requestMatchers("/vista/clientes/**", "/vista/consultorias/**")
                        .hasRole("ADMINISTRADOR")

                        // Cualquier otra ruta requiere autenticacion.
                        .anyRequest().authenticated()
                )

                // Agrega nuestro filtro JWT antes del filtro normal de usuario/password de Spring.
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager() {
        // Este proveedor usa UsuarioDetailsService para buscar el usuario y BCrypt para validar la contrasena.
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(usuarioDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        // BCrypt guarda contrasenas cifradas, nunca en texto plano.
        return new BCryptPasswordEncoder();
    }
}
