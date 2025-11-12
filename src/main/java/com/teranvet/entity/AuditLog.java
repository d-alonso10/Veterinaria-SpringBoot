package com.teranvet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad AuditLog - Registro de auditoría para cambios en la base de datos
 * Mapeo a tabla: audit_log
 */
@Entity
@Table(name = "audit_log", indexes = {
        @Index(name = "idx_entidad", columnList = "entidad"),
        @Index(name = "idx_accion", columnList = "accion"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Integer idLog;
    
    @Column(name = "entidad", nullable = false)
    private String entidad;
    
    @Column(name = "entidad_id", nullable = false)
    private Integer entidadId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "accion", nullable = false)
    private Accion accion;
    
    @Column(name = "id_usuario")
    private Integer idUsuario;
    
    @Column(name = "antes", columnDefinition = "JSON")
    private String antes;
    
    @Column(name = "despues", columnDefinition = "JSON")
    private String despues;
    
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    // Enumeraciones
    
    public enum Accion {
        INSERT, UPDATE, DELETE
    }
    
    /**
     * Automáticamente registra la hora de creación
     */
    @PrePersist
    public void prePersist() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
