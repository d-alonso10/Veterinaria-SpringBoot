package com.teranvet.service;

import com.teranvet.entity.ConfiguracionEstimacion;
// Se asume que también tienes importadas las entidades Servicio y Groomer
import com.teranvet.entity.Servicio;
import com.teranvet.entity.Groomer;
import com.teranvet.repository.ConfiguracionEstimacionRepository;
// Se asume que tienes los repositorios de Servicio y Groomer para el método 'actualizar'
import com.teranvet.repository.ServicioRepository;
import com.teranvet.repository.GroomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // <--- IMPORT REQUERIDO

/**
 * Servicio para gestión de Configuraciones de Estimación.
 */
@Service
@Transactional
public class ConfiguracionService {

    @Autowired
    private ConfiguracionEstimacionRepository configRepository;

    // --- DEPENDENCIAS ADICIONALES REQUERIDAS ---
    // Necesarias para el método 'actualizar' y 'crear' para buscar (fetch) las entidades por ID.
    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private GroomerRepository groomerRepository;

    /**
     * Obtener todas las configuraciones.
     */
    @Transactional(readOnly = true)
    public List<ConfiguracionEstimacion> obtenerTodas() {
        return configRepository.findAll();
    }

    /**
     * Obtener una configuración por ID.
     */
    @Transactional(readOnly = true)
    public Optional<ConfiguracionEstimacion> obtenerPorId(Integer idConfig) {
        if (idConfig == null || idConfig <= 0) {
            throw new IllegalArgumentException("ID de configuración inválido");
        }
        return configRepository.findById(idConfig);
    }

    /**
     * Obtener configuraciones por servicio.
     */
    @Transactional(readOnly = true)
    public List<ConfiguracionEstimacion> obtenerPorServicio(Integer idServicio) {
        if (idServicio == null || idServicio <= 0) {
            throw new IllegalArgumentException("ID de servicio inválido");
        }
        return configRepository.findAll().stream()
                // CORREGIDO: El getter de Lombok para 'idServicio' es 'getIdServicio()'
                .filter(c -> c.getServicio() != null && c.getServicio().getIdServicio().equals(idServicio))
                // CORREGIDO: Usar .collect(Collectors.toList()) para Java 8
                .collect(Collectors.toList());
    }

    /**
     * Crear una nueva configuración.
     * MODIFICADO: Ahora acepta IDs y busca las entidades.
     */
    public ConfiguracionEstimacion crear(ConfiguracionEstimacion config) {
        if (config == null) {
            throw new IllegalArgumentException("La configuración no puede ser nula");
        }

        // CORREGIDO: Validar el ID a través del objeto Servicio
        if (config.getServicio() == null || config.getServicio().getIdServicio() == null || config.getServicio().getIdServicio() <= 0) {
            throw new IllegalArgumentException("El ID de servicio es requerido");
        }

        if (config.getTiempoEstimadoMin() == null || config.getTiempoEstimadoMin() <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser mayor a 0");
        }

        // --- MEJORA DE ROBUSTEZ ---
        // Asegurarse de que las entidades estén gestionadas por JPA antes de guardar.

        // 1. Buscar el Servicio
        Servicio servicio = servicioRepository.findById(config.getServicio().getIdServicio())
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + config.getServicio().getIdServicio()));
        config.setServicio(servicio); // Asignar la entidad gestionada

        // 2. Buscar el Groomer (si existe)
        if (config.getGroomer() != null && config.getGroomer().getIdGroomer() != null) {
            Groomer groomer = groomerRepository.findById(config.getGroomer().getIdGroomer())
                    .orElseThrow(() -> new IllegalArgumentException("Groomer no encontrado con ID: " + config.getGroomer().getIdGroomer()));
            config.setGroomer(groomer); // Asignar la entidad gestionada
        }

        return configRepository.save(config);
    }


    /**
     * Actualizar una configuración existente.
     */
    public ConfiguracionEstimacion actualizar(Integer idConfig, ConfiguracionEstimacion configActualizada) {
        if (idConfig == null || idConfig <= 0) {
            throw new IllegalArgumentException("ID de configuración inválido");
        }

        ConfiguracionEstimacion configExistente = configRepository.findById(idConfig)
                .orElseThrow(() -> new IllegalArgumentException("Configuración no encontrada con ID: " + idConfig));

        // CORREGIDO: Usar setServicio() y getServicio().getIdServicio()
        if (configActualizada.getServicio() != null && configActualizada.getServicio().getIdServicio() != null && configActualizada.getServicio().getIdServicio() > 0) {
            // Buscar la entidad Servicio para asignarla
            Servicio servicio = servicioRepository.findById(configActualizada.getServicio().getIdServicio())
                    .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + configActualizada.getServicio().getIdServicio()));
            configExistente.setServicio(servicio);
        }

        // CORREGIDO: Usar setGroomer() y getGroomer().getIdGroomer()
        if (configActualizada.getGroomer() != null && configActualizada.getGroomer().getIdGroomer() != null && configActualizada.getGroomer().getIdGroomer() > 0) {
            // Buscar la entidad Groomer para asignarla
            Groomer groomer = groomerRepository.findById(configActualizada.getGroomer().getIdGroomer())
                    .orElseThrow(() -> new IllegalArgumentException("Groomer no encontrado con ID: " + configActualizada.getGroomer().getIdGroomer()));
            configExistente.setGroomer(groomer);
        }

        if (configActualizada.getTiempoEstimadoMin() != null && configActualizada.getTiempoEstimadoMin() > 0) {
            configExistente.setTiempoEstimadoMin(configActualizada.getTiempoEstimadoMin());
        }

        return configRepository.save(configExistente);
    }

    /**
     * Eliminar una configuración.
     */
    public void eliminar(Integer idConfig) {
        if (idConfig == null || idConfig <= 0) {
            throw new IllegalArgumentException("ID de configuración inválido");
        }

        if (!configRepository.existsById(idConfig)) {
            throw new IllegalArgumentException("Configuración no encontrada con ID: " + idConfig);
        }

        configRepository.deleteById(idConfig);
    }

    /**
     * Obtener tiempo estimado para un servicio.
     */
    @Transactional(readOnly = true)
    public Integer obtenerTiempoEstimado(Integer idServicio) {
        if (idServicio == null || idServicio <= 0) {
            throw new IllegalArgumentException("ID de servicio inválido");
        }

        return configRepository.findAll().stream()
                // CORREGIDO: El getter de Lombok para 'idServicio' es 'getIdServicio()'
                .filter(c -> c.getServicio() != null && c.getServicio().getIdServicio().equals(idServicio))
                .findFirst()
                .map(ConfiguracionEstimacion::getTiempoEstimadoMin)
                .orElse(null); // Devuelve null si no se encuentra, o un valor por defecto si prefieres
    }
}