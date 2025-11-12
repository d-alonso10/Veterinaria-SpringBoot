package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.PaqueteServicio;
import com.teranvet.service.PaqueteServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de Paquetes de Servicios.
 * Endpoints: /api/servicios/paquetes
 */
@RestController
@RequestMapping("/api/servicios/paquetes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaqueteServicioController {

    @Autowired
    private PaqueteServicioService paqueteService;

    /**
     * GET /api/servicios/paquetes
     * Obtener todos los paquetes.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaqueteServicio>>> obtenerTodos() {
        try {
            List<PaqueteServicio> paquetes = paqueteService.obtenerTodos();
            return ResponseEntity.ok(ApiResponse.exitoso("Paquetes obtenidos correctamente", paquetes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener paquetes: " + e.getMessage()));
        }
    }

    /**
     * GET /api/servicios/paquetes/{id}
     * Obtener un paquete por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaqueteServicio>> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<PaqueteServicio> paquete = paqueteService.obtenerPorId(id);
            if (paquete.isPresent()) {
                return ResponseEntity.ok(ApiResponse.exitoso("Paquete encontrado", paquete.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Paquete no encontrado con ID: " + id));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener paquete: " + e.getMessage()));
        }
    }

    /**
     * POST /api/servicios/paquetes
     * Crear un nuevo paquete.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PaqueteServicio>> crear(@RequestBody PaqueteServicio paquete) {
        try {
            PaqueteServicio nuevoPaquete = paqueteService.crear(paquete);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Paquete creado correctamente", nuevoPaquete));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear paquete: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/servicios/paquetes/{id}
     * Actualizar un paquete existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaqueteServicio>> actualizar(
            @PathVariable Integer id,
            @RequestBody PaqueteServicio paqueteActualizado) {
        try {
            PaqueteServicio paqueteModificado = paqueteService.actualizar(id, paqueteActualizado);
            return ResponseEntity.ok(ApiResponse.exitoso("Paquete actualizado correctamente", paqueteModificado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al actualizar paquete: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/servicios/paquetes/{id}
     * Eliminar un paquete.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        try {
            paqueteService.eliminar(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Paquete eliminado correctamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al eliminar paquete: " + e.getMessage()));
        }
    }

    /**
     * GET /api/servicios/paquetes/activos
     * Obtener paquetes activos.
     */
    @GetMapping("/activos")
    public ResponseEntity<ApiResponse<List<PaqueteServicio>>> obtenerActivos() {
        try {
            List<PaqueteServicio> paquetes = paqueteService.obtenerActivos();
            return ResponseEntity.ok(ApiResponse.exitoso("Paquetes activos obtenidos correctamente", paquetes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener paquetes activos: " + e.getMessage()));
        }
    }

    /**
     * GET /api/servicios/paquetes/{id}/precio-final
     * Obtener precio final del paquete con descuento.
     */
    @GetMapping("/{id}/precio-final")
    public ResponseEntity<ApiResponse<BigDecimal>> obtenerPrecioFinal(@PathVariable Integer id) {
        try {
            BigDecimal precioFinal = paqueteService.obtenerPrecioFinal(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Precio final obtenido correctamente", precioFinal));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener precio final: " + e.getMessage()));
        }
    }
}
