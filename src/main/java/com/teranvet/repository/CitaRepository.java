package com.teranvet.repository;

import com.teranvet.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
