package com.teranvet.repository;

import com.teranvet.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    
    Optional<Servicio> findByCodigo(String codigo);
    
    List<Servicio> findByCategoria(Servicio.Categoria categoria);
    
    List<Servicio> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT s FROM Servicio s WHERE s.categoria = :categoria ORDER BY s.nombre")
    List<Servicio> findByCategoriaSorted(@Param("categoria") Servicio.Categoria categoria);
    
    // ==================== STORED PROCEDURES ====================
    
    /**
     * Obtiene todos los servicios
     * Llamada al SP: sp_ObtenerServicios
     */
    @Query(value = "CALL sp_ObtenerServicios()", nativeQuery = true)
    List<Object[]> obtenerServiciosSP();
    
    /**
     * Obtiene servicios por categoría
     * Llamada al SP: sp_ObtenerServiciosPorCategoria
     */
    @Query(value = "CALL sp_ObtenerServiciosPorCategoria(:p_categoria)", nativeQuery = true)
    List<Object[]> obtenerServiciosPorCategoriaSP(@Param("p_categoria") String categoria);
    
    /**
     * Inserta un nuevo Servicio
     * Llamada al SP: sp_InsertarServicio
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_InsertarServicio(:p_codigo, :p_nombre, :p_descripcion, " +
            ":p_duracion_estimada_min, :p_precio_base, :p_categoria)", nativeQuery = true)
    void insertarServicio(
            @Param("p_codigo") String codigo,
            @Param("p_nombre") String nombre,
            @Param("p_descripcion") String descripcion,
            @Param("p_duracion_estimada_min") Integer duracionEstimadaMin,
            @Param("p_precio_base") BigDecimal precioBase,
            @Param("p_categoria") String categoria);
    
    /**
     * Actualiza un Servicio existente
     * Llamada al SP: sp_ActualizarServicio
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_ActualizarServicio(:p_id_servicio, :p_codigo, :p_nombre, " +
            ":p_descripcion, :p_duracion_estimada_min, :p_precio_base, :p_categoria)", nativeQuery = true)
    void actualizarServicio(
            @Param("p_id_servicio") Integer idServicio,
            @Param("p_codigo") String codigo,
            @Param("p_nombre") String nombre,
            @Param("p_descripcion") String descripcion,
            @Param("p_duracion_estimada_min") Integer duracionEstimadaMin,
            @Param("p_precio_base") BigDecimal precioBase,
            @Param("p_categoria") String categoria);
}
