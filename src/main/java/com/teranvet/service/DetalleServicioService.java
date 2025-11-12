package com.teranvet.service;

import com.teranvet.entity.DetalleServicio;
import com.teranvet.repository.DetalleServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de Detalles de Servicios.
 */
@Service
@Transactional
public class DetalleServicioService {

    @Autowired
    private DetalleServicioRepository detalleRepository;

    /**
     * Obtener todos los detalles de servicios.
     */
    @Transactional(readOnly = true)
    public List<DetalleServicio> obtenerTodos() {
        return detalleRepository.findAll();
    }

    /**
     * Obtener un detalle por ID.
     */
    @Transactional(readOnly = true)
    public Optional<DetalleServicio> obtenerPorId(Integer idDetalle) {
        if (idDetalle == null || idDetalle <= 0) {
            throw new IllegalArgumentException("ID de detalle inválido");
        }
        return detalleRepository.findById(idDetalle);
    }

    /**
     * Obtener detalles de una atención.
     */
    @Transactional(readOnly = true)
    public List<DetalleServicio> obtenerPorAtencion(Integer idAtencion) {
        if (idAtencion == null || idAtencion <= 0) {
            throw new IllegalArgumentException("ID de atención inválido");
        }
        return detalleRepository.findAll().stream()
                .filter(d -> d.getIdAtencion() != null && d.getIdAtencion().equals(idAtencion))
                .toList();
    }

    /**
     * Crear un nuevo detalle de servicio.
     */
    public DetalleServicio crear(DetalleServicio detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle no puede ser nulo");
        }
        
        if (detalle.getIdAtencion() == null || detalle.getIdAtencion() <= 0) {
            throw new IllegalArgumentException("El ID de atención es requerido");
        }
        
        if (detalle.getIdServicio() == null || detalle.getIdServicio() <= 0) {
            throw new IllegalArgumentException("El ID de servicio es requerido");
        }
        
        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        
        if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a 0");
        }
        
        // Calcular subtotal si no está definido
        if (detalle.getSubtotal() == null || detalle.getSubtotal().compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal subtotal = detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()));
            detalle.setSubtotal(subtotal);
        }
        
        return detalleRepository.save(detalle);
    }

    /**
     * Actualizar un detalle existente.
     */
    public DetalleServicio actualizar(Integer idDetalle, DetalleServicio detalleActualizado) {
        if (idDetalle == null || idDetalle <= 0) {
            throw new IllegalArgumentException("ID de detalle inválido");
        }
        
        DetalleServicio detalleExistente = detalleRepository.findById(idDetalle)
                .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado con ID: " + idDetalle));
        
        if (detalleActualizado.getCantidad() != null && detalleActualizado.getCantidad() > 0) {
            detalleExistente.setCantidad(detalleActualizado.getCantidad());
        }
        
        if (detalleActualizado.getPrecioUnitario() != null && 
            detalleActualizado.getPrecioUnitario().compareTo(BigDecimal.ZERO) > 0) {
            detalleExistente.setPrecioUnitario(detalleActualizado.getPrecioUnitario());
        }
        
        if (detalleActualizado.getObservaciones() != null) {
            detalleExistente.setObservaciones(detalleActualizado.getObservaciones());
        }
        
        // Recalcular subtotal
        BigDecimal subtotal = detalleExistente.getPrecioUnitario()
                .multiply(new BigDecimal(detalleExistente.getCantidad()));
        detalleExistente.setSubtotal(subtotal);
        
        return detalleRepository.save(detalleExistente);
    }

    /**
     * Eliminar un detalle.
     */
    public void eliminar(Integer idDetalle) {
        if (idDetalle == null || idDetalle <= 0) {
            throw new IllegalArgumentException("ID de detalle inválido");
        }
        
        if (!detalleRepository.existsById(idDetalle)) {
            throw new IllegalArgumentException("Detalle no encontrado con ID: " + idDetalle);
        }
        
        detalleRepository.deleteById(idDetalle);
    }

    /**
     * Calcular subtotal total de una atención.
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularSubtotalAtencion(Integer idAtencion) {
        if (idAtencion == null || idAtencion <= 0) {
            throw new IllegalArgumentException("ID de atención inválido");
        }
        
        return detalleRepository.findAll().stream()
                .filter(d -> d.getIdAtencion() != null && d.getIdAtencion().equals(idAtencion))
                .map(DetalleServicio::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
