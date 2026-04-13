package com.spring.app.controller;

import com.spring.app.dto.ClienteDTO;
import com.spring.app.entity.Cliente;
import com.spring.app.repository.ClienteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public List<ClienteDTO> listar() {
        return clienteRepository.findAll().stream()
                .map(c -> new ClienteDTO(
                        c.getId(),
                        c.getEmpresa(),
                        c.getNombre(),
                        c.getTelefono(),
                        c.getCorreo()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ClienteDTO obtener(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(c -> new ClienteDTO(
                        c.getId(),
                        c.getEmpresa(),
                        c.getNombre(),
                        c.getTelefono(),
                        c.getCorreo()
                ))
                .orElse(null);
    }

    @PostMapping
    public ClienteDTO crear(@RequestBody Cliente cliente) {
        Cliente saved = clienteRepository.save(cliente);
        return new ClienteDTO(
                saved.getId(),
                saved.getEmpresa(),
                saved.getNombre(),
                saved.getTelefono(),
                saved.getCorreo()
        );
    }

    @PutMapping("/{id}")
    public ClienteDTO actualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        cliente.setId(id);
        Cliente updated = clienteRepository.save(cliente);
        return new ClienteDTO(
                updated.getId(),
                updated.getEmpresa(),
                updated.getNombre(),
                updated.getTelefono(),
                updated.getCorreo()
        );
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        clienteRepository.deleteById(id);
    }
}


