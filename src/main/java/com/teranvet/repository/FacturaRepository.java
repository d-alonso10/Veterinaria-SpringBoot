package com.teranvet.repository;

import com.teranvet.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    
    // ==================== STORED PROCEDURES ====================
    
    /**
     * Crea una nueva Factura
     * Llamada al SP: sp_CrearFactura
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_CrearFactura(:p_serie, :p_numero, :p_id_atencion, :p_metodo_pago_sugerido)", nativeQuery = true)
    void crearFactura(
            @Param("p_serie") String serie,
            @Param("p_numero") String numero,
            @Param("p_id_atencion") Integer idAtencion,
            @Param("p_metodo_pago_sugerido") String metodoPagoSugerido);
    
    /**
     * Anula una Factura existente
     * Llamada al SP: sp_AnularFactura
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_AnularFactura(:p_id_factura)", nativeQuery = true)
    void anularFactura(@Param("p_id_factura") Integer idFactura);
    
    /**
     * Obtiene facturas por cliente
     * Llamada al SP: sp_ObtenerFacturasPorCliente
     */
    @Query(value = "CALL sp_ObtenerFacturasPorCliente(:p_id_cliente)", nativeQuery = true)
    List<Object[]> obtenerFacturasPorClienteSP(@Param("p_id_cliente") Integer idCliente);
    
    /**
     * Recalcula los totales de todas las facturas
     * Llamada al SP: sp_RecalcularTotalesFacturas
     */
    @Modifying
    @Transactional
    @Query(value = "CALL sp_RecalcularTotalesFacturas()", nativeQuery = true)
    void recalcularTotalesFacturas();
}
