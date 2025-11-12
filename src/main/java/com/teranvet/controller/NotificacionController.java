package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.Notificacion;
import com.teranvet.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de Notificaciones.
 * Endpoints: /api/notificaciones
 */
@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    /**
     * GET /api/notificaciones
     * Obtener todas las notificaciones.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Notificacion>>> obtenerTodas() {
        try {
            List<Notificacion> notificaciones = notificacionService.obtenerTodas();
            return ResponseEntity.ok(ApiResponse.exitoso("Notificaciones obtenidas correctamente", notificaciones));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener notificaciones: " + e.getMessage()));
        }
    }

    /**
     * GET /api/notificaciones/{id}
     * Obtener una notificación por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Notificacion>> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<Notificacion> notificacion = notificacionService.obtenerPorId(id);
            if (notificacion.isPresent()) {
                return ResponseEntity.ok(ApiResponse.exitoso("Notificación encontrada", notificacion.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Notificación no encontrada con ID: " + id));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener notificación: " + e.getMessage()));
        }
    }

    /**
     * GET /api/notificaciones/cliente/{idCliente}
     * Obtener notificaciones de un cliente.
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<ApiResponse<List<Notificacion>>> obtenerPorCliente(@PathVariable Integer idCliente) {
        try {
            List<Notificacion> notificaciones = notificacionService.obtenerPorCliente(idCliente);
            return ResponseEntity.ok(ApiResponse.exitoso("Notificaciones obtenidas correctamente", notificaciones));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener notificaciones: " + e.getMessage()));
        }
    }

    /**
     * GET /api/notificaciones/pendientes
     * Obtener notificaciones pendientes.
     */
    @GetMapping("/pendientes")
    public ResponseEntity<ApiResponse<List<Notificacion>>> obtenerPendientes() {
        try {
            List<Notificacion> notificaciones = notificacionService.obtenerPendientes();
            return ResponseEntity.ok(ApiResponse.exitoso("Notificaciones pendientes obtenidas correctamente", notificaciones));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener notificaciones pendientes: " + e.getMessage()));
        }
    }

    /**
     * POST /api/notificaciones
     * Crear una nueva notificación.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Notificacion>> crear(@RequestBody Notificacion notificacion) {
        try {
            Notificacion nuevaNotificacion = notificacionService.crear(notificacion);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Notificación creada correctamente", nuevaNotificacion));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear notificación: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/notificaciones/{id}
     * Actualizar una notificación.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Notificacion>> actualizar(
            @PathVariable Integer id,
            @RequestBody Notificacion notificacionActualizada) {
        try {
            Notificacion notificacionModificada = notificacionService.actualizar(id, notificacionActualizada);
            return ResponseEntity.ok(ApiResponse.exitoso("Notificación actualizada correctamente", notificacionModificada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al actualizar notificación: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/notificaciones/{id}/marcar-enviada
     * Marcar notificación como enviada.
     */
    @PutMapping("/{id}/marcar-enviada")
    public ResponseEntity<ApiResponse<Notificacion>> marcarEnviada(@PathVariable Integer id) {
        try {
            Notificacion notificacion = notificacionService.marcarEnviada(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Notificación marcada como enviada", notificacion));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al marcar notificación: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/notificaciones/{id}/marcar-leida
     * Marcar notificación como leída.
     */
    @PutMapping("/{id}/marcar-leida")
    public ResponseEntity<ApiResponse<Notificacion>> marcarLeida(@PathVariable Integer id) {
        try {
            Notificacion notificacion = notificacionService.marcarLeida(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Notificación marcada como leída", notificacion));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al marcar notificación: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/notificaciones/{id}
     * Eliminar una notificación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        try {
            notificacionService.eliminar(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Notificación eliminada correctamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al eliminar notificación: " + e.getMessage()));
        }
    }

    /**
     * GET /api/notificaciones/cliente/{idCliente}/no-leidas
     * Obtener notificaciones no leídas de un cliente.
     */
    @GetMapping("/cliente/{idCliente}/no-leidas")
    public ResponseEntity<ApiResponse<List<Notificacion>>> obtenerNoLeidas(@PathVariable Integer idCliente) {
        try {
            List<Notificacion> notificaciones = notificacionService.obtenerNoLeidas(idCliente);
            return ResponseEntity.ok(ApiResponse.exitoso("Notificaciones no leídas obtenidas correctamente", notificaciones));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener notificaciones no leídas: " + e.getMessage()));
        }
    }
}
