package com.teranvet.service;

import com.teranvet.entity.ConfiguracionEstimacion;
import com.teranvet.repository.ConfiguracionEstimacionRepository;
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
                // CORREGIDO: Llamar a getServicio().getId()
                .filter(c -> c.getServicio() != null && c.getServicio().getId().equals(idServicio))
                // CORREGIDO: Usar .collect(Collectors.toList()) para Java 8
                .collect(Collectors.toList());
    }

    /**
     * Crear una nueva configuración.
     */
    public ConfiguracionEstimacion crear(ConfiguracionEstimacion config) {
        if (config == null) {
            throw new IllegalArgumentException("La configuración no puede ser nula");
        }

        // CORREGIDO: Validar el ID a través del objeto Servicio
        if (config.getServicio() == null || config.getServicio().getId() == null || config.getServicio().getId() <= 0) {
            throw new IllegalArgumentException("El ID de servicio es requerido");
        }

        if (config.getTiempoEstimadoMin() == null || config.getTiempoEstimadoMin() <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser mayor a 0");
        }

        // Aquí se asume que los objetos Servicio y Groomer ya vienen adjuntos.
        // Si no, necesitarías obtenerlos (fetch) de sus repositorios primero.
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

        // CORREGIDO: Usar setServicio() y getServicio()
        if (configActualizada.getServicio() != null && configActualizada.getServicio().getId() != null && configActualizada.getServicio().getId() > 0) {
            configExistente.setServicio(configActualizada.getServicio());
        }

        // CORREGIDO: Usar setGroomer() y getGroomer()
        if (configActualizada.getGroomer() != null && configActualizada.getGroomer().getId() != null && configActualizada.getGroomer().getId() > 0) {
            configExistente.setGroomer(configActualizada.getGroomer());
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
                // CORREGIDO: Llamar a getServicio().getId()
                .filter(c -> c.getServicio() != null && c.getServicio().getId().equals(idServicio))
                .findFirst()
                .map(ConfiguracionEstimacion::getTiempoEstimadoMin)
                .orElse(null); // Devuelve null si no se encuentra, o un valor por defecto si prefieres
    }
}