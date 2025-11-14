package com.teranvet.service;

import com.teranvet.entity.Atencion;
import com.teranvet.entity.DetalleServicio;
import com.teranvet.entity.Servicio;
import com.teranvet.repository.AtencionRepository;
import com.teranvet.repository.DetalleServicioRepository;
import com.teranvet.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // <--- IMPORT REQUERIDO

/**
 * Servicio para gestión de Detalles de Servicios.
 */
@Service
@Transactional
public class DetalleServicioService {

    @Autowired
    private DetalleServicioRepository detalleRepository;

    // Repositorios necesarios para validar y adjuntar entidades
    @Autowired
    private AtencionRepository atencionRepository;

    @Autowired
    private ServicioRepository servicioRepository;

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
                // CORREGIDO: Acceso encadenado a la entidad
                .filter(d -> d.getAtencion() != null && d.getAtencion().getIdAtencion().equals(idAtencion))
                // CORREGIDO: Compatible con Java 8
                .collect(Collectors.toList());
    }

    /**
     * Crear un nuevo detalle de servicio.
     */
    public DetalleServicio crear(DetalleServicio detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle no puede ser nulo");
        }

        // CORREGIDO: Validar ID a través del objeto Atencion
        if (detalle.getAtencion() == null || detalle.getAtencion().getIdAtencion() == null || detalle.getAtencion().getIdAtencion() <= 0) {
            throw new IllegalArgumentException("El ID de atención es requerido");
        }

        // CORREGIDO: Validar ID a través del objeto Servicio
        if (detalle.getServicio() == null || detalle.getServicio().getIdServicio() == null || detalle.getServicio().getIdServicio() <= 0) {
            throw new IllegalArgumentException("El ID de servicio es requerido");
        }

        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a 0");
        }

        // --- MEJORA DE ROBUSTEZ ---
        // 1. Buscar (Fetch) la entidad Atencion
        Atencion atencion = atencionRepository.findById(detalle.getAtencion().getIdAtencion())
                .orElseThrow(() -> new IllegalArgumentException("Atención no encontrada con ID: " + detalle.getAtencion().getIdAtencion()));

        // 2. Buscar (Fetch) la entidad Servicio
        Servicio servicio = servicioRepository.findById(detalle.getServicio().getIdServicio())
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + detalle.getServicio().getIdServicio()));

        // 3. Asignar las entidades gestionadas (fetched) al nuevo detalle
        detalle.setAtencion(atencion);
        detalle.setServicio(servicio);

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

        // --- MEJORA: Asignar nuevas entidades si se proveen ---
        if (detalleActualizado.getAtencion() != null && detalleActualizado.getAtencion().getIdAtencion() != null) {
            Atencion atencion = atencionRepository.findById(detalleActualizado.getAtencion().getIdAtencion())
                    .orElseThrow(() -> new IllegalArgumentException("Atención no encontrada con ID: " + detalleActualizado.getAtencion().getIdAtencion()));
            detalleExistente.setAtencion(atencion);
        }

        if (detalleActualizado.getServicio() != null && detalleActualizado.getServicio().getIdServicio() != null) {
            Servicio servicio = servicioRepository.findById(detalleActualizado.getServicio().getIdServicio())
                    .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + detalleActualizado.getServicio().getIdServicio()));
            detalleExistente.setServicio(servicio);
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
                // CORREGIDO: Acceso encadenado a la entidad
                .filter(d -> d.getAtencion() != null && d.getAtencion().getIdAtencion().equals(idAtencion))
                .map(DetalleServicio::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}