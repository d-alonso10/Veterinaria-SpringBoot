package com.teranvet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Entidad ConfiguracionEstimacion - Almacena estimaciones de tiempo personalizadas
 * Mapeo a tabla: configuracion_estimacion
 */
@Entity
@Table(name = "configuracion_estimacion", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_servicio", "id_groomer"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionEstimacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_config")
    private Integer idConfig;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_groomer", nullable = false)
    private Groomer groomer;
    
    @Column(name = "tiempo_estimado_min", nullable = false)
    private Integer tiempoEstimadoMin;
    
    /**
     * Constructor auxiliar con IDs
     */
    public ConfiguracionEstimacion(Integer idServicio, Integer idGroomer, Integer tiempoEstimadoMin) {
        this.idConfig = null;
        this.tiempoEstimadoMin = tiempoEstimadoMin;
    }
}
