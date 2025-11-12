package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para Reportes.
 * Endpoints: /api/reportes
 * Proporciona reportes detallados de operaciones del negocio.
 */
@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    /**
     * GET /api/reportes/ingresos
     * Obtener reporte de ingresos.
     */
    @GetMapping("/ingresos")
    public ResponseEntity<ApiResponse<List<Map>>> reporteIngresos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam Integer idSucursal) {
        try {
            List<Map> reporte = reporteService.reporteIngresos(fechaInicio, fechaFin, idSucursal);
            return ResponseEntity.ok(ApiResponse.exitoso("Reporte de ingresos generado correctamente", reporte));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al generar reporte: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reportes/clientes-frecuentes
     * Obtener reporte de clientes frecuentes.
     */
    @GetMapping("/clientes-frecuentes")
    public ResponseEntity<ApiResponse<List<Map>>> reporteClientesFrecuentes() {
        try {
            List<Map> reporte = reporteService.reporteClientesFrecuentes();
            return ResponseEntity.ok(ApiResponse.exitoso("Reporte de clientes frecuentes generado correctamente", reporte));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al generar reporte: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reportes/servicios-mas-solicitados
     * Obtener reporte de servicios más solicitados.
     */
    @GetMapping("/servicios-mas-solicitados")
    public ResponseEntity<ApiResponse<List<Map>>> reporteServiciosMasSolicitados(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            List<Map> reporte = reporteService.reporteServiciosMasSolicitados(fechaInicio, fechaFin);
            return ResponseEntity.ok(ApiResponse.exitoso("Reporte de servicios más solicitados generado correctamente", reporte));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al generar reporte: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reportes/facturas-cliente/{idCliente}
     * Obtener reporte de facturas por cliente.
     */
    @GetMapping("/facturas-cliente/{idCliente}")
    public ResponseEntity<ApiResponse<List<Map>>> reporteFacturasPorCliente(@PathVariable Integer idCliente) {
        try {
            List<Map> reporte = reporteService.reporteFacturasPorCliente(idCliente);
            return ResponseEntity.ok(ApiResponse.exitoso("Reporte de facturas generado correctamente", reporte));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al generar reporte: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reportes/pagos-factura/{idFactura}
     * Obtener reporte de pagos por factura.
     */
    @GetMapping("/pagos-factura/{idFactura}")
    public ResponseEntity<ApiResponse<List<Map>>> reportePagosPorFactura(@PathVariable Integer idFactura) {
        try {
            List<Map> reporte = reporteService.reportePagosPorFactura(idFactura);
            return ResponseEntity.ok(ApiResponse.exitoso("Reporte de pagos generado correctamente", reporte));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al generar reporte: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reportes/auditoria
     * Obtener reporte de logs de auditoría.
     */
    @GetMapping("/auditoria")
    public ResponseEntity<ApiResponse<List<Map>>> reporteLogsAuditoria(
            @RequestParam(defaultValue = "100") Integer limite,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) String accion) {
        try {
            List<Map> reporte = reporteService.reporteLogsAuditoria(limite, entidad, accion);
            return ResponseEntity.ok(ApiResponse.exitoso("Reporte de auditoría generado correctamente", reporte));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al generar reporte: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reportes/resumen-general
     * Obtener resumen general del negocio.
     */
    @GetMapping("/resumen-general")
    public ResponseEntity<ApiResponse<Map<String, Object>>> reporteResumenGeneral(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            Map<String, Object> reporte = reporteService.obtenerResumeGeneral(fechaInicio, fechaFin);
            return ResponseEntity.ok(ApiResponse.exitoso("Resumen general generado correctamente", reporte));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al generar reporte: " + e.getMessage()));
        }
    }
}
