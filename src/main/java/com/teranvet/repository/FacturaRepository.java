package com.teranvet.repository;

import com.teranvet.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    
    List<Factura> findByCliente_IdCliente(Integer idCliente);
    
    List<Factura> findByNumero(String numero);
    
    @Query("SELECT f FROM Factura f WHERE f.fechaEmision BETWEEN :inicio AND :fin")
    List<Factura> findFacturasPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT f FROM Factura f WHERE f.cliente.idCliente = :idCliente ORDER BY f.fechaEmision DESC")
    List<Factura> findFacturasPorCliente(@Param("idCliente") Integer idCliente);
}
