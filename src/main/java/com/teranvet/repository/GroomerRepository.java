package com.teranvet.repository;

import com.teranvet.entity.Groomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GroomerRepository extends JpaRepository<Groomer, Integer> {
    
    List<Groomer> findAll();
    
    List<Groomer> findByNombreContainingIgnoreCase(String nombre);
    
    // ==================== STORED PROCEDURES ====================
    
    /**
     * Obtiene todos los Groomers
     * Llamada al SP: sp_ObtenerGroomers
     */
    @Query(value = "CALL sp_ObtenerGroomers()", nativeQuery = true)
    List<Object[]> obtenerGroomersSP();
    
    /**
     * Inserta un nuevo Groomer
     * Llamada al SP: sp_InsertarGroomer
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_InsertarGroomer(:p_nombre, :p_especialidades, :p_disponibilidad)", nativeQuery = true)
    void insertarGroomer(
            @Param("p_nombre") String nombre,
            @Param("p_especialidades") String especialidades,
            @Param("p_disponibilidad") String disponibilidad);
    
    /**
     * Actualiza un Groomer existente
     * Llamada al SP: sp_ActualizarGroomer
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_ActualizarGroomer(:p_id_groomer, :p_nombre, :p_especialidades, :p_disponibilidad)", nativeQuery = true)
    void actualizarGroomer(
            @Param("p_id_groomer") Integer idGroomer,
            @Param("p_nombre") String nombre,
            @Param("p_especialidades") String especialidades,
            @Param("p_disponibilidad") String disponibilidad);
    
    /**
     * Obtiene disponibilidad de Groomers en una fecha
     * Llamada al SP: sp_ObtenerDisponibilidadGroomers
     */
    @Query(value = "CALL sp_ObtenerDisponibilidadGroomers(:p_fecha)", nativeQuery = true)
    List<Object[]> obtenerDisponibilidadGroomers(@Param("p_fecha") LocalDate fecha);
    
    /**
     * Obtiene ocupación de Groomers en una fecha
     * Llamada al SP: sp_OcupacionGroomer
     */
    @Query(value = "CALL sp_OcupacionGroomer(:p_fecha)", nativeQuery = true)
    List<Object[]> ocupacionGroomer(@Param("p_fecha") LocalDate fecha);
    
    /**
     * Obtiene tiempos promedio de Groomers en un rango de fechas
     * Llamada al SP: sp_TiemposPromedioGroomer
     */
    @Query(value = "CALL sp_TiemposPromedioGroomer(:p_fecha_inicio, :p_fecha_fin)", nativeQuery = true)
    List<Object[]> tiemposPromedioGroomer(
            @Param("p_fecha_inicio") LocalDate fechaInicio,
            @Param("p_fecha_fin") LocalDate fechaFin);
}
