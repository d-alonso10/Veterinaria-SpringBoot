package com.teranvet.service;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.Factura;
import com.teranvet.repository.AtencionRepository;
import com.teranvet.repository.ClienteRepository;
import com.teranvet.repository.FacturaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para operaciones relacionadas con Facturas
 * Integra con los Stored Procedures de la base de datos
 */
@Service
@Transactional
@Slf4j
public class FacturaService {
    
    @Autowired
    private FacturaRepository facturaRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private AtencionRepository atencionRepository;
    
    /**
     * Obtiene todas las facturas
     */
    public List<Factura> obtenerTodas() {
        log.info("Obteniendo todas las facturas");
        return facturaRepository.findAll();
    }
    
    /**
     * Obtiene una factura por ID
     */
    public Factura obtenerPorId(Integer idFactura) {
        log.info("Obteniendo factura con ID: {}", idFactura);
        return facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
    }
    
    /**
     * Obtiene facturas de un cliente
     */
    public List<Factura> obtenerPorCliente(Integer idCliente) {
        log.info("Obteniendo facturas del cliente: {}", idCliente);
        
        if (!clienteRepository.existsById(idCliente)) {
            throw new RuntimeException("Cliente no encontrado");
        }
        
        return facturaRepository.findByCliente_IdCliente(idCliente);
    }
    
    /**
     * Crea una nueva factura usando el SP
     */
    public void crearFactura(Integer idAtencion, String serie, String numero, String metodoPagoSugerido) {
        log.info("Creando factura para atención: {}", idAtencion);
        
        // Validar que la atención existe
        if (!atencionRepository.existsById(idAtencion)) {
            throw new RuntimeException("Atención no encontrada");
        }
        
        try {
            // Llamar al SP
            facturaRepository.crearFactura(serie, numero, idAtencion, metodoPagoSugerido);
            log.info("Factura creada exitosamente usando SP");
        } catch (Exception e) {
            log.error("Error al crear factura: ", e);
            throw new RuntimeException("Error al crear factura: " + e.getMessage());
        }
    }
    
    /**
     * Anula una factura usando el SP
     */
    public void anularFactura(Integer idFactura) {
        log.info("Anulando factura: {}", idFactura);
        
        // Validar que la factura existe
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
        
        try {
            // Llamar al SP
            facturaRepository.anularFactura(idFactura);
            log.info("Factura anulada exitosamente usando SP");
        } catch (Exception e) {
            log.error("Error al anular factura: ", e);
            throw new RuntimeException("Error al anular factura: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene facturas por cliente usando el SP
     */
    public List<Object[]> obtenerFacturasPorClienteSP(Integer idCliente) {
        log.info("Obteniendo facturas del cliente usando SP: {}", idCliente);
        
        if (!clienteRepository.existsById(idCliente)) {
            throw new RuntimeException("Cliente no encontrado");
        }
        
        try {
            return facturaRepository.obtenerFacturasPorClienteSP(idCliente);
        } catch (Exception e) {
            log.error("Error al obtener facturas del cliente: ", e);
            throw new RuntimeException("Error al obtener facturas: " + e.getMessage());
        }
    }
    
    /**
     * Recalcula los totales de todas las facturas
     */
    public void recalcularTotalesFacturas() {
        log.info("Recalculando totales de todas las facturas");
        
        try {
            facturaRepository.recalcularTotalesFacturas();
            log.info("Totales de facturas recalculados exitosamente");
        } catch (Exception e) {
            log.error("Error al recalcular totales: ", e);
            throw new RuntimeException("Error al recalcular totales: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene facturas en un rango de fechas
     */
    public List<Factura> obtenerPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        log.info("Obteniendo facturas entre {} y {}", inicio, fin);
        return facturaRepository.findFacturasPorFecha(inicio, fin);
    }
}
