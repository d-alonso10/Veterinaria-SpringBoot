package com.teranvet.service;

import com.teranvet.dto.CitaDTO;
import com.teranvet.entity.*;
import com.teranvet.repository.*;
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
public class CitaService {
    
    @Autowired
    private CitaRepository citaRepository;
    
    @Autowired
    private MascotaRepository mascotaRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    @Autowired
    private SucursalRepository sucursalRepository;
    
    /**
     * Obtiene todas las citas
     */
    public List<CitaDTO> obtenerTodas() {
        log.info("Obteniendo todas las citas");
        return citaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una cita por ID
     */
    public CitaDTO obtenerPorId(Integer idCita) {
        log.info("Obteniendo cita con ID: {}", idCita);
        return citaRepository.findById(idCita)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
    }
    
    /**
     * Obtiene las próximas citas de un cliente
     */
    public List<CitaDTO> obtenerProximasCitas(Integer idCliente) {
        log.info("Obteniendo próximas citas del cliente: {}", idCliente);
        
        if (!clienteRepository.existsById(idCliente)) {
            throw new RuntimeException("Cliente no encontrado");
        }
        
        return citaRepository.findProximasCitas(idCliente).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene citas de un cliente
     */
    public List<CitaDTO> obtenerPorCliente(Integer idCliente) {
        log.info("Obteniendo citas del cliente: {}", idCliente);
        return citaRepository.findByCliente_IdCliente(idCliente).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Crea una nueva cita usando el SP
     */
    public void crear(Integer idMascota, Integer idCliente, Integer idSucursal, 
                      Integer idServicio, LocalDateTime fechaProgramada, 
                      String modalidad, String notas) {
        log.info("Creando nueva cita para mascota usando SP: {}", idMascota);
        
        // Validaciones
        if (!mascotaRepository.existsById(idMascota)) {
            throw new RuntimeException("Mascota no encontrada");
        }
        
        if (!clienteRepository.existsById(idCliente)) {
            throw new RuntimeException("Cliente no encontrada");
        }
        
        if (!sucursalRepository.existsById(idSucursal)) {
            throw new RuntimeException("Sucursal no encontrada");
        }
        
        if (!servicioRepository.existsById(idServicio)) {
            throw new RuntimeException("Servicio no encontrado");
        }
        
        try {
            // Llamar al SP
            citaRepository.crearCita(
                    idMascota,
                    idCliente,
                    idSucursal,
                    idServicio,
                    fechaProgramada,
                    modalidad,
                    notas
            );
            log.info("Cita creada exitosamente usando SP");
        } catch (Exception e) {
            log.error("Error al crear cita: ", e);
            throw new RuntimeException("Error al crear cita: " + e.getMessage());
        }
    }
    
    /**
     * Reprograma una cita usando el SP
     */
    public void reprogramar(Integer idCita, LocalDateTime nuevaFecha) {
        log.info("Reprogramando cita usando SP con ID: {}", idCita);
        
        // Validar que la cita existe
        if (!citaRepository.existsById(idCita)) {
            throw new RuntimeException("Cita no encontrada");
        }
        
        try {
            // Llamar al SP
            citaRepository.reprogramarCita(idCita, nuevaFecha);
            log.info("Cita reprogramada exitosamente usando SP");
        } catch (Exception e) {
            log.error("Error al reprogramar cita: ", e);
            throw new RuntimeException("Error al reprogramar cita: " + e.getMessage());
        }
    }
    
    /**
     * Cancela una cita usando el SP
     */
    public void cancelar(Integer idCita) {
        log.info("Cancelando cita usando SP con ID: {}", idCita);
        
        // Validar que la cita existe
        if (!citaRepository.existsById(idCita)) {
            throw new RuntimeException("Cita no encontrada");
        }
        
        try {
            // Llamar al SP
            citaRepository.cancelarCita(idCita);
            log.info("Cita cancelada exitosamente usando SP");
        } catch (Exception e) {
            log.error("Error al cancelar cita: ", e);
            throw new RuntimeException("Error al cancelar cita: " + e.getMessage());
        }
    }
    
    /**
     * Confirma asistencia de una cita usando el SP
     */
    public void confirmarAsistencia(Integer idCita) {
        log.info("Confirmando asistencia a cita usando SP con ID: {}", idCita);
        
        // Validar que la cita existe
        if (!citaRepository.existsById(idCita)) {
            throw new RuntimeException("Cita no encontrada");
        }
        
        try {
            // Llamar al SP
            citaRepository.confirmarAsistenciaCita(idCita);
            log.info("Asistencia confirmada exitosamente usando SP");
        } catch (Exception e) {
            log.error("Error al confirmar asistencia: ", e);
            throw new RuntimeException("Error al confirmar asistencia: " + e.getMessage());
        }
    }
    
    /**
     * Marca cita como no-show usando el SP
     */
    public void marcarNoShow(Integer idCita) {
        log.info("Marcando cita como no-show usando SP: {}", idCita);
        
        // Validar que la cita existe
        if (!citaRepository.existsById(idCita)) {
            throw new RuntimeException("Cita no encontrada");
        }
        
        try {
            // Llamar al SP - puede usarse el SP de cancelar o crear uno específico
            // Por ahora usamos el de confirmar pero deberíamos tener uno para no-show
            // Esta es una simplificación
            log.warn("No-show marcado (debería existir un SP específico)");
            throw new RuntimeException("No-show requiere un SP específico aún no disponible");
        } catch (Exception e) {
            log.error("Error al marcar no-show: ", e);
            throw new RuntimeException("Error al marcar no-show: " + e.getMessage());
        }
    }
    
    /**
     * Convierte Cita a CitaDTO
     */
    private CitaDTO convertToDTO(Cita cita) {
        return new CitaDTO(
                cita.getIdCita(),
                cita.getMascota().getIdMascota(),
                cita.getCliente().getIdCliente(),
                cita.getSucursal().getIdSucursal(),
                cita.getServicio().getIdServicio(),
                cita.getFechaProgramada(),
                cita.getModalidad().toString(),
                cita.getEstado().toString(),
                cita.getNotas()
        );
    }
    
    /**
     * Convierte CitaDTO a Cita
     */
    private Cita convertToEntity(CitaDTO dto) {
        Cita cita = new Cita();
        cita.setFechaProgramada(dto.getFechaProgramada());
        cita.setModalidad(Cita.Modalidad.valueOf(dto.getModalidad()));
        cita.setNotas(dto.getNotas());
        return cita;
    }
}
