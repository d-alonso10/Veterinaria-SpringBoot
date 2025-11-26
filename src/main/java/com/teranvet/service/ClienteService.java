package com.teranvet.service;

import com.teranvet.dto.ClienteDTO;
import com.teranvet.entity.Cliente;
import com.teranvet.repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Obtiene todos los clientes
     */
    public List<ClienteDTO> obtenerTodos() {
        log.info("Obteniendo todos los clientes");
        return clienteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un cliente por ID
     */
    public ClienteDTO obtenerPorId(Integer idCliente) {
        log.info("Obteniendo cliente con ID: {}", idCliente);
        return clienteRepository.findById(idCliente)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    /**
     * Busca clientes por término (nombre, apellido, DNI)
     */
    public List<ClienteDTO> buscarPorTermino(String termino) {
        log.info("Buscando clientes con término: {}", termino);
        return clienteRepository.buscarPorTermino(termino).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un cliente por DNI/RUC
     */
    public ClienteDTO obtenerPorDniRuc(String dniRuc) {
        log.info("Obteniendo cliente con DNI/RUC: {}", dniRuc);
        return clienteRepository.findByDniRuc(dniRuc)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    /**
     * Crea un nuevo cliente
     */
    public ClienteDTO crear(ClienteDTO clienteDTO) {
        log.info("Creando nuevo cliente: {}", clienteDTO.getNombre());

        // Validar que el DNI/RUC sea único (solo si no es null)
        if (clienteDTO.getDniRuc() != null &&
                clienteRepository.findByDniRuc(clienteDTO.getDniRuc()).isPresent()) {
            throw new RuntimeException("El DNI/RUC ya existe en el sistema");
        }

        Cliente cliente = convertToEntity(clienteDTO);
        cliente.setCreatedAt(LocalDateTime.now());
        cliente.setUpdatedAt(LocalDateTime.now());

        Cliente guardado = clienteRepository.save(cliente);
        log.info("Cliente creado exitosamente con ID: {}", guardado.getIdCliente());
        return convertToDTO(guardado);
    }

    /**
     * Actualiza un cliente existente
     */
    public ClienteDTO actualizar(Integer idCliente, ClienteDTO clienteDTO) {
        log.info("Actualizando cliente con ID: {}", idCliente);

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Validar DNI/RUC solo si cambió (manejando NULLs correctamente)
        String dniActual = cliente.getDniRuc();
        String dniNuevo = clienteDTO.getDniRuc();

        // Comparar manejando NULLs correctamente
        boolean dniCambio = (dniActual == null && dniNuevo != null) ||
                (dniActual != null && !dniActual.equals(dniNuevo));

        if (dniCambio) {
            log.info("DNI cambió de '{}' a '{}'", dniActual, dniNuevo);

            // Validar que el nuevo DNI no exista (solo si no es NULL)
            if (dniNuevo != null) {
                Optional<Cliente> clienteConMismoDni = clienteRepository.findByDniRuc(dniNuevo);
                if (clienteConMismoDni.isPresent()) {
                    throw new RuntimeException("El DNI/RUC ya existe en el sistema");
                }
            }
            cliente.setDniRuc(dniNuevo);
        }

        cliente.setNombre(clienteDTO.getNombre());
        cliente.setApellido(clienteDTO.getApellido());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setPreferencias(clienteDTO.getPreferencias());
        cliente.setUpdatedAt(LocalDateTime.now());

        Cliente actualizado = clienteRepository.save(cliente);
        log.info("Cliente actualizado exitosamente con DNI: {}", actualizado.getDniRuc());
        return convertToDTO(actualizado);
    }

    /**
     * Elimina un cliente
     */
    public void eliminar(Integer idCliente) {
        log.info("Eliminando cliente con ID: {}", idCliente);

        if (!clienteRepository.existsById(idCliente)) {
            throw new RuntimeException("Cliente no encontrado");
        }

        clienteRepository.deleteById(idCliente);
        log.info("Cliente eliminado exitosamente");
    }

    /**
     * Convierte Cliente a ClienteDTO
     */
    private ClienteDTO convertToDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getIdCliente(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getDniRuc(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getPreferencias());
    }

    /**
     * Convierte ClienteDTO a Cliente
     */
    private Cliente convertToEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setDniRuc(dto.getDniRuc());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setPreferencias(dto.getPreferencias());
        return cliente;
    }
}
