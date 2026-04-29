package com.spring.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.entity.Consultoria;
import com.spring.app.repository.ConsultoriaRepository;
@Service
public class ConsultoriaService {
    
    private final ConsultoriaRepository consultoriaRepository;

    public ConsultoriaService(ConsultoriaRepository consultoriaRepository) {
        this.consultoriaRepository = consultoriaRepository;
    }

    public List<Consultoria> findAll() {
        return consultoriaRepository.findAll();
    }

    public Consultoria save(Consultoria consultoria) {
        return consultoriaRepository.save(consultoria);
    }

    public Optional<Consultoria> findById(Long id) {
        return consultoriaRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(Long id) {
        return consultoriaRepository.deleteConsultoriaById(id) > 0;
    }

    public Consultoria update(Long id, Consultoria consultoriaActualizada) {
        Consultoria consultoriaExistente = consultoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultoría no encontrada con id: " + id));

        consultoriaExistente.setTipo(consultoriaActualizada.getTipo());
        consultoriaExistente.setDescripcion(consultoriaActualizada.getDescripcion());

        return consultoriaRepository.save(consultoriaExistente);
    }
}
