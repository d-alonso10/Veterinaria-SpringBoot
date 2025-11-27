package com.teranvet.service;

import com.teranvet.entity.*;
import com.teranvet.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
public class AtencionService {
    
    @Autowired
    private AtencionRepository atencionRepository;
    
    @Autowired
    private CitaRepository citaRepository;
    
    @Autowired
    private MascotaRepository mascotaRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private GroomerRepository groomerRepository;
    
    @Autowired
    private SucursalRepository sucursalRepository;
    
    /**
     * Obtiene todas las atenciones
     */
    public List<Atencion> obtenerTodas() {
        log.info("Obteniendo todas las atenciones");
        return atencionRepository.findAll();
    }
    
    /**
     * Obtiene una atención por ID
     */
    public Atencion obtenerPorId(Integer idAtencion) {
        log.info("Obteniendo atención con ID: {}", idAtencion);
        return atencionRepository.findById(idAtencion)
                .orElseThrow(() -> new RuntimeException("Atención no encontrada"));
    }
    
    /**
     * Obtiene la cola actual de una sucursal
     */
    public List<Atencion> obtenerColaActual(Integer idSucursal) {
        log.info("Obteniendo cola actual de sucursal: {}", idSucursal);
        
        if (!sucursalRepository.existsById(idSucursal)) {
            throw new RuntimeException("Sucursal no encontrada");
        }
        
        return atencionRepository.findColaActual(idSucursal);
    }
    
    /**
     * Obtiene atenciones de un cliente
     */
    public List<Atencion> obtenerPorCliente(Integer idCliente) {
        log.info("Obteniendo atenciones del cliente: {}", idCliente);
        return atencionRepository.findByCliente_IdCliente(idCliente);
    }
    
    /**
     * Crea una atención desde una cita usando el SP
     */
    public Atencion crearDesdeCita(Integer idCita, Integer idGroomer, Integer idSucursal, 
                               Integer turnoNum,
                               LocalDateTime tiempoEstimadoInicio, 
                               LocalDateTime tiempoEstimadoFin,
                               Integer prioridad) {
        log.info("Creando atención desde cita usando SP: {}", idCita);
        
        // Validar que la cita existe
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        
        // Validar que el groomer existe
        if (!groomerRepository.existsById(idGroomer)) {
            throw new RuntimeException("Groomer no encontrado");
        }
        
        // Validar que la sucursal existe
        if (!sucursalRepository.existsById(idSucursal)) {
            throw new RuntimeException("Sucursal no encontrada");
        }
        
        try {
            // Llamar al SP y capturar resultado
            Atencion atencionCreada = atencionRepository.crearAtencionDesdeCita(
                    idCita,
                    idGroomer,
                    idSucursal,
                    turnoNum,
                    tiempoEstimadoInicio,
                    tiempoEstimadoFin,
                    prioridad
            );
            log.info("Atención creada exitosamente desde cita usando SP con ID: {}", atencionCreada.getIdAtencion());
            return atencionCreada;
        } catch (Exception e) {
            log.error("Error al crear atención desde cita: ", e);
            throw new RuntimeException("Error al crear atención: " + e.getMessage());
        }
    }
    
    /**
     * Crea una atención walk-in (sin cita previa) usando el SP
     */
    public Atencion crearWalkIn(Integer idMascota, Integer idCliente, Integer idGroomer, 
                            Integer idSucursal, Integer turnoNum,
                            LocalDateTime tiempoEstimadoInicio,
                            LocalDateTime tiempoEstimadoFin, 
                            Integer prioridad,
                            String observaciones) {
        log.info("Creando atención walk-in para mascota usando SP: {}", idMascota);
        
        // Validaciones
        if (!mascotaRepository.existsById(idMascota)) {
            throw new RuntimeException("Mascota no encontrada");
        }
        
        if (!clienteRepository.existsById(idCliente)) {
            throw new RuntimeException("Cliente no encontrado");
        }
        
        if (!groomerRepository.existsById(idGroomer)) {
            throw new RuntimeException("Groomer no encontrado");
        }
        
        if (!sucursalRepository.existsById(idSucursal)) {
            throw new RuntimeException("Sucursal no encontrada");
        }
        
        try {
            // Llamar al SP y capturar resultado
            Atencion atencionCreada = atencionRepository.crearAtencionWalkIn(
                    idMascota,
                    idCliente,
                    idGroomer,
                    idSucursal,
                    turnoNum,
                    tiempoEstimadoInicio,
                    tiempoEstimadoFin,
                    prioridad,
                    observaciones
            );
            log.info("Atención walk-in creada exitosamente usando SP con ID: {}", atencionCreada.getIdAtencion());
            return atencionCreada;
        } catch (Exception e) {
            log.error("Error al crear atención walk-in: ", e);
            throw new RuntimeException("Error al crear atención walk-in: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza el estado de una atención usando el SP
     */
    public void actualizarEstado(Integer idAtencion, String nuevoEstado) {
        log.info("Actualizando estado de atención usando SP: {} a {}", idAtencion, nuevoEstado);
        
        // Validar que la atención existe
        if (!atencionRepository.existsById(idAtencion)) {
            throw new RuntimeException("Atención no encontrada");
        }
        
        // Validar que el estado es válido
        try {
            Atencion.Estado.valueOf(nuevoEstado);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado no válido: " + nuevoEstado);
        }
        
        try {
            // Llamar al SP
            atencionRepository.actualizarEstadoAtencion(idAtencion, nuevoEstado);
            log.info("Estado de atención actualizado exitosamente usando SP");
        } catch (Exception e) {
            log.error("Error al actualizar estado de atención: ", e);
            throw new RuntimeException("Error al actualizar estado: " + e.getMessage());
        }
    }
    
    /**
     * Marca una atención como terminada usando el SP
     */
    public void terminar(Integer idAtencion) {
        log.info("Terminando atención: {}", idAtencion);
        actualizarEstado(idAtencion, "terminado");
    }
}
