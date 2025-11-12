package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.Pago;
import com.teranvet.service.PagoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para operaciones con Pagos
 * Endpoints: /api/pagos
 */
@RestController
@RequestMapping("/api/pagos")
@Slf4j
public class PagoController {
    
    @Autowired
    private PagoService pagoService;
    
    /**
     * GET /api/pagos
     * Obtiene todos los pagos
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Pago>>> obtenerTodos() {
        log.info("GET /api/pagos - Obteniendo todos los pagos");
        try {
            List<Pago> pagos = pagoService.obtenerTodos();
            return ResponseEntity.ok(ApiResponse.exitoso("Pagos obtenidos exitosamente", pagos));
        } catch (Exception e) {
            log.error("Error al obtener pagos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener pagos", e.getMessage()));
        }
    }
    
    /**
     * GET /api/pagos/{id}
     * Obtiene un pago por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Pago>> obtenerPorId(@PathVariable Integer id) {
        log.info("GET /api/pagos/{} - Obteniendo pago por ID", id);
        try {
            Pago pago = pagoService.obtenerPorId(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Pago obtenido exitosamente", pago));
        } catch (RuntimeException e) {
            log.warn("Pago no encontrado: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Pago no encontrado", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener pago", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener pago", e.getMessage()));
        }
    }
    
    /**
     * GET /api/pagos/factura/{idFactura}
     * Obtiene los pagos de una factura
     */
    @GetMapping("/factura/{idFactura}")
    public ResponseEntity<ApiResponse<List<Pago>>> obtenerPorFactura(@PathVariable Integer idFactura) {
        log.info("GET /api/pagos/factura/{} - Obteniendo pagos de factura", idFactura);
        try {
            List<Pago> pagos = pagoService.obtenerPorFactura(idFactura);
            return ResponseEntity.ok(ApiResponse.exitoso("Pagos de factura obtenidos", pagos));
        } catch (RuntimeException e) {
            log.warn("Factura no encontrada: {}", idFactura);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Factura no encontrada", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener pagos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener pagos", e.getMessage()));
        }
    }
    
    /**
     * GET /api/pagos/confirmados
     * Obtiene todos los pagos confirmados
     */
    @GetMapping("/confirmados")
    public ResponseEntity<ApiResponse<List<Pago>>> obtenerPagosConfirmados() {
        log.info("GET /api/pagos/confirmados - Obteniendo pagos confirmados");
        try {
            List<Pago> pagos = pagoService.obtenerPagosConfirmados();
            return ResponseEntity.ok(ApiResponse.exitoso("Pagos confirmados obtenidos", pagos));
        } catch (Exception e) {
            log.error("Error al obtener pagos confirmados", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener pagos", e.getMessage()));
        }
    }
    
    /**
     * POST /api/pagos
     * Registra un nuevo pago
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> registrar(
            @RequestParam Integer idFactura,
            @RequestParam BigDecimal monto,
            @RequestParam String metodo,
            @RequestParam(required = false) String referencia) {
        log.info("POST /api/pagos - Registrando nuevo pago");
        try {
            pagoService.registrarPago(idFactura, monto, metodo, referencia);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Pago registrado exitosamente", "Pago confirmado"));
        } catch (RuntimeException e) {
            log.warn("Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Validación fallida", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al registrar pago", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al registrar pago", e.getMessage()));
        }
    }
}
