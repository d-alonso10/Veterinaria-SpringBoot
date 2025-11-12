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
     * Crea una atención desde una cita
     */
    public Atencion crearDesdeCita(Integer idCita, Integer idGroomer, Integer idSucursal, 
                                   LocalDateTime tiempoEstimadoInicio, 
                                   LocalDateTime tiempoEstimadoFin) {
        log.info("Creando atención desde cita: {}", idCita);
        
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        
        Groomer groomer = groomerRepository.findById(idGroomer)
                .orElseThrow(() -> new RuntimeException("Groomer no encontrado"));
        
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
        
        Atencion atencion = new Atencion();
        atencion.setCita(cita);
        atencion.setMascota(cita.getMascota());
        atencion.setCliente(cita.getCliente());
        atencion.setGroomer(groomer);
        atencion.setSucursal(sucursal);
        atencion.setEstado(Atencion.Estado.en_espera);
        atencion.setTiempoEstimadoInicio(tiempoEstimadoInicio);
        atencion.setTiempoEstimadoFin(tiempoEstimadoFin);
        atencion.setPrioridad(0);
        atencion.setCreatedAt(LocalDateTime.now());
        atencion.setUpdatedAt(LocalDateTime.now());
        
        // Actualizar estado de la cita
        cita.setEstado(Cita.Estado.confirmada);
        citaRepository.save(cita);
        
        Atencion guardada = atencionRepository.save(atencion);
        log.info("Atención creada exitosamente con ID: {}", guardada.getIdAtencion());
        return guardada;
    }
    
    /**
     * Crea una atención walk-in (sin cita previa)
     */
    public Atencion crearWalkIn(Integer idMascota, Integer idCliente, Integer idGroomer, 
                               Integer idSucursal, LocalDateTime tiempoEstimadoInicio,
                               LocalDateTime tiempoEstimadoFin, String observaciones) {
        log.info("Creando atención walk-in para mascota: {}", idMascota);
        
        Mascota mascota = mascotaRepository.findById(idMascota)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        Groomer groomer = groomerRepository.findById(idGroomer)
                .orElseThrow(() -> new RuntimeException("Groomer no encontrado"));
        
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
        
        Atencion atencion = new Atencion();
        atencion.setMascota(mascota);
        atencion.setCliente(cliente);
        atencion.setGroomer(groomer);
        atencion.setSucursal(sucursal);
        atencion.setEstado(Atencion.Estado.en_espera);
        atencion.setTiempoEstimadoInicio(tiempoEstimadoInicio);
        atencion.setTiempoEstimadoFin(tiempoEstimadoFin);
        atencion.setObservaciones(observaciones);
        atencion.setPrioridad(0);
        atencion.setCreatedAt(LocalDateTime.now());
        atencion.setUpdatedAt(LocalDateTime.now());
        
        Atencion guardada = atencionRepository.save(atencion);
        log.info("Atención walk-in creada con ID: {}", guardada.getIdAtencion());
        return guardada;
    }
    
    /**
     * Actualiza el estado de una atención
     */
    public Atencion actualizarEstado(Integer idAtencion, String nuevoEstado) {
        log.info("Actualizando estado de atención: {} a {}", idAtencion, nuevoEstado);
        
        Atencion atencion = atencionRepository.findById(idAtencion)
                .orElseThrow(() -> new RuntimeException("Atención no encontrada"));
        
        try {
            Atencion.Estado estado = Atencion.Estado.valueOf(nuevoEstado);
            atencion.setEstado(estado);
            
            // Si cambia a "en_servicio", registrar hora real de inicio
            if (estado == Atencion.Estado.en_servicio && atencion.getTiempoRealInicio() == null) {
                atencion.setTiempoRealInicio(LocalDateTime.now());
            }
            
            // Si cambia a "terminado", registrar hora real de fin
            if (estado == Atencion.Estado.terminado && atencion.getTiempoRealFin() == null) {
                atencion.setTiempoRealFin(LocalDateTime.now());
            }
            
            atencion.setUpdatedAt(LocalDateTime.now());
            
            Atencion actualizada = atencionRepository.save(atencion);
            log.info("Estado de atención actualizado");
            return actualizada;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado no válido: " + nuevoEstado);
        }
    }
    
    /**
     * Marca una atención como terminada
     */
    public Atencion terminar(Integer idAtencion) {
        log.info("Terminando atención: {}", idAtencion);
        return actualizarEstado(idAtencion, "terminado");
    }
}
