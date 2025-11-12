package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.Factura;
import com.teranvet.service.FacturaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para operaciones con Facturas
 * Endpoints: /api/facturas
 */
@RestController
@RequestMapping("/api/facturas")
@Slf4j
public class FacturaController {
    
    @Autowired
    private FacturaService facturaService;
    
    /**
     * GET /api/facturas
     * Obtiene todas las facturas
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Factura>>> obtenerTodas() {
        log.info("GET /api/facturas - Obteniendo todas las facturas");
        try {
            List<Factura> facturas = facturaService.obtenerTodas();
            return ResponseEntity.ok(ApiResponse.exitoso("Facturas obtenidas exitosamente", facturas));
        } catch (Exception e) {
            log.error("Error al obtener facturas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener facturas", e.getMessage()));
        }
    }
    
    /**
     * GET /api/facturas/{id}
     * Obtiene una factura por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Factura>> obtenerPorId(@PathVariable Integer id) {
        log.info("GET /api/facturas/{} - Obteniendo factura por ID", id);
        try {
            Factura factura = facturaService.obtenerPorId(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Factura obtenida exitosamente", factura));
        } catch (RuntimeException e) {
            log.warn("Factura no encontrada: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Factura no encontrada", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener factura", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener factura", e.getMessage()));
        }
    }
    
    /**
     * GET /api/facturas/cliente/{idCliente}
     * Obtiene todas las facturas de un cliente
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<ApiResponse<List<Factura>>> obtenerPorCliente(@PathVariable Integer idCliente) {
        log.info("GET /api/facturas/cliente/{} - Obteniendo facturas del cliente", idCliente);
        try {
            List<Factura> facturas = facturaService.obtenerPorCliente(idCliente);
            return ResponseEntity.ok(ApiResponse.exitoso("Facturas del cliente obtenidas", facturas));
        } catch (RuntimeException e) {
            log.warn("Cliente no encontrado: {}", idCliente);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Cliente no encontrado", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener facturas del cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener facturas", e.getMessage()));
        }
    }
    
    /**
     * POST /api/facturas
     * Crea una nueva factura
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> crear(
            @RequestParam Integer idAtencion,
            @RequestParam String serie,
            @RequestParam String numero,
            @RequestParam String metodoPagoSugerido) {
        log.info("POST /api/facturas - Creando nueva factura");
        try {
            facturaService.crearFactura(idAtencion, serie, numero, metodoPagoSugerido);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Factura creada exitosamente", "Factura registrada en la BD"));
        } catch (RuntimeException e) {
            log.warn("Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Validación fallida", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al crear factura", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear factura", e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/facturas/{id}
     * Anula una factura
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> anular(@PathVariable Integer id) {
        log.info("DELETE /api/facturas/{} - Anulando factura", id);
        try {
            facturaService.anularFactura(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Factura anulada exitosamente", "Factura marcada como anulada"));
        } catch (RuntimeException e) {
            log.warn("Factura no encontrada: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Factura no encontrada", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al anular factura", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al anular factura", e.getMessage()));
        }
    }
    
    /**
     * POST /api/facturas/recalcular
     * Recalcula los totales de todas las facturas
     */
    @PostMapping("/recalcular")
    public ResponseEntity<ApiResponse<String>> recalcularTotales() {
        log.info("POST /api/facturas/recalcular - Recalculando totales");
        try {
            facturaService.recalcularTotalesFacturas();
            return ResponseEntity.ok(ApiResponse.exitoso("Totales recalculados", "Todos los totales han sido recalculados"));
        } catch (Exception e) {
            log.error("Error al recalcular totales", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al recalcular", e.getMessage()));
        }
    }
}
