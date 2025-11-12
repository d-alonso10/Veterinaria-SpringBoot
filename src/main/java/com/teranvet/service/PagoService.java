package com.teranvet.service;

import com.teranvet.entity.Pago;
import com.teranvet.repository.FacturaRepository;
import com.teranvet.repository.PagoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio para operaciones relacionadas con Pagos
 * Integra con los Stored Procedures de la base de datos
 */
@Service
@Transactional
@Slf4j
public class PagoService {
    
    @Autowired
    private PagoRepository pagoRepository;
    
    @Autowired
    private FacturaRepository facturaRepository;
    
    /**
     * Obtiene todos los pagos
     */
    public List<Pago> obtenerTodos() {
        log.info("Obteniendo todos los pagos");
        return pagoRepository.findAll();
    }
    
    /**
     * Obtiene un pago por ID
     */
    public Pago obtenerPorId(Integer idPago) {
        log.info("Obteniendo pago con ID: {}", idPago);
        return pagoRepository.findById(idPago)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }
    
    /**
     * Obtiene pagos de una factura
     */
    public List<Pago> obtenerPorFactura(Integer idFactura) {
        log.info("Obteniendo pagos de factura: {}", idFactura);
        
        if (!facturaRepository.existsById(idFactura)) {
            throw new RuntimeException("Factura no encontrada");
        }
        
        return pagoRepository.findByFactura_IdFactura(idFactura);
    }
    
    /**
     * Obtiene pagos confirmados
     */
    public List<Pago> obtenerPagosConfirmados() {
        log.info("Obteniendo pagos confirmados");
        return pagoRepository.findPagosConfirmados();
    }
    
    /**
     * Registra un nuevo pago usando el SP
     */
    public void registrarPago(Integer idFactura, BigDecimal monto, String metodo, String referencia) {
        log.info("Registrando pago para factura: {}", idFactura);
        
        // Validar que la factura existe
        if (!facturaRepository.existsById(idFactura)) {
            throw new RuntimeException("Factura no encontrada");
        }
        
        // Validar monto
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Monto debe ser mayor a 0");
        }
        
        try {
            // Llamar al SP
            pagoRepository.registrarPago(idFactura, monto, metodo, referencia);
            log.info("Pago registrado exitosamente usando SP");
        } catch (Exception e) {
            log.error("Error al registrar pago: ", e);
            throw new RuntimeException("Error al registrar pago: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene pagos de una factura usando el SP
     */
    public List<Object[]> obtenerPagosPorFacturaSP(Integer idFactura) {
        log.info("Obteniendo pagos de factura usando SP: {}", idFactura);
        
        if (!facturaRepository.existsById(idFactura)) {
            throw new RuntimeException("Factura no encontrada");
        }
        
        try {
            return pagoRepository.obtenerPagosPorFacturaSP(idFactura);
        } catch (Exception e) {
            log.error("Error al obtener pagos: ", e);
            throw new RuntimeException("Error al obtener pagos: " + e.getMessage());
        }
    }
}
