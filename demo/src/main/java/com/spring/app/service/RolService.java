package com.spring.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.spring.app.entity.NombreRol;
import com.spring.app.entity.Rol;
import com.spring.app.repository.RolRepository;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public Optional<Rol> findById(Long id) {
        return rolRepository.findById(id);
    }

    public Optional<Rol> findByNombre(NombreRol nombre) {
        return rolRepository.findByNombre(nombre);
    }

    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    public Rol save(Rol rol) {
        return rolRepository.save(rol);
    }
}
