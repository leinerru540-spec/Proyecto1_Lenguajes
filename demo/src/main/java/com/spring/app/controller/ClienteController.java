package com.spring.app.controller;

import com.spring.app.dto.ClienteDTO;
import com.spring.app.dto.ClienteForm;
import com.spring.app.entity.Cliente;
import com.spring.app.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ClienteDTO> obtener(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(c -> ResponseEntity.ok(new ClienteDTO(
                        c.getId(),
                        c.getEmpresa(),
                        c.getNombre(),
                        c.getTelefono(),
                        c.getCorreo()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@RequestBody ClienteForm clienteForm) {
        Cliente cliente = mapToEntity(clienteForm);
        Cliente saved = clienteRepository.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ClienteDTO(
                saved.getId(),
                saved.getEmpresa(),
                saved.getNombre(),
                saved.getTelefono(),
                saved.getCorreo()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizar(@PathVariable Long id, @RequestBody ClienteForm clienteForm) {
        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Cliente cliente = mapToEntity(clienteForm);
        cliente.setId(id);
        Cliente updated = clienteRepository.save(cliente);
        return ResponseEntity.ok(new ClienteDTO(
                updated.getId(),
                updated.getEmpresa(),
                updated.getNombre(),
                updated.getTelefono(),
                updated.getCorreo()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        clienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Cliente mapToEntity(ClienteForm clienteForm) {
        Cliente cliente = new Cliente();
        cliente.setNombre(clienteForm.getNombre());
        cliente.setTelefono(clienteForm.getTelefono());
        cliente.setEmpresa(clienteForm.getEmpresa());
        cliente.setCorreo(clienteForm.getCorreo());
        return cliente;
    }
}


