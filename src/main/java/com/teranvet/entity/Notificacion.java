package com.teranvet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Notificacion - Registra notificaciones enviadas a clientes y usuarios
 * Mapeo a tabla: notificacion
 */
@Entity
@Table(name = "notificacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer idNotificacion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoNotificacion tipo;
    
    @Column(name = "destinatario_id", nullable = false)
    private Integer destinatarioId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false)
    private CanalNotificacion canal;
    
    @Column(name = "contenido", columnDefinition = "TEXT", nullable = false)
    private String contenido;
    
    @Column(name = "enviado_at")
    private LocalDateTime enviadoAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoNotificacion estado;
    
    @Column(name = "referencia_tipo")
    private String referenciaTipo;
    
    @Column(name = "referencia_id")
    private Integer referenciaId;
    
    // Enumeraciones
    
    public enum TipoNotificacion {
        sms, email, push
    }
    
    public enum CanalNotificacion {
        cliente, usuario
    }
    
    public enum EstadoNotificacion {
        pendiente, enviado, fallido
    }
    
    /**
     * Automáticamente registra la hora de envío
     */
    @PrePersist
    public void prePersist() {
        if (this.enviadoAt == null && this.estado == EstadoNotificacion.enviado) {
            this.enviadoAt = LocalDateTime.now();
        }
    }
}
