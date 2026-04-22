package com.spring.app.dto;

public class LoginRequest {

    // Email usado como identificador del usuario al iniciar sesion.
    private String email;

    // Contrasena enviada por el usuario. Se compara contra la contrasena cifrada en BD.
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

