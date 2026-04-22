package com.spring.app.dto;

public class AuthResponse {

    // Token JWT generado despues de un login correcto.
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

