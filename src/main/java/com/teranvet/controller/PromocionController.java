package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.Promocion;
import com.teranvet.service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de Promociones.
 * Endpoints: /api/promociones
 */
@RestController
@RequestMapping("/api/promociones")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    /**
     * GET /api/promociones
     * Obtener todas las promociones.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Promocion>>> obtenerTodas() {
        try {
            List<Promocion> promociones = promocionService.obtenerTodas();
            return ResponseEntity.ok(ApiResponse.exitoso("Promociones obtenidas correctamente", promociones));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener promociones: " + e.getMessage()));
        }
    }

    /**
     * GET /api/promociones/{id}
     * Obtener una promoción por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Promocion>> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<Promocion> promocion = promocionService.obtenerPorId(id);
            if (promocion.isPresent()) {
                return ResponseEntity.ok(ApiResponse.exitoso("Promoción encontrada", promocion.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Promoción no encontrada con ID: " + id));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener promoción: " + e.getMessage()));
        }
    }

    /**
     * GET /api/promociones/activas
     * Obtener promociones activas.
     */
    @GetMapping("/activas")
    public ResponseEntity<ApiResponse<List<Promocion>>> obtenerActivas() {
        try {
            List<Promocion> promociones = promocionService.obtenerActivas();
            return ResponseEntity.ok(ApiResponse.exitoso("Promociones activas obtenidas correctamente", promociones));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener promociones activas: " + e.getMessage()));
        }
    }

    /**
     * POST /api/promociones
     * Crear una nueva promoción.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Promocion>> crear(@RequestBody Promocion promocion) {
        try {
            Promocion nuevaPromocion = promocionService.crear(promocion);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Promoción creada correctamente", nuevaPromocion));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear promoción: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/promociones/{id}
     * Actualizar una promoción.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Promocion>> actualizar(
            @PathVariable Integer id,
            @RequestBody Promocion promocionActualizada) {
        try {
            Promocion promocionModificada = promocionService.actualizar(id, promocionActualizada);
            return ResponseEntity.ok(ApiResponse.exitoso("Promoción actualizada correctamente", promocionModificada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al actualizar promoción: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/promociones/{id}
     * Eliminar una promoción.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        try {
            promocionService.eliminar(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Promoción eliminada correctamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al eliminar promoción: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/promociones/{id}/activar
     * Activar una promoción.
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Promocion>> activar(@PathVariable Integer id) {
        try {
            Promocion promocion = promocionService.activar(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Promoción activada correctamente", promocion));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al activar promoción: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/promociones/{id}/desactivar
     * Desactivar una promoción.
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<Promocion>> desactivar(@PathVariable Integer id) {
        try {
            Promocion promocion = promocionService.desactivar(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Promoción desactivada correctamente", promocion));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al desactivar promoción: " + e.getMessage()));
        }
    }

    /**
     * GET /api/promociones/{id}/valida
     * Verificar si una promoción es válida.
     */
    @GetMapping("/{id}/valida")
    public ResponseEntity<ApiResponse<Boolean>> esValida(@PathVariable Integer id) {
        try {
            boolean valida = promocionService.esValida(id);
            String mensaje = valida ? "Promoción es válida" : "Promoción no es válida";
            return ResponseEntity.ok(ApiResponse.exitoso(mensaje, valida));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al verificar promoción: " + e.getMessage()));
        }
    }
}
