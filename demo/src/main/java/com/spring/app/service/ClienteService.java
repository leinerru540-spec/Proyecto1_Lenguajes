package com.spring.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.spring.app.entity.Cliente;
import com.spring.app.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll(); // Implementar lógica para obtener todos los clientes
    }

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente); // Implementar lógica para crear un cliente
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id); // Implementar lógica para obtener un cliente por ID
    }

    public Optional<Cliente> findByCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    public void deleteById(Long id) {
        clienteRepository.deleteById(id); // Implementar lógica para eliminar un cliente por ID
    }

    public Cliente update(Long id, Cliente clienteActualizado) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));

        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setTelefono(clienteActualizado.getTelefono());
        clienteExistente.setEmpresa(clienteActualizado.getEmpresa());
        clienteExistente.setCorreo(clienteActualizado.getCorreo());

        return clienteRepository.save(clienteExistente);
    }

}
