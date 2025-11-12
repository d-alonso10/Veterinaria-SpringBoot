package com.teranvet.service;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.Servicio;
import com.teranvet.repository.ServicioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ServicioService {
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    /**
     * Obtiene todos los servicios
     */
    public List<Servicio> obtenerTodos() {
        log.info("Obteniendo todos los servicios");
        return servicioRepository.findAll();
    }
    
    /**
     * Obtiene un servicio por ID
     */
    public Servicio obtenerPorId(Integer idServicio) {
        log.info("Obteniendo servicio con ID: {}", idServicio);
        return servicioRepository.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
    }
    
    /**
     * Obtiene servicios por categoría
     */
    public List<Servicio> obtenerPorCategoria(String categoria) {
        log.info("Obteniendo servicios de categoría: {}", categoria);
        try {
            Servicio.Categoria cat = Servicio.Categoria.valueOf(categoria);
            return servicioRepository.findByCategoriaSorted(cat);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Categoría no válida: " + categoria);
        }
    }
    
    /**
     * Busca servicios por nombre
     */
    public List<Servicio> buscarPorNombre(String nombre) {
        log.info("Buscando servicios por nombre: {}", nombre);
        return servicioRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    /**
     * Crea un nuevo servicio
     */
    public Servicio crear(Servicio servicio) {
        log.info("Creando nuevo servicio: {}", servicio.getNombre());
        
        if (servicioRepository.findByCodigo(servicio.getCodigo()).isPresent()) {
            throw new RuntimeException("El código de servicio ya existe");
        }
        
        servicio.setCreatedAt(LocalDateTime.now());
        servicio.setUpdatedAt(LocalDateTime.now());
        
        Servicio guardado = servicioRepository.save(servicio);
        log.info("Servicio creado con ID: {}", guardado.getIdServicio());
        return guardado;
    }
    
    /**
     * Actualiza un servicio
     */
    public Servicio actualizar(Integer idServicio, Servicio servicioDTO) {
        log.info("Actualizando servicio con ID: {}", idServicio);
        
        Servicio servicio = servicioRepository.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        
        servicio.setNombre(servicioDTO.getNombre());
        servicio.setDescripcion(servicioDTO.getDescripcion());
        servicio.setDuracionEstimadaMin(servicioDTO.getDuracionEstimadaMin());
        servicio.setPrecioBase(servicioDTO.getPrecioBase());
        servicio.setCategoria(servicioDTO.getCategoria());
        servicio.setUpdatedAt(LocalDateTime.now());
        
        Servicio actualizado = servicioRepository.save(servicio);
        log.info("Servicio actualizado exitosamente");
        return actualizado;
    }
    
    /**
     * Elimina un servicio
     */
    public void eliminar(Integer idServicio) {
        log.info("Eliminando servicio con ID: {}", idServicio);
        
        if (!servicioRepository.existsById(idServicio)) {
            throw new RuntimeException("Servicio no encontrado");
        }
        
        servicioRepository.deleteById(idServicio);
        log.info("Servicio eliminado exitosamente");
    }
}
