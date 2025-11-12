package com.teranvet.service;

import com.teranvet.entity.ConfiguracionEstimacion;
import com.teranvet.repository.ConfiguracionEstimacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
                .filter(c -> c.getIdServicio() != null && c.getIdServicio().equals(idServicio))
                .toList();
    }

    /**
     * Crear una nueva configuración.
     */
    public ConfiguracionEstimacion crear(ConfiguracionEstimacion config) {
        if (config == null) {
            throw new IllegalArgumentException("La configuración no puede ser nula");
        }
        
        if (config.getIdServicio() == null || config.getIdServicio() <= 0) {
            throw new IllegalArgumentException("El ID de servicio es requerido");
        }
        
        if (config.getTiempoEstimadoMin() == null || config.getTiempoEstimadoMin() <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser mayor a 0");
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
        
        if (configActualizada.getIdServicio() != null && configActualizada.getIdServicio() > 0) {
            configExistente.setIdServicio(configActualizada.getIdServicio());
        }
        
        if (configActualizada.getIdGroomer() != null && configActualizada.getIdGroomer() > 0) {
            configExistente.setIdGroomer(configActualizada.getIdGroomer());
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
                .filter(c -> c.getIdServicio() != null && c.getIdServicio().equals(idServicio))
                .findFirst()
                .map(ConfiguracionEstimacion::getTiempoEstimadoMin)
                .orElse(null);
    }
}
