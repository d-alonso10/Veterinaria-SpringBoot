package com.teranvet.service;

import com.teranvet.entity.PaqueteServicio;
import com.teranvet.repository.PaqueteServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de Paquetes de Servicios.
 */
@Service
@Transactional
public class PaqueteServicioService {

    @Autowired
    private PaqueteServicioRepository paqueteRepository;

    /**
     * Obtener todos los paquetes.
     */
    @Transactional(readOnly = true)
    public List<PaqueteServicio> obtenerTodos() {
        return paqueteRepository.findAll();
    }

    /**
     * Obtener un paquete por ID.
     */
    @Transactional(readOnly = true)
    public Optional<PaqueteServicio> obtenerPorId(Integer idPaquete) {
        if (idPaquete == null || idPaquete <= 0) {
            throw new IllegalArgumentException("ID de paquete inválido");
        }
        return paqueteRepository.findById(idPaquete);
    }

    /**
     * Crear un nuevo paquete.
     */
    public PaqueteServicio crear(PaqueteServicio paquete) {
        if (paquete == null) {
            throw new IllegalArgumentException("El paquete no puede ser nulo");
        }
        
        if (paquete.getNombre() == null || paquete.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del paquete es requerido");
        }
        
        if (paquete.getPrecio() == null || paquete.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio del paquete debe ser mayor a 0");
        }
        
        return paqueteRepository.save(paquete);
    }

    /**
     * Actualizar un paquete existente.
     */
    public PaqueteServicio actualizar(Integer idPaquete, PaqueteServicio paqueteActualizado) {
        if (idPaquete == null || idPaquete <= 0) {
            throw new IllegalArgumentException("ID de paquete inválido");
        }
        
        PaqueteServicio paqueteExistente = paqueteRepository.findById(idPaquete)
                .orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado con ID: " + idPaquete));
        
        if (paqueteActualizado.getNombre() != null && !paqueteActualizado.getNombre().trim().isEmpty()) {
            paqueteExistente.setNombre(paqueteActualizado.getNombre());
        }
        
        if (paqueteActualizado.getDescripcion() != null) {
            paqueteExistente.setDescripcion(paqueteActualizado.getDescripcion());
        }
        
        if (paqueteActualizado.getPrecio() != null && paqueteActualizado.getPrecio().compareTo(BigDecimal.ZERO) > 0) {
            paqueteExistente.setPrecio(paqueteActualizado.getPrecio());
        }
        
        if (paqueteActualizado.getDescuento() != null) {
            paqueteExistente.setDescuento(paqueteActualizado.getDescuento());
        }
        
        if (paqueteActualizado.getEstado() != null) {
            paqueteExistente.setEstado(paqueteActualizado.getEstado());
        }
        
        return paqueteRepository.save(paqueteExistente);
    }

    /**
     * Eliminar un paquete.
     */
    public void eliminar(Integer idPaquete) {
        if (idPaquete == null || idPaquete <= 0) {
            throw new IllegalArgumentException("ID de paquete inválido");
        }
        
        if (!paqueteRepository.existsById(idPaquete)) {
            throw new IllegalArgumentException("Paquete no encontrado con ID: " + idPaquete);
        }
        
        paqueteRepository.deleteById(idPaquete);
    }

    /**
     * Obtener paquetes activos.
     */
    @Transactional(readOnly = true)
    public List<PaqueteServicio> obtenerActivos() {
        return paqueteRepository.findAll().stream()
                .filter(p -> p.getEstado() != null && p.getEstado().equals("activo"))
                .toList();
    }

    /**
     * Obtener precio final del paquete con descuento aplicado.
     */
    @Transactional(readOnly = true)
    public BigDecimal obtenerPrecioFinal(Integer idPaquete) {
        if (idPaquete == null || idPaquete <= 0) {
            throw new IllegalArgumentException("ID de paquete inválido");
        }
        
        PaqueteServicio paquete = paqueteRepository.findById(idPaquete)
                .orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado con ID: " + idPaquete));
        
        BigDecimal descuentoAplicado = paquete.getDescuento() != null ? paquete.getDescuento() : BigDecimal.ZERO;
        return paquete.getPrecio().subtract(descuentoAplicado);
    }
}
