package com.spring.app.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "consultorias")
public class Consultoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo;   // legal, ambiental, industrial

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @OneToMany(mappedBy = "consultoria")
    private List<Solicitud> solicitudes;
}

