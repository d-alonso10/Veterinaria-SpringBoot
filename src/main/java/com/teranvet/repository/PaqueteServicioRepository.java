package com.teranvet.repository;

import com.teranvet.entity.PaqueteServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaqueteServicioRepository extends JpaRepository<PaqueteServicio, Integer> {
    
    List<PaqueteServicio> findByNombreContainingIgnoreCase(String nombre);
    
    // ==================== STORED PROCEDURES ====================
    
    /**
     * Crea un nuevo Paquete de Servicio
     * Llamada al SP: sp_CrearPaqueteServicio
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_CrearPaqueteServicio(:p_nombre, :p_descripcion, :p_precio_total)", nativeQuery = true)
    void crearPaqueteServicio(
            @Param("p_nombre") String nombre,
            @Param("p_descripcion") String descripcion,
            @Param("p_precio_total") BigDecimal precioTotal);
    
    /**
     * Agrega un servicio a un paquete
     * Llamada al SP: sp_AgregarServicioPaquete
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_AgregarServicioPaquete(:p_id_paquete, :p_id_servicio, :p_cantidad)", nativeQuery = true)
    void agregarServicioPaquete(
            @Param("p_id_paquete") Integer idPaquete,
            @Param("p_id_servicio") Integer idServicio,
            @Param("p_cantidad") Integer cantidad);
}
