package com.teranvet.repository;

import com.teranvet.entity.DetalleServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleServicioRepository extends JpaRepository<DetalleServicio, Integer> {
    
    List<DetalleServicio> findByAtencion_IdAtencion(Integer idAtencion);
    
    List<DetalleServicio> findByServicio_IdServicio(Integer idServicio);
    
    // ==================== STORED PROCEDURES ====================
    
    /**
     * Obtiene detalles de servicios de una atención
     * Llamada al SP: sp_DetalleServiciosAtencion
     */
    @Query(value = "CALL sp_DetalleServiciosAtencion(:p_id_atencion)", nativeQuery = true)
    List<Object[]> obtenerDetalleServiciosAtencionSP(@Param("p_id_atencion") Integer idAtencion);
}
