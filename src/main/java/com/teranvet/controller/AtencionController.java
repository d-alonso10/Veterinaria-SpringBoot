package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.Atencion;
import com.teranvet.service.AtencionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/atenciones")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class AtencionController {
    
    @Autowired
    private AtencionService atencionService;
    
    /**
     * GET /api/atenciones - Obtener todas las atenciones
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Atencion>>> obtenerTodas() {
        try {
            log.info("GET /atenciones - Obteniendo todas las atenciones");
            List<Atencion> atenciones = atencionService.obtenerTodas();
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Atenciones obtenidas exitosamente", atenciones)
            );
        } catch (Exception e) {
            log.error("Error al obtener atenciones", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener atenciones", e.getMessage()));
        }
    }
    
    /**
     * GET /api/atenciones/{id} - Obtener atención por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Atencion>> obtenerPorId(@PathVariable Integer id) {
        try {
            log.info("GET /atenciones/{} - Obteniendo atención", id);
            Atencion atencion = atencionService.obtenerPorId(id);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Atención obtenida exitosamente", atencion)
            );
        } catch (Exception e) {
            log.error("Error al obtener atención", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Atención no encontrada", e.getMessage()));
        }
    }
    
    /**
     * GET /api/atenciones/cola/{idSucursal} - Obtener cola actual
     */
    @GetMapping("/cola/{idSucursal}")
    public ResponseEntity<ApiResponse<List<Atencion>>> obtenerColaActual(@PathVariable Integer idSucursal) {
        try {
            log.info("GET /atenciones/cola/{} - Obteniendo cola actual", idSucursal);
            List<Atencion> cola = atencionService.obtenerColaActual(idSucursal);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Cola obtenida exitosamente", cola)
            );
        } catch (Exception e) {
            log.error("Error al obtener cola", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Error al obtener cola", e.getMessage()));
        }
    }
    
    /**
     * GET /api/atenciones/cliente/{idCliente} - Atenciones de un cliente
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<ApiResponse<List<Atencion>>> obtenerPorCliente(@PathVariable Integer idCliente) {
        try {
            log.info("GET /atenciones/cliente/{} - Obteniendo atenciones del cliente", idCliente);
            List<Atencion> atenciones = atencionService.obtenerPorCliente(idCliente);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Atenciones del cliente obtenidas", atenciones)
            );
        } catch (Exception e) {
            log.error("Error al obtener atenciones del cliente", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Error al obtener atenciones", e.getMessage()));
        }
    }
    
    /**
     * POST /api/atenciones/desde-cita - Crear atención desde cita
     */
    @PostMapping("/desde-cita")
    public ResponseEntity<ApiResponse<Atencion>> crearDesdeCita(
            @RequestParam Integer idCita,
            @RequestParam Integer idGroomer,
            @RequestParam Integer idSucursal,
            @RequestParam LocalDateTime tiempoEstimadoInicio,
            @RequestParam LocalDateTime tiempoEstimadoFin) {
        try {
            log.info("POST /atenciones/desde-cita - Creando atención desde cita: {}", idCita);
            Atencion atencion = atencionService.crearDesdeCita(
                    idCita, idGroomer, idSucursal, tiempoEstimadoInicio, tiempoEstimadoFin
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Atención creada exitosamente", atencion));
        } catch (Exception e) {
            log.error("Error al crear atención desde cita", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al crear atención", e.getMessage()));
        }
    }
    
    /**
     * POST /api/atenciones/walk-in - Crear atención walk-in
     */
    @PostMapping("/walk-in")
    public ResponseEntity<ApiResponse<Atencion>> crearWalkIn(
            @RequestParam Integer idMascota,
            @RequestParam Integer idCliente,
            @RequestParam Integer idGroomer,
            @RequestParam Integer idSucursal,
            @RequestParam LocalDateTime tiempoEstimadoInicio,
            @RequestParam LocalDateTime tiempoEstimadoFin,
            @RequestParam(required = false) String observaciones) {
        try {
            log.info("POST /atenciones/walk-in - Creando atención walk-in");
            Atencion atencion = atencionService.crearWalkIn(
                    idMascota, idCliente, idGroomer, idSucursal, 
                    tiempoEstimadoInicio, tiempoEstimadoFin, observaciones
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Atención walk-in creada exitosamente", atencion));
        } catch (Exception e) {
            log.error("Error al crear atención walk-in", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al crear atención", e.getMessage()));
        }
    }
    
    /**
     * PUT /api/atenciones/{id}/estado - Cambiar estado
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<Atencion>> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam String nuevoEstado) {
        try {
            log.info("PUT /atenciones/{}/estado - Cambiando a: {}", id, nuevoEstado);
            Atencion actualizada = atencionService.actualizarEstado(id, nuevoEstado);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Estado actualizado exitosamente", actualizada)
            );
        } catch (Exception e) {
            log.error("Error al cambiar estado", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al cambiar estado", e.getMessage()));
        }
    }
    
    /**
     * PUT /api/atenciones/{id}/terminar - Terminar atención
     */
    @PutMapping("/{id}/terminar")
    public ResponseEntity<ApiResponse<Atencion>> terminar(@PathVariable Integer id) {
        try {
            log.info("PUT /atenciones/{}/terminar - Terminando atención", id);
            Atencion actualizada = atencionService.terminar(id);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Atención terminada exitosamente", actualizada)
            );
        } catch (Exception e) {
            log.error("Error al terminar atención", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al terminar atención", e.getMessage()));
        }
    }
}
