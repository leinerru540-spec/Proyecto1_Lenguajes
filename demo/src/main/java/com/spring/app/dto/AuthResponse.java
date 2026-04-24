package com.spring.app.dto;

public class AuthResponse {

    // Token JWT generado despues de un login correcto.
    private String token;
    
    // Rol principal del usuario autenticado.
    private String rol;

    public AuthResponse(String token, String rol) {
        this.token = token;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}

