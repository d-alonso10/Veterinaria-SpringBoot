package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.AuditLog;
import com.teranvet.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de Logs de Auditoría.
 * Endpoints: /api/admin/audit
 */
@RestController
@RequestMapping("/api/admin/audit")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuditController {

    @Autowired
    private AuditService auditService;

    /**
     * GET /api/admin/audit
     * Obtener todos los logs de auditoría.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLog>>> obtenerTodos() {
        try {
            List<AuditLog> logs = auditService.obtenerTodos();
            return ResponseEntity.ok(ApiResponse.exitoso("Logs de auditoría obtenidos correctamente", logs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener logs: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/audit/{id}
     * Obtener un log por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditLog>> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<AuditLog> log = auditService.obtenerPorId(id);
            if (log.isPresent()) {
                return ResponseEntity.ok(ApiResponse.exitoso("Log encontrado", log.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Log no encontrado con ID: " + id));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener log: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/audit/usuario/{idUsuario}
     * Obtener logs por usuario.
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<ApiResponse<List<AuditLog>>> obtenerPorUsuario(@PathVariable Integer idUsuario) {
        try {
            List<AuditLog> logs = auditService.obtenerPorUsuario(idUsuario);
            return ResponseEntity.ok(ApiResponse.exitoso("Logs obtenidos por usuario correctamente", logs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener logs: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/audit/fecha/{fecha}
     * Obtener logs por fecha.
     */
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<ApiResponse<List<AuditLog>>> obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            List<AuditLog> logs = auditService.obtenerPorFecha(fecha);
            return ResponseEntity.ok(ApiResponse.exitoso("Logs obtenidos por fecha correctamente", logs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener logs: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/audit/accion/{accion}
     * Obtener logs por acción.
     */
    @GetMapping("/accion/{accion}")
    public ResponseEntity<ApiResponse<List<AuditLog>>> obtenerPorAccion(@PathVariable String accion) {
        try {
            List<AuditLog> logs = auditService.obtenerPorAccion(accion);
            return ResponseEntity.ok(ApiResponse.exitoso("Logs obtenidos por acción correctamente", logs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener logs: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/admin/audit/{id}
     * Eliminar un log.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        try {
            auditService.eliminar(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Log eliminado correctamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al eliminar log: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/audit/limite/{limite}
     * Obtener logs de auditoría con límite.
     */
    @GetMapping("/limite/{limite}")
    public ResponseEntity<ApiResponse<List<AuditLog>>> obtenerConLimite(@PathVariable Integer limite) {
        try {
            List<AuditLog> logs = auditService.obtenerConLimite(limite);
            return ResponseEntity.ok(ApiResponse.exitoso("Logs obtenidos con límite correctamente", logs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener logs: " + e.getMessage()));
        }
    }
}
