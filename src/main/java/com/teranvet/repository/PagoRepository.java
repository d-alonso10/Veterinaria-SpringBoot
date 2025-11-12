package com.teranvet.repository;

import com.teranvet.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    
    List<Pago> findByFactura_IdFactura(Integer idFactura);
    
    @Query("SELECT p FROM Pago p WHERE p.fechaPago BETWEEN :inicio AND :fin ORDER BY p.fechaPago DESC")
    List<Pago> findPagosPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT p FROM Pago p WHERE p.estado = 'confirmado' ORDER BY p.fechaPago DESC")
    List<Pago> findPagosConfirmados();
}
