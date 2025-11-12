package com.teranvet.repository;

import com.teranvet.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    
    List<Cita> findByCliente_IdCliente(Integer idCliente);
    
    List<Cita> findByMascota_IdMascota(Integer idMascota);
    
    @Query("SELECT c FROM Cita c WHERE c.cliente.idCliente = :idCliente AND c.estado IN ('reservada', 'confirmada') ORDER BY c.fechaProgramada ASC")
    List<Cita> findProximasCitas(@Param("idCliente") Integer idCliente);
    
    @Query("SELECT c FROM Cita c WHERE c.fechaProgramada BETWEEN :inicio AND :fin ORDER BY c.fechaProgramada ASC")
    List<Cita> findCitasPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    // ==================== STORED PROCEDURES ====================
    
    /**
     * Crea una nueva Cita
     * Llamada al SP: sp_CrearCita
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_CrearCita(:idMascota, :idCliente, :idSucursal, :idServicio, " +
            ":fechaProgramada, :modalidad, :notas)", nativeQuery = true)
    void crearCita(
            @Param("idMascota") Integer idMascota,
            @Param("idCliente") Integer idCliente,
            @Param("idSucursal") Integer idSucursal,
            @Param("idServicio") Integer idServicio,
            @Param("fechaProgramada") LocalDateTime fechaProgramada,
            @Param("modalidad") String modalidad,
            @Param("notas") String notas);
    
    /**
     * Reprograma una Cita existente
     * Llamada al SP: sp_ReprogramarCita
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_ReprogramarCita(:idCita, :nuevaFecha)", nativeQuery = true)
    void reprogramarCita(
            @Param("idCita") Integer idCita,
            @Param("nuevaFecha") LocalDateTime nuevaFecha);
    
    /**
     * Cancela una Cita existente
     * Llamada al SP: sp_CancelarCita
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_CancelarCita(:idCita)", nativeQuery = true)
    void cancelarCita(@Param("idCita") Integer idCita);
    
    /**
     * Confirma la asistencia de una Cita
     * Llamada al SP: sp_ConfirmarAsistenciaCita
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_ConfirmarAsistenciaCita(:idCita)", nativeQuery = true)
    void confirmarAsistenciaCita(@Param("idCita") Integer idCita);
    
    /**
     * Obtiene las próximas citas de un cliente
     * Llamada al SP: sp_ObtenerProximasCitas
     */
    @Query(value = "CALL sp_ObtenerProximasCitas(:idCliente)", nativeQuery = true)
    List<Object[]> obtenerProximasCitasSP(@Param("idCliente") Integer idCliente);
}
