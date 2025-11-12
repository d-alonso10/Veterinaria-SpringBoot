package com.teranvet.repository;

import com.teranvet.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    
    List<Pago> findByFactura_IdFactura(Integer idFactura);
    
    @Query("SELECT p FROM Pago p WHERE p.fechaPago BETWEEN :inicio AND :fin ORDER BY p.fechaPago DESC")
    List<Pago> findPagosPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT p FROM Pago p WHERE p.estado = 'confirmado' ORDER BY p.fechaPago DESC")
    List<Pago> findPagosConfirmados();
    
    // ==================== STORED PROCEDURES ====================
    
    /**
     * Registra un nuevo Pago
     * Llamada al SP: sp_RegistrarPago
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_RegistrarPago(:p_id_factura, :p_monto, :p_metodo, :p_referencia)", nativeQuery = true)
    void registrarPago(
            @Param("p_id_factura") Integer idFactura,
            @Param("p_monto") BigDecimal monto,
            @Param("p_metodo") String metodo,
            @Param("p_referencia") String referencia);
    
    /**
     * Obtiene pagos por factura
     * Llamada al SP: sp_ObtenerPagosPorFactura
     */
    @Query(value = "CALL sp_ObtenerPagosPorFactura(:p_id_factura)", nativeQuery = true)
    List<Object[]> obtenerPagosPorFacturaSP(@Param("p_id_factura") Integer idFactura);
}
