package com.teranvet.repository;

import com.teranvet.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    
    List<Notificacion> findByDestinatarioId(Integer destinatarioId);
    
    List<Notificacion> findByEstado(Notificacion.EstadoNotificacion estado);
    
    @Query("SELECT n FROM Notificacion n WHERE n.destinatarioId = :destinatarioId ORDER BY n.enviadoAt DESC")
    List<Notificacion> findByDestinatarioIdOrderByEnviadoDesc(@Param("destinatarioId") Integer destinatarioId);
    
    // ==================== STORED PROCEDURES ====================
    
    /**
     * Registra una nueva notificación
     * Llamada al SP: sp_RegistrarNotificacion
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_RegistrarNotificacion(:p_tipo, :p_destinatario_id, :p_canal, " +
            ":p_contenido, :p_referencia_tipo, :p_referencia_id)", nativeQuery = true)
    void registrarNotificacion(
            @Param("p_tipo") String tipo,
            @Param("p_destinatario_id") Integer destinatarioId,
            @Param("p_canal") String canal,
            @Param("p_contenido") String contenido,
            @Param("p_referencia_tipo") String referenciaTipo,
            @Param("p_referencia_id") Integer referenciaId);
    
    /**
     * Obtiene notificaciones de un cliente
     * Llamada al SP: sp_ObtenerNotificacionesCliente
     */
    @Query(value = "CALL sp_ObtenerNotificacionesCliente(:p_destinatario_id, :p_limite)", nativeQuery = true)
    List<Object[]> obtenerNotificacionesClienteSP(
            @Param("p_destinatario_id") Integer destinatarioId,
            @Param("p_limite") Integer limite);
}
