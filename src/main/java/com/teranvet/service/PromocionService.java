package com.teranvet.service;

import com.teranvet.entity.Promocion;
import com.teranvet.repository.PromocionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de Promociones.
 */
@Service
@Transactional
public class PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    /**
     * Obtener todas las promociones.
     */
    @Transactional(readOnly = true)
    public List<Promocion> obtenerTodas() {
        return promocionRepository.findAll();
    }

    /**
     * Obtener una promoción por ID.
     */
    @Transactional(readOnly = true)
    public Optional<Promocion> obtenerPorId(Integer idPromocion) {
        if (idPromocion == null || idPromocion <= 0) {
            throw new IllegalArgumentException("ID de promoción inválido");
        }
        return promocionRepository.findById(idPromocion);
    }

    /**
     * Obtener promociones activas.
     */
    @Transactional(readOnly = true)
    public List<Promocion> obtenerActivas() {
        return promocionRepository.findAll().stream()
                .filter(p -> p.getEstado() != null && p.getEstado().equals("activa") &&
                           p.getFechaInicio() != null && p.getFechaFin() != null &&
                           !LocalDate.now().isBefore(p.getFechaInicio()) &&
                           !LocalDate.now().isAfter(p.getFechaFin()))
                .toList();
    }

    /**
     * Crear una nueva promoción.
     */
    public Promocion crear(Promocion promocion) {
        if (promocion == null) {
            throw new IllegalArgumentException("La promoción no puede ser nula");
        }
        
        if (promocion.getNombre() == null || promocion.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la promoción es requerido");
        }
        
        if (promocion.getFechaInicio() == null || promocion.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son requeridas");
        }
        
        if (promocion.getFechaInicio().isAfter(promocion.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        // Establecer estado por defecto si no está definido
        if (promocion.getEstado() == null || promocion.getEstado().isEmpty()) {
            promocion.setEstado("activa");
        }
        
        return promocionRepository.save(promocion);
    }

    /**
     * Actualizar una promoción.
     */
    public Promocion actualizar(Integer idPromocion, Promocion promocionActualizada) {
        if (idPromocion == null || idPromocion <= 0) {
            throw new IllegalArgumentException("ID de promoción inválido");
        }
        
        Promocion promocionExistente = promocionRepository.findById(idPromocion)
                .orElseThrow(() -> new IllegalArgumentException("Promoción no encontrada con ID: " + idPromocion));
        
        if (promocionActualizada.getNombre() != null && !promocionActualizada.getNombre().trim().isEmpty()) {
            promocionExistente.setNombre(promocionActualizada.getNombre());
        }
        
        if (promocionActualizada.getDescripcion() != null) {
            promocionExistente.setDescripcion(promocionActualizada.getDescripcion());
        }
        
        if (promocionActualizada.getFechaInicio() != null && promocionActualizada.getFechaFin() != null) {
            if (!promocionActualizada.getFechaInicio().isAfter(promocionActualizada.getFechaFin())) {
                promocionExistente.setFechaInicio(promocionActualizada.getFechaInicio());
                promocionExistente.setFechaFin(promocionActualizada.getFechaFin());
            }
        }
        
        if (promocionActualizada.getEstado() != null && !promocionActualizada.getEstado().isEmpty()) {
            promocionExistente.setEstado(promocionActualizada.getEstado());
        }
        
        return promocionRepository.save(promocionExistente);
    }

    /**
     * Eliminar una promoción.
     */
    public void eliminar(Integer idPromocion) {
        if (idPromocion == null || idPromocion <= 0) {
            throw new IllegalArgumentException("ID de promoción inválido");
        }
        
        if (!promocionRepository.existsById(idPromocion)) {
            throw new IllegalArgumentException("Promoción no encontrada con ID: " + idPromocion);
        }
        
        promocionRepository.deleteById(idPromocion);
    }

    /**
     * Activar una promoción.
     */
    public Promocion activar(Integer idPromocion) {
        if (idPromocion == null || idPromocion <= 0) {
            throw new IllegalArgumentException("ID de promoción inválido");
        }
        
        Promocion promocion = promocionRepository.findById(idPromocion)
                .orElseThrow(() -> new IllegalArgumentException("Promoción no encontrada con ID: " + idPromocion));
        
        promocion.setEstado("activa");
        return promocionRepository.save(promocion);
    }

    /**
     * Desactivar una promoción.
     */
    public Promocion desactivar(Integer idPromocion) {
        if (idPromocion == null || idPromocion <= 0) {
            throw new IllegalArgumentException("ID de promoción inválido");
        }
        
        Promocion promocion = promocionRepository.findById(idPromocion)
                .orElseThrow(() -> new IllegalArgumentException("Promoción no encontrada con ID: " + idPromocion));
        
        promocion.setEstado("inactiva");
        return promocionRepository.save(promocion);
    }

    /**
     * Verificar si una promoción es válida en la fecha actual.
     */
    @Transactional(readOnly = true)
    public boolean esValida(Integer idPromocion) {
        if (idPromocion == null || idPromocion <= 0) {
            throw new IllegalArgumentException("ID de promoción inválido");
        }
        
        Optional<Promocion> promocion = promocionRepository.findById(idPromocion);
        
        if (promocion.isEmpty()) {
            return false;
        }
        
        Promocion p = promocion.get();
        LocalDate hoy = LocalDate.now();
        
        return p.getEstado() != null && p.getEstado().equals("activa") &&
               p.getFechaInicio() != null && p.getFechaFin() != null &&
               !hoy.isBefore(p.getFechaInicio()) &&
               !hoy.isAfter(p.getFechaFin());
    }
}
