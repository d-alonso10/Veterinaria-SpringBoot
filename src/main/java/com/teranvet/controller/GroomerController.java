package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.Groomer;
import com.teranvet.service.GroomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de Groomers.
 * Endpoints: /api/groomers
 */
@RestController
@RequestMapping("/api/groomers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GroomerController {

    @Autowired
    private GroomerService groomerService;

    /**
     * GET /api/groomers
     * Obtener todos los groomers.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Groomer>>> obtenerTodos() {
        try {
            // Este método SÍ devuelve List<Groomer>
            List<Groomer> groomers = groomerService.obtenerTodos();
            return ResponseEntity.ok(ApiResponse.exitoso("Groomers obtenidos correctamente", groomers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener groomers: " + e.getMessage()));
        }
    }

    /**
     * GET /api/groomers/{id}
     * Obtener un groomer por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Groomer>> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<Groomer> groomer = groomerService.obtenerPorId(id);
            if (groomer.isPresent()) {
                return ResponseEntity.ok(ApiResponse.exitoso("Groomer encontrado", groomer.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Groomer no encontrado con ID: " + id));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener groomer: " + e.getMessage()));
        }
    }

    /**
     * POST /api/groomers
     * Crear un nuevo groomer.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Groomer>> crear(@RequestBody Groomer groomer) {
        try {
            Groomer nuevoGroomer = groomerService.crear(groomer);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Groomer creado correctamente", nuevoGroomer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear groomer: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/groomers/{id}
     * Actualizar un groomer existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Groomer>> actualizar(
            @PathVariable Integer id,
            @RequestBody Groomer groomerActualizado) {
        try {
            Groomer groomerModificado = groomerService.actualizar(id, groomerActualizado);
            return ResponseEntity.ok(ApiResponse.exitoso("Groomer actualizado correctamente", groomerModificado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al actualizar groomer: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/groomers/{id}
     * Eliminar un groomer.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        try {
            groomerService.eliminar(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Groomer eliminado correctamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al eliminar groomer: " + e.getMessage()));
        }
    }

    /**
     * GET /api/groomers/disponibilidad/{fecha}
     * Obtener groomers disponibles para una fecha específica.
     */
    @GetMapping("/disponibilidad/{fecha}")
    // CORRECCIÓN: El tipo de dato genérico de ApiResponse ahora es List<Object[]>
    public ResponseEntity<ApiResponse<List<Object[]>>> obtenerDisponibilidad(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            // CORRECCIÓN: El servicio devuelve List<Object[]>
            List<Object[]> disponibles = groomerService.obtenerDisponibilidad(fecha);
            return ResponseEntity.ok(ApiResponse.exitoso(
                    "Datos de disponibilidad obtenidos correctamente", disponibles));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener disponibilidad: " + e.getMessage()));
        }
    }

    /**
     * GET /api/groomers/ocupacion/{fecha}
     * Obtener ocupación de groomers para una fecha específica.
     */
    @GetMapping("/ocupacion/{fecha}")
    // CORRECCIÓN: El tipo de dato genérico de ApiResponse ahora es List<Object[]>
    public ResponseEntity<ApiResponse<List<Object[]>>> obtenerOcupacion(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            // CORRECCIÓN: El servicio devuelve List<Object[]>
            List<Object[]> ocupacion = groomerService.obtenerOcupacion(fecha);
            return ResponseEntity.ok(ApiResponse.exitoso(
                    "Información de ocupación obtenida correctamente", ocupacion));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener ocupación: " + e.getMessage()));
        }
    }

    /**
     * GET /api/groomers/tiempos-promedio
     * Obtener tiempos promedio de todos los groomers.
     */
    @GetMapping("/tiempos-promedio")
    // CORRECCIÓN: El tipo de dato genérico de ApiResponse ahora es List<Object[]>
    // CORRECCIÓN: Se requieren parámetros de fecha
    public ResponseEntity<ApiResponse<List<Object[]>>> obtenerTiemposPromedio(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            // CORRECCIÓN: El servicio devuelve List<Object[]> y requiere fechas
            List<Object[]> tiempos = groomerService.obtenerTiemposPromedio(fechaInicio, fechaFin);
            return ResponseEntity.ok(ApiResponse.exitoso(
                    "Tiempos promedio obtenidos correctamente", tiempos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener tiempos promedio: " + e.getMessage()));
        }
    }

    /**
     * GET /api/groomers/especialidad/{especialidad}
     * Obtener groomers por especialidad.
     */
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<ApiResponse<List<Groomer>>> obtenerPorEspecialidad(
            @PathVariable String especialidad) {
        try {
            // Este método SÍ devuelve List<Groomer>
            List<Groomer> groomers = groomerService.obtenerPorEspecialidad(especialidad);
            return ResponseEntity.ok(ApiResponse.exitoso(
                    "Groomers obtenidos por especialidad correctamente", groomers));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener groomers por especialidad: " + e.getMessage()));
        }
    }

    /**
     * GET /api/groomers/disponible/{idGroomer}/{fecha}/{minutos}
     * Verificar si un groomer está disponible en una fecha/hora específica.
     */
    @GetMapping("/disponible/{idGroomer}/{fecha}/{minutos}")
    public ResponseEntity<ApiResponse<Boolean>> verificarDisponibilidad(
            @PathVariable Integer idGroomer,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha,
            @PathVariable Integer minutos) {
        try {
            // Este método SÍ devuelve un Boolean
            boolean disponible = groomerService.estaDisponible(idGroomer, fecha, minutos);
            String mensaje = disponible ? "Groomer está disponible" : "Groomer no está disponible";
            return ResponseEntity.ok(ApiResponse.exitoso(mensaje, disponible));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al verificar disponibilidad: " + e.getMessage()));
        }
    }
}