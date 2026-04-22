package com.spring.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring Data JPA crea automaticamente la consulta para buscar un usuario por email.
    Optional<Usuario> findByEmail(String email);
    
}
