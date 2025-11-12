package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.ConfiguracionEstimacion;
import com.teranvet.service.ConfiguracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de Configuraciones de Estimación.
 * Endpoints: /api/admin/configuracion
 */
@RestController
@RequestMapping("/api/admin/configuracion")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ConfiguracionController {

    @Autowired
    private ConfiguracionService configService;

    /**
     * GET /api/admin/configuracion
     * Obtener todas las configuraciones.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ConfiguracionEstimacion>>> obtenerTodas() {
        try {
            List<ConfiguracionEstimacion> configs = configService.obtenerTodas();
            return ResponseEntity.ok(ApiResponse.exitoso("Configuraciones obtenidas correctamente", configs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener configuraciones: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/configuracion/{id}
     * Obtener una configuración por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConfiguracionEstimacion>> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<ConfiguracionEstimacion> config = configService.obtenerPorId(id);
            if (config.isPresent()) {
                return ResponseEntity.ok(ApiResponse.exitoso("Configuración encontrada", config.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Configuración no encontrada con ID: " + id));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener configuración: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/configuracion/servicio/{idServicio}
     * Obtener configuraciones por servicio.
     */
    @GetMapping("/servicio/{idServicio}")
    public ResponseEntity<ApiResponse<List<ConfiguracionEstimacion>>> obtenerPorServicio(@PathVariable Integer idServicio) {
        try {
            List<ConfiguracionEstimacion> configs = configService.obtenerPorServicio(idServicio);
            return ResponseEntity.ok(ApiResponse.exitoso("Configuraciones obtenidas por servicio correctamente", configs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener configuraciones: " + e.getMessage()));
        }
    }

    /**
     * POST /api/admin/configuracion
     * Crear una nueva configuración.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ConfiguracionEstimacion>> crear(@RequestBody ConfiguracionEstimacion config) {
        try {
            ConfiguracionEstimacion nuevaConfig = configService.crear(config);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Configuración creada correctamente", nuevaConfig));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear configuración: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/configuracion/{id}
     * Actualizar una configuración existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ConfiguracionEstimacion>> actualizar(
            @PathVariable Integer id,
            @RequestBody ConfiguracionEstimacion configActualizada) {
        try {
            ConfiguracionEstimacion configModificada = configService.actualizar(id, configActualizada);
            return ResponseEntity.ok(ApiResponse.exitoso("Configuración actualizada correctamente", configModificada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al actualizar configuración: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/admin/configuracion/{id}
     * Eliminar una configuración.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        try {
            configService.eliminar(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Configuración eliminada correctamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al eliminar configuración: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/configuracion/tiempo/{idServicio}
     * Obtener tiempo estimado para un servicio.
     */
    @GetMapping("/tiempo/{idServicio}")
    public ResponseEntity<ApiResponse<Integer>> obtenerTiempoEstimado(@PathVariable Integer idServicio) {
        try {
            Integer tiempo = configService.obtenerTiempoEstimado(idServicio);
            return ResponseEntity.ok(ApiResponse.exitoso("Tiempo estimado obtenido correctamente", tiempo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener tiempo: " + e.getMessage()));
        }
    }
}
