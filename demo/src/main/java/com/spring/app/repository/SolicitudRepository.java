package com.spring.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.entity.Solicitud;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByUsuario_EmailOrCorreoSolicitante(String emailUsuario, String correoSolicitante);
}
