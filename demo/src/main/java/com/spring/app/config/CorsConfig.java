package com.spring.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Permite que un frontend externo consuma la API del backend.
                registry.addMapping("/**")
                        // Origenes comunes para pruebas con frontend local.
                        .allowedOrigins(
                                "http://localhost:5500",
                                "http://127.0.0.1:5500",
                                "http://localhost:3000",
                                "http://localhost:5173"
                        )
                        // Metodos HTTP usados por la API REST.
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        // Encabezados permitidos, incluido Authorization para enviar el JWT.
                        .allowedHeaders("*")
                        // Permite que el navegador envie/reciba credenciales si se necesitan.
                        .allowCredentials(true);
            }
        };
    }
}

