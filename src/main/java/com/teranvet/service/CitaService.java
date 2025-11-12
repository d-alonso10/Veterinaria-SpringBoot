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
     * Crea una nueva cita
     */
    public CitaDTO crear(CitaDTO citaDTO) {
        log.info("Creando nueva cita para mascota: {}", citaDTO.getIdMascota());
        
        Mascota mascota = mascotaRepository.findById(citaDTO.getIdMascota())
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        
        Cliente cliente = clienteRepository.findById(citaDTO.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        Sucursal sucursal = sucursalRepository.findById(citaDTO.getIdSucursal())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
        
        Servicio servicio = servicioRepository.findById(citaDTO.getIdServicio())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        
        Cita cita = convertToEntity(citaDTO);
        cita.setMascota(mascota);
        cita.setCliente(cliente);
        cita.setSucursal(sucursal);
        cita.setServicio(servicio);
        cita.setEstado(Cita.Estado.reservada);
        cita.setCreatedAt(LocalDateTime.now());
        cita.setUpdatedAt(LocalDateTime.now());
        
        Cita guardada = citaRepository.save(cita);
        log.info("Cita creada exitosamente con ID: {}", guardada.getIdCita());
        return convertToDTO(guardada);
    }
    
    /**
     * Reprograma una cita
     */
    public CitaDTO reprogramar(Integer idCita, LocalDateTime nuevaFecha) {
        log.info("Reprogramando cita con ID: {}", idCita);
        
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        
        if (!cita.getEstado().equals(Cita.Estado.reservada) && 
            !cita.getEstado().equals(Cita.Estado.confirmada)) {
            throw new RuntimeException("No se puede reprogramar una cita en este estado");
        }
        
        cita.setFechaProgramada(nuevaFecha);
        cita.setUpdatedAt(LocalDateTime.now());
        
        Cita actualizada = citaRepository.save(cita);
        log.info("Cita reprogramada exitosamente");
        return convertToDTO(actualizada);
    }
    
    /**
     * Cancela una cita
     */
    public CitaDTO cancelar(Integer idCita) {
        log.info("Cancelando cita con ID: {}", idCita);
        
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        
        cita.setEstado(Cita.Estado.cancelada);
        cita.setUpdatedAt(LocalDateTime.now());
        
        Cita actualizada = citaRepository.save(cita);
        log.info("Cita cancelada exitosamente");
        return convertToDTO(actualizada);
    }
    
    /**
     * Confirma asistencia de una cita
     */
    public CitaDTO confirmarAsistencia(Integer idCita) {
        log.info("Confirmando asistencia a cita con ID: {}", idCita);
        
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        
        cita.setEstado(Cita.Estado.asistio);
        cita.setUpdatedAt(LocalDateTime.now());
        
        Cita actualizada = citaRepository.save(cita);
        log.info("Asistencia confirmada");
        return convertToDTO(actualizada);
    }
    
    /**
     * Marca cita como no-show
     */
    public CitaDTO marcarNoShow(Integer idCita) {
        log.info("Marcando cita como no-show: {}", idCita);
        
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        
        cita.setEstado(Cita.Estado.no_show);
        cita.setUpdatedAt(LocalDateTime.now());
        
        Cita actualizada = citaRepository.save(cita);
        log.info("Cita marcada como no-show");
        return convertToDTO(actualizada);
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
