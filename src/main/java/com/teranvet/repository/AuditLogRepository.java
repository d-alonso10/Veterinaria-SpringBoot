package com.teranvet.repository;

import com.teranvet.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    
    List<AuditLog> findByEntidad(String entidad);
    
    List<AuditLog> findByAccion(AuditLog.Accion accion);
    
    @Query("SELECT a FROM AuditLog a WHERE a.entidad = :entidad AND a.entidadId = :entidadId ORDER BY a.timestamp DESC")
    List<AuditLog> findByEntidadAndEntidadId(@Param("entidad") String entidad, @Param("entidadId") Integer entidadId);
    
    // ==================== STORED PROCEDURES ====================
    
    /**
     * Obtiene logs de auditoría con filtros
     * Llamada al SP: sp_ObtenerLogsAuditoria
     */
    @Query(value = "CALL sp_ObtenerLogsAuditoria(:p_limite, :p_entidad, :p_accion)", nativeQuery = true)
    List<Object[]> obtenerLogsAuditoriaSP(
            @Param("p_limite") Integer limite,
            @Param("p_entidad") String entidad,
            @Param("p_accion") String accion);
}
