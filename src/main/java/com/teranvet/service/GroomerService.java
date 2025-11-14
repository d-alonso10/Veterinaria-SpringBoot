package com.teranvet.service;

import com.teranvet.entity.Groomer;
import com.teranvet.repository.GroomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger; // <--- IMPORT REQUERIDO
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // <--- IMPORT REQUERIDO PARA JAVA 8

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
     * Obtener todos los groomers.
     *
     * @return Lista de todos los groomers
     */
    @Transactional(readOnly = true)
    public List<Groomer> obtenerTodos() {
        // Usamos findAll() de JPA que devuelve List<Groomer>
        return groomerRepository.findAll();
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
     * @return Groomer (Nota: El SP es void, se retorna el objeto de entrada por consistencia)
     */
    public Groomer crear(Groomer groomer) {
        if (groomer == null || groomer.getNombre() == null || groomer.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del groomer es requerido");
        }

        if (groomer.getEspecialidades() == null || groomer.getEspecialidades().isEmpty()) {
            groomer.setEspecialidades("[]");
        }

        if (groomer.getDisponibilidad() == null || groomer.getDisponibilidad().isEmpty()) {
            groomer.setDisponibilidad("{}");
        }

        // Llamada al Stored Procedure
        groomerRepository.insertarGroomer(
                groomer.getNombre(),
                groomer.getEspecialidades(),
                groomer.getDisponibilidad()
        );

        return groomer;
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

        String nombre = (groomerActualizado.getNombre() != null) ? groomerActualizado.getNombre() : groomerExistente.getNombre();
        String especialidades = (groomerActualizado.getEspecialidades() != null) ? groomerActualizado.getEspecialidades() : groomerExistente.getEspecialidades();
        String disponibilidad = (groomerActualizado.getDisponibilidad() != null) ? groomerActualizado.getDisponibilidad() : groomerExistente.getDisponibilidad();

        // Llamada al Stored Procedure
        groomerRepository.actualizarGroomer(
                idGroomer,
                nombre,
                especialidades,
                disponibilidad
        );

        // Retornar el objeto actualizado buscándolo de nuevo
        return groomerRepository.findById(idGroomer).get();
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
     * @return Lista de Object[] con los datos de disponibilidad del SP.
     */
    @Transactional(readOnly = true)
    // CORRECCIÓN: El tipo de retorno es List<Object[]>
    public List<Object[]> obtenerDisponibilidad(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es requerida");
        }

        return groomerRepository.obtenerDisponibilidadGroomers(fecha);
    }

    /**
     * Obtener ocupación de un groomer para una fecha específica.
     * Utiliza el SP sp_OcupacionGroomer.
     *
     * @param fecha Fecha para consultar ocupación
     * @return Lista de Object[] con su información de ocupación
     */
    @Transactional(readOnly = true)
    public List<Object[]> obtenerOcupacion(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es requerida");
        }

        return groomerRepository.ocupacionGroomer(fecha);
    }

    /**
     * Obtener tiempos promedio de trabajo de todos los groomers.
     * Utiliza el SP sp_TiemposPromedioGroomer.
     *
     * @return Lista de Object[] con su información de tiempos promedio
     */
    @Transactional(readOnly = true)
    public List<Object[]> obtenerTiemposPromedio(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son requeridas");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha fin no puede ser anterior a la fecha inicio");
        }

        return groomerRepository.tiemposPromedioGroomer(fechaInicio, fechaFin);
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
                // CORRECCIÓN: Usando .collect(Collectors.toList()) para Java 8
                .collect(Collectors.toList());
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

        if (!groomerRepository.existsById(idGroomer)) {
            throw new IllegalArgumentException("Groomer no encontrado con ID: " + idGroomer);
        }

        // CORRECCIÓN: El SP devuelve List<Object[]>
        List<Object[]> disponibles = groomerRepository.obtenerDisponibilidadGroomers(fechaInicio.toLocalDate());

        // CORRECCIÓN: Procesar el Object[] para encontrar el ID.
        return disponibles.stream()
                .anyMatch(row -> {
                    if (row[0] instanceof BigInteger) {
                        return ((BigInteger) row[0]).intValue() == idGroomer;
                    } else if (row[0] instanceof Integer) {
                        return ((Integer) row[0]).equals(idGroomer);
                    }
                    return false;
                });
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