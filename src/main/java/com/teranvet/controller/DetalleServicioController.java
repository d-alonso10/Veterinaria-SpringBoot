package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.DetalleServicio;
import com.teranvet.service.DetalleServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de Detalles de Servicios.
 * Endpoints: /api/atenciones/{id}/detalles
 */
@RestController
@RequestMapping("/api/atenciones/{idAtencion}/detalles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DetalleServicioController {

    @Autowired
    private DetalleServicioService detalleService;

    /**
     * GET /api/atenciones/{idAtencion}/detalles
     * Obtener todos los detalles de una atención.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DetalleServicio>>> obtenerDetallesPorAtencion(@PathVariable Integer idAtencion) {
        try {
            List<DetalleServicio> detalles = detalleService.obtenerPorAtencion(idAtencion);
            return ResponseEntity.ok(ApiResponse.exitoso("Detalles obtenidos correctamente", detalles));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener detalles: " + e.getMessage()));
        }
    }

    /**
     * GET /api/atenciones/{idAtencion}/detalles/{idDetalle}
     * Obtener un detalle específico.
     */
    @GetMapping("/{idDetalle}")
    public ResponseEntity<ApiResponse<DetalleServicio>> obtenerPorId(
            @PathVariable Integer idAtencion,
            @PathVariable Integer idDetalle) {
        try {
            Optional<DetalleServicio> detalle = detalleService.obtenerPorId(idDetalle);
            if (detalle.isPresent() && detalle.get().getIdAtencion().equals(idAtencion)) {
                return ResponseEntity.ok(ApiResponse.exitoso("Detalle encontrado", detalle.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Detalle no encontrado"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener detalle: " + e.getMessage()));
        }
    }

    /**
     * POST /api/atenciones/{idAtencion}/detalles
     * Crear un nuevo detalle de servicio.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DetalleServicio>> crear(
            @PathVariable Integer idAtencion,
            @RequestBody DetalleServicio detalle) {
        try {
            detalle.setIdAtencion(idAtencion);
            DetalleServicio nuevoDetalle = detalleService.crear(detalle);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Detalle creado correctamente", nuevoDetalle));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear detalle: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/atenciones/{idAtencion}/detalles/{idDetalle}
     * Actualizar un detalle existente.
     */
    @PutMapping("/{idDetalle}")
    public ResponseEntity<ApiResponse<DetalleServicio>> actualizar(
            @PathVariable Integer idAtencion,
            @PathVariable Integer idDetalle,
            @RequestBody DetalleServicio detalleActualizado) {
        try {
            DetalleServicio detalleModificado = detalleService.actualizar(idDetalle, detalleActualizado);
            return ResponseEntity.ok(ApiResponse.exitoso("Detalle actualizado correctamente", detalleModificado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al actualizar detalle: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/atenciones/{idAtencion}/detalles/{idDetalle}
     * Eliminar un detalle.
     */
    @DeleteMapping("/{idDetalle}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Integer idAtencion,
            @PathVariable Integer idDetalle) {
        try {
            detalleService.eliminar(idDetalle);
            return ResponseEntity.ok(ApiResponse.exitoso("Detalle eliminado correctamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al eliminar detalle: " + e.getMessage()));
        }
    }

    /**
     * GET /api/atenciones/{idAtencion}/detalles/subtotal
     * Obtener subtotal total de la atención.
     */
    @GetMapping("/subtotal")
    public ResponseEntity<ApiResponse<BigDecimal>> obtenerSubtotalAtencion(@PathVariable Integer idAtencion) {
        try {
            BigDecimal subtotal = detalleService.calcularSubtotalAtencion(idAtencion);
            return ResponseEntity.ok(ApiResponse.exitoso("Subtotal calculado correctamente", subtotal));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al calcular subtotal: " + e.getMessage()));
        }
    }
}
