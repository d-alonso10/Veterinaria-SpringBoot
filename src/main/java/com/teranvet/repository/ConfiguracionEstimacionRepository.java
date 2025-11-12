package com.teranvet.repository;

import com.teranvet.entity.ConfiguracionEstimacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionEstimacionRepository extends JpaRepository<ConfiguracionEstimacion, Integer> {
    
    List<ConfiguracionEstimacion> findByServicio_IdServicio(Integer idServicio);
    
    List<ConfiguracionEstimacion> findByGroomer_IdGroomer(Integer idGroomer);
    
    @Query("SELECT c FROM ConfiguracionEstimacion c WHERE c.servicio.idServicio = :idServicio AND c.groomer.idGroomer = :idGroomer")
    Optional<ConfiguracionEstimacion> findByServicioAndGroomer(@Param("idServicio") Integer idServicio, @Param("idGroomer") Integer idGroomer);
    
    // ==================== STORED PROCEDURES ====================
    
    /**
     * Ajusta el tiempo estimado para un servicio y groomer
     * Llamada al SP: sp_AjustarTiempoEstimado
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_AjustarTiempoEstimado(:p_id_servicio, :p_id_groomer, :p_tiempo_estimado_min)", nativeQuery = true)
    void ajustarTiempoEstimado(
            @Param("p_id_servicio") Integer idServicio,
            @Param("p_id_groomer") Integer idGroomer,
            @Param("p_tiempo_estimado_min") Integer tiempoEstimadoMin);
    
    /**
     * Obtiene estimaciones de tiempo
     * Llamada al SP: sp_ObtenerEstimacionesTiempo
     */
    @Query(value = "CALL sp_ObtenerEstimacionesTiempo()", nativeQuery = true)
    List<Object[]> obtenerEstimacionesTiempoSP();
}
