package com.spring.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.spring.app.entity.Cliente;
import com.spring.app.entity.NombreRol;
import com.spring.app.entity.Usuario;
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

    public Cliente ensureClienteForUsuario(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null || usuario.getRol().getNombre() != NombreRol.CLIENTE) {
            return null;
        }

        return clienteRepository.findByCorreo(usuario.getEmail())
                .orElseGet(() -> {
                    Cliente cliente = new Cliente();
                    cliente.setNombre(usuario.getNombre());
                    cliente.setCorreo(usuario.getEmail());
                    cliente.setTelefono("Sin registrar");
                    return clienteRepository.save(cliente);
                });
    }

    public void ensureClientesForUsuarios(List<Usuario> usuarios) {
        usuarios.forEach(this::ensureClienteForUsuario);
    }

    public Cliente syncClienteForUsuario(Usuario usuario, String previousEmail) {
        if (usuario == null || usuario.getRol() == null || usuario.getRol().getNombre() != NombreRol.CLIENTE) {
            return null;
        }

        Optional<Cliente> clienteExistente = previousEmail != null
                ? clienteRepository.findByCorreo(previousEmail)
                : Optional.empty();

        Cliente cliente = clienteExistente
                .or(() -> clienteRepository.findByCorreo(usuario.getEmail()))
                .orElseGet(Cliente::new);

        cliente.setNombre(usuario.getNombre());
        cliente.setCorreo(usuario.getEmail());
        if (cliente.getTelefono() == null || cliente.getTelefono().isBlank()) {
            cliente.setTelefono("Sin registrar");
        }

        return clienteRepository.save(cliente);
    }

    public void deleteByCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return;
        }

        clienteRepository.findByCorreo(correo)
                .ifPresent(clienteRepository::delete);
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
