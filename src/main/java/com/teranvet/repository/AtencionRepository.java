package com.teranvet.repository;

import com.teranvet.entity.Atencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AtencionRepository extends JpaRepository<Atencion, Integer> {
    
    List<Atencion> findByMascota_IdMascota(Integer idMascota);
    
    List<Atencion> findByCliente_IdCliente(Integer idCliente);
    
    List<Atencion> findBySucursal_IdSucursal(Integer idSucursal);
    
    @Query("SELECT a FROM Atencion a WHERE a.sucursal.idSucursal = :idSucursal AND a.estado IN ('en_espera', 'en_servicio') ORDER BY a.prioridad DESC, a.tiempoEstimadoInicio ASC")
    List<Atencion> findColaActual(@Param("idSucursal") Integer idSucursal);
    
    @Query("SELECT a FROM Atencion a WHERE DATE(a.createdAt) = :fecha ORDER BY a.createdAt DESC")
    List<Atencion> findAtencionesDelDia(@Param("fecha") LocalDate fecha);
}
