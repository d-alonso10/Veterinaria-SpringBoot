package com.teranvet.service;

import com.teranvet.dto.MascotaDTO;
import com.teranvet.entity.Cliente;
import com.teranvet.entity.Mascota;
import com.teranvet.repository.ClienteRepository;
import com.teranvet.repository.MascotaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class MascotaService {
    
    @Autowired
    private MascotaRepository mascotaRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    /**
     * Obtiene todas las mascotas
     */
    public List<MascotaDTO> obtenerTodas() {
        log.info("Obteniendo todas las mascotas");
        return mascotaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una mascota por ID
     */
    public MascotaDTO obtenerPorId(Integer idMascota) {
        log.info("Obteniendo mascota con ID: {}", idMascota);
        return mascotaRepository.findById(idMascota)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
    }
    
    /**
     * Obtiene mascotas de un cliente específico
     */
    public List<MascotaDTO> obtenerPorCliente(Integer idCliente) {
        log.info("Obteniendo mascotas del cliente: {}", idCliente);
        
        if (!clienteRepository.existsById(idCliente)) {
            throw new RuntimeException("Cliente no encontrado");
        }
        
        return mascotaRepository.findByCliente_IdCliente(idCliente).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca mascotas por término (nombre, raza)
     */
    public List<MascotaDTO> buscarPorTermino(String termino) {
        log.info("Buscando mascotas con término: {}", termino);
        return mascotaRepository.buscarPorTermino(termino).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Crea una nueva mascota
     */
    public MascotaDTO crear(MascotaDTO mascotaDTO) {
        log.info("Creando nueva mascota: {}", mascotaDTO.getNombre());
        
        Cliente cliente = clienteRepository.findById(mascotaDTO.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        Mascota mascota = convertToEntity(mascotaDTO);
        mascota.setCliente(cliente);
        mascota.setCreatedAt(LocalDateTime.now());
        mascota.setUpdatedAt(LocalDateTime.now());
        
        Mascota guardada = mascotaRepository.save(mascota);
        log.info("Mascota creada exitosamente con ID: {}", guardada.getIdMascota());
        return convertToDTO(guardada);
    }
    
    /**
     * Actualiza una mascota existente
     */
    public MascotaDTO actualizar(Integer idMascota, MascotaDTO mascotaDTO) {
        log.info("Actualizando mascota con ID: {}", idMascota);
        
        Mascota mascota = mascotaRepository.findById(idMascota)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        
        mascota.setNombre(mascotaDTO.getNombre());
        mascota.setEspecie(Mascota.Especie.valueOf(mascotaDTO.getEspecie()));
        mascota.setRaza(mascotaDTO.getRaza());
        mascota.setSexo(Mascota.Sexo.valueOf(mascotaDTO.getSexo()));
        mascota.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
        mascota.setMicrochip(mascotaDTO.getMicrochip());
        mascota.setObservaciones(mascotaDTO.getObservaciones());
        mascota.setUpdatedAt(LocalDateTime.now());
        
        Mascota actualizada = mascotaRepository.save(mascota);
        log.info("Mascota actualizada exitosamente");
        return convertToDTO(actualizada);
    }
    
    /**
     * Elimina una mascota
     */
    public void eliminar(Integer idMascota) {
        log.info("Eliminando mascota con ID: {}", idMascota);
        
        if (!mascotaRepository.existsById(idMascota)) {
            throw new RuntimeException("Mascota no encontrada");
        }
        
        mascotaRepository.deleteById(idMascota);
        log.info("Mascota eliminada exitosamente");
    }
    
    /**
     * Convierte Mascota a MascotaDTO
     */
    private MascotaDTO convertToDTO(Mascota mascota) {
        return new MascotaDTO(
                mascota.getIdMascota(),
                mascota.getCliente().getIdCliente(),
                mascota.getNombre(),
                mascota.getEspecie().toString(),
                mascota.getRaza(),
                mascota.getSexo() != null ? mascota.getSexo().toString() : null,
                mascota.getFechaNacimiento(),
                mascota.getMicrochip(),
                mascota.getObservaciones()
        );
    }
    
    /**
     * Convierte MascotaDTO a Mascota
     */
    private Mascota convertToEntity(MascotaDTO dto) {
        Mascota mascota = new Mascota();
        mascota.setNombre(dto.getNombre());
        mascota.setEspecie(Mascota.Especie.valueOf(dto.getEspecie()));
        mascota.setRaza(dto.getRaza());
        mascota.setSexo(Mascota.Sexo.valueOf(dto.getSexo()));
        mascota.setFechaNacimiento(dto.getFechaNacimiento());
        mascota.setMicrochip(dto.getMicrochip());
        mascota.setObservaciones(dto.getObservaciones());
        return mascota;
    }
}
