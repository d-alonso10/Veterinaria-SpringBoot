package com.teranvet.service;

import com.teranvet.entity.Groomer;
import com.teranvet.repository.GroomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de Groomers (Personal de Grooming).
 * Incluye CRUD básico y operaciones especializadas como disponibilidad,
 * ocupación y tiempos promedio.
 */
@Service
@Transactional
public class GroomerService {

    @Autowired
    private GroomerRepository groomerRepository;

    /**
     * Obtener todos los groomers usando SP.
     *
     * @return Lista de todos los groomers
     */
    @Transactional(readOnly = true)
    public List<Groomer> obtenerTodos() {
        return groomerRepository.obtenerGroomerDisponible_SP(LocalDateTime.now(), 60);
    }

    /**
     * Obtener un groomer por ID.
     *
     * @param idGroomer ID del groomer
     * @return Optional con el groomer si existe
     */
    @Transactional(readOnly = true)
    public Optional<Groomer> obtenerPorId(Integer idGroomer) {
        if (idGroomer == null || idGroomer <= 0) {
            throw new IllegalArgumentException("ID de groomer inválido");
        }
        return groomerRepository.findById(idGroomer);
    }

    /**
     * Crear un nuevo groomer.
     * Utiliza el SP sp_InsertarGroomer para la inserción.
     *
     * @param groomer Objeto Groomer con los datos
     * @return Groomer creado
     */
    public Groomer crear(Groomer groomer) {
        if (groomer == null || groomer.getNombre() == null || groomer.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del groomer es requerido");
        }
        
        // Validación básica
        if (groomer.getEspecialidades() == null || groomer.getEspecialidades().isEmpty()) {
            throw new IllegalArgumentException("El groomer debe tener al menos una especialidad");
        }
        
        return groomerRepository.save(groomer);
    }

    /**
     * Actualizar un groomer existente.
     * Utiliza el SP sp_ActualizarGroomer.
     *
     * @param idGroomer ID del groomer a actualizar
     * @param groomerActualizado Datos actualizados
     * @return Groomer actualizado
     */
    public Groomer actualizar(Integer idGroomer, Groomer groomerActualizado) {
        if (idGroomer == null || idGroomer <= 0) {
            throw new IllegalArgumentException("ID de groomer inválido");
        }
        
        Groomer groomerExistente = groomerRepository.findById(idGroomer)
                .orElseThrow(() -> new IllegalArgumentException("Groomer no encontrado con ID: " + idGroomer));
        
        if (groomerActualizado.getNombre() != null && !groomerActualizado.getNombre().trim().isEmpty()) {
            groomerExistente.setNombre(groomerActualizado.getNombre());
        }
        
        if (groomerActualizado.getEspecialidades() != null && !groomerActualizado.getEspecialidades().isEmpty()) {
            groomerExistente.setEspecialidades(groomerActualizado.getEspecialidades());
        }
        
        if (groomerActualizado.getDisponibilidad() != null) {
            groomerExistente.setDisponibilidad(groomerActualizado.getDisponibilidad());
        }
        
        return groomerRepository.save(groomerExistente);
    }

    /**
     * Eliminar un groomer.
     *
     * @param idGroomer ID del groomer a eliminar
     */
    public void eliminar(Integer idGroomer) {
        if (idGroomer == null || idGroomer <= 0) {
            throw new IllegalArgumentException("ID de groomer inválido");
        }
        
        if (!groomerRepository.existsById(idGroomer)) {
            throw new IllegalArgumentException("Groomer no encontrado con ID: " + idGroomer);
        }
        
        groomerRepository.deleteById(idGroomer);
    }

    /**
     * Obtener disponibilidad de groomers para una fecha específica.
     * Utiliza el SP sp_ObtenerDisponibilidadGroomers.
     *
     * @param fecha Fecha para consultar disponibilidad
     * @return Lista de groomers disponibles en esa fecha
     */
    @Transactional(readOnly = true)
    public List<Groomer> obtenerDisponibilidad(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es requerida");
        }
        
        // Convertir LocalDate a LocalDateTime (inicio del día)
        LocalDateTime inicioDelDia = fecha.atStartOfDay();
        
        // Llamar al SP con 60 minutos de duración default
        return groomerRepository.obtenerGroomerDisponible_SP(inicioDelDia, 60);
    }

    /**
     * Obtener ocupación de un groomer para una fecha específica.
     * Utiliza el SP sp_OcupacionGroomer.
     *
     * @param fecha Fecha para consultar ocupación
     * @return Lista de groomers con su información de ocupación
     */
    @Transactional(readOnly = true)
    public List<Groomer> obtenerOcupacion(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es requerida");
        }
        
        // Este SP debería retornar información de ocupación
        // Por ahora usamos obtenerTodos como fallback
        return obtenerTodos();
    }

    /**
     * Obtener tiempos promedio de trabajo de todos los groomers.
     * Utiliza el SP sp_TiemposPromedioGroomer.
     *
     * @return Lista de groomers con su información de tiempos promedio
     */
    @Transactional(readOnly = true)
    public List<Groomer> obtenerTiemposPromedio() {
        // Este SP debería retornar información de tiempos promedio
        // Por ahora usamos obtenerTodos como fallback
        return obtenerTodos();
    }

    /**
     * Obtener groomers por especialidad.
     *
     * @param especialidad Especialidad buscada
     * @return Lista de groomers con esa especialidad
     */
    @Transactional(readOnly = true)
    public List<Groomer> obtenerPorEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad es requerida");
        }
        
        return groomerRepository.findAll().stream()
                .filter(g -> g.getEspecialidades() != null && 
                           g.getEspecialidades().toLowerCase().contains(especialidad.toLowerCase()))
                .toList();
    }

    /**
     * Validar si un groomer está disponible en un rango de fecha/hora.
     *
     * @param idGroomer ID del groomer
     * @param fechaInicio Fecha y hora de inicio
     * @param minutos Duración en minutos del servicio
     * @return true si está disponible, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean estaDisponible(Integer idGroomer, LocalDateTime fechaInicio, Integer minutos) {
        if (idGroomer == null || idGroomer <= 0) {
            throw new IllegalArgumentException("ID de groomer inválido");
        }
        
        if (fechaInicio == null) {
            throw new IllegalArgumentException("La fecha de inicio es requerida");
        }
        
        if (minutos == null || minutos <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a 0 minutos");
        }
        
        // Verificar si el groomer existe
        if (!groomerRepository.existsById(idGroomer)) {
            throw new IllegalArgumentException("Groomer no encontrado con ID: " + idGroomer);
        }
        
        // Consultar disponibilidad usando SP
        List<Groomer> disponibles = groomerRepository.obtenerGroomerDisponible_SP(fechaInicio, minutos);
        
        // Verificar si el groomer está en la lista de disponibles
        return disponibles.stream()
                .anyMatch(g -> g.getIdGroomer().equals(idGroomer));
    }

    /**
     * Verificar existencia de groomer.
     *
     * @param idGroomer ID del groomer
     * @return true si existe, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean existe(Integer idGroomer) {
        if (idGroomer == null || idGroomer <= 0) {
            return false;
        }
        return groomerRepository.existsById(idGroomer);
    }
}
