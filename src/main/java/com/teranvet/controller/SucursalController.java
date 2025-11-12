package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.Sucursal;
import com.teranvet.service.SucursalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de Sucursales.
 * Endpoints: /api/admin/sucursales
 */
@RestController
@RequestMapping("/api/admin/sucursales")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SucursalController {

    @Autowired
    private SucursalService sucursalService;

    /**
     * GET /api/admin/sucursales
     * Obtener todas las sucursales.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Sucursal>>> obtenerTodas() {
        try {
            List<Sucursal> sucursales = sucursalService.obtenerTodas();
            return ResponseEntity.ok(ApiResponse.exitoso("Sucursales obtenidas correctamente", sucursales));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener sucursales: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/sucursales/{id}
     * Obtener una sucursal por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Sucursal>> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<Sucursal> sucursal = sucursalService.obtenerPorId(id);
            if (sucursal.isPresent()) {
                return ResponseEntity.ok(ApiResponse.exitoso("Sucursal encontrada", sucursal.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Sucursal no encontrada con ID: " + id));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener sucursal: " + e.getMessage()));
        }
    }

    /**
     * POST /api/admin/sucursales
     * Crear una nueva sucursal.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Sucursal>> crear(@RequestBody Sucursal sucursal) {
        try {
            Sucursal nuevaSucursal = sucursalService.crear(sucursal);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Sucursal creada correctamente", nuevaSucursal));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear sucursal: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/sucursales/{id}
     * Actualizar una sucursal existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Sucursal>> actualizar(
            @PathVariable Integer id,
            @RequestBody Sucursal sucursalActualizada) {
        try {
            Sucursal sucursalModificada = sucursalService.actualizar(id, sucursalActualizada);
            return ResponseEntity.ok(ApiResponse.exitoso("Sucursal actualizada correctamente", sucursalModificada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al actualizar sucursal: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/admin/sucursales/{id}
     * Eliminar una sucursal.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        try {
            sucursalService.eliminar(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Sucursal eliminada correctamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al eliminar sucursal: " + e.getMessage()));
        }
    }
}
