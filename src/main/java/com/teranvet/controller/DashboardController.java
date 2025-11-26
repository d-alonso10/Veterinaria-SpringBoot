package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.dto.MetricasDashboardDTO;
import com.teranvet.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para operaciones del Dashboard.
 * Expone endpoints para métricas, estadísticas y reportes del dashboard.
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * GET /api/dashboard/metricas
     * Obtener métricas generales del dashboard.
     * 
     * @param fechaInicio Fecha de inicio del período (default: 2025-01-01)
     * @param fechaFin    Fecha de fin del período (default: hoy)
     * @return MetricasDashboardDTO con 5 métricas: total_clientes, total_mascotas,
     *         citas_hoy, ingresos_periodo, atenciones_en_curso
     */
    @GetMapping("/metricas")
    public ResponseEntity<ApiResponse<MetricasDashboardDTO>> obtenerMetricas(
            @RequestParam(defaultValue = "2025-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            // Si no se proporciona fechaFin, el Service usará LocalDate.now()
            LocalDate fin = fechaFin != null ? fechaFin : LocalDate.now();

            MetricasDashboardDTO metricas = dashboardService.obtenerMetricas(fechaInicio, fin);
            return ResponseEntity.ok(ApiResponse.exitoso("Métricas obtenidas correctamente", metricas));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener métricas: " + e.getMessage()));
        }
    }

    /**
     * GET /api/dashboard/cola/{idSucursal}
     * Obtener cola actual en una sucursal.
     */
    @GetMapping("/cola/{idSucursal}")
    public ResponseEntity<ApiResponse<List<Map>>> obtenerColaActual(@PathVariable Integer idSucursal) {
        try {
            List<Map> cola = dashboardService.obtenerColaActual(idSucursal);
            return ResponseEntity.ok(ApiResponse.exitoso("Cola obtenida correctamente", cola));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener cola: " + e.getMessage()));
        }
    }

    /**
     * GET /api/dashboard/estadisticas-mensuales
     * Obtener estadísticas mensuales.
     */
    @GetMapping("/estadisticas-mensuales")
    public ResponseEntity<ApiResponse<List<Map>>> obtenerEstadisticasMensuales(
            @RequestParam Integer anio,
            @RequestParam Integer mes) {
        try {
            List<Map> estadisticas = dashboardService.obtenerEstadisticasMensuales(anio, mes);
            return ResponseEntity
                    .ok(ApiResponse.exitoso("Estadísticas mensuales obtenidas correctamente", estadisticas));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener estadísticas: " + e.getMessage()));
        }
    }

    /**
     * GET /api/dashboard/proximas-citas/{idCliente}
     * Obtener próximas citas de un cliente.
     */
    @GetMapping("/proximas-citas/{idCliente}")
    public ResponseEntity<ApiResponse<List<Map>>> obtenerProximasCitas(@PathVariable Integer idCliente) {
        try {
            List<Map> citas = dashboardService.obtenerProximasCitas(idCliente);
            return ResponseEntity.ok(ApiResponse.exitoso("Próximas citas obtenidas correctamente", citas));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener próximas citas: " + e.getMessage()));
        }
    }

    /**
     * GET /api/dashboard/historial-mascota/{idMascota}
     * Obtener historial de servicios de una mascota.
     */
    @GetMapping("/historial-mascota/{idMascota}")
    public ResponseEntity<ApiResponse<List<Map>>> obtenerHistorialMascota(@PathVariable Integer idMascota) {
        try {
            List<Map> historial = dashboardService.obtenerHistorialMascota(idMascota);
            return ResponseEntity.ok(ApiResponse.exitoso("Historial obtenido correctamente", historial));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener historial: " + e.getMessage()));
        }
    }
}
