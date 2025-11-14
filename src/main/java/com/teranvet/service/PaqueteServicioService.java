package com.teranvet.service;

import com.teranvet.entity.PaqueteServicio;
import com.teranvet.repository.PaqueteServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // <--- IMPORT REQUERIDO

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

        // CORREGIDO: El campo se llama 'precioTotal'
        if (paquete.getPrecioTotal() == null || paquete.getPrecioTotal().compareTo(BigDecimal.ZERO) <= 0) {
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

        // CORREGIDO: El campo se llama 'precioTotal'
        if (paqueteActualizado.getPrecioTotal() != null && paqueteActualizado.getPrecioTotal().compareTo(BigDecimal.ZERO) > 0) {
            paqueteExistente.setPrecioTotal(paqueteActualizado.getPrecioTotal());
        }

        // ELIMINADO: El campo 'descuentoAplicado' no existe en la entidad.
        // if (paqueteActualizado.getDescuentoAplicado() != null) { ... }

        // ELIMINADO: El campo 'estado' no existe en la entidad.
        // if (paqueteActualizado.getEstado() != null) { ... }

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
     * ELIMINADO: El concepto de 'activo' (estado) no existe en la entidad PaqueteServicio.
     * Este método ahora simplemente devuelve todos los paquetes.
     */
    @Transactional(readOnly = true)
    public List<PaqueteServicio> obtenerActivos() {
        // CORRECCIÓN: Eliminado el filtro por 'estado'
        return paqueteRepository.findAll();
    }

    /**
     * Obtener precio final del paquete.
     * CORREGIDO: Como no hay descuento, el precio final es el precio total.
     */
    @Transactional(readOnly = true)
    public BigDecimal obtenerPrecioFinal(Integer idPaquete) {
        if (idPaquete == null || idPaquete <= 0) {
            throw new IllegalArgumentException("ID de paquete inválido");
        }

        PaqueteServicio paquete = paqueteRepository.findById(idPaquete)
                .orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado con ID: " + idPaquete));

        // CORREGIDO: Devuelve 'precioTotal' ya que 'descuentoAplicado' no existe.
        return paquete.getPrecioTotal();
    }
}