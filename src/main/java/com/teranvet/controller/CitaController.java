package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.dto.CitaDTO;
import com.teranvet.service.CitaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/citas")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class CitaController {
    
    @Autowired
    private CitaService citaService;
    
    /**
     * GET /api/citas - Obtener todas las citas
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CitaDTO>>> obtenerTodas() {
        try {
            log.info("GET /citas - Obteniendo todas las citas");
            List<CitaDTO> citas = citaService.obtenerTodas();
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Citas obtenidas exitosamente", citas)
            );
        } catch (Exception e) {
            log.error("Error al obtener citas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener citas", e.getMessage()));
        }
    }
    
    /**
     * GET /api/citas/{id} - Obtener cita por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CitaDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            log.info("GET /citas/{} - Obteniendo cita", id);
            CitaDTO cita = citaService.obtenerPorId(id);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Cita obtenida exitosamente", cita)
            );
        } catch (Exception e) {
            log.error("Error al obtener cita", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Cita no encontrada", e.getMessage()));
        }
    }
    
    /**
     * GET /api/citas/cliente/{idCliente}/proximas - Próximas citas de cliente
     */
    @GetMapping("/cliente/{idCliente}/proximas")
    public ResponseEntity<ApiResponse<List<CitaDTO>>> obtenerProximasCitas(@PathVariable Integer idCliente) {
        try {
            log.info("GET /citas/cliente/{}/proximas - Obteniendo próximas citas", idCliente);
            List<CitaDTO> citas = citaService.obtenerProximasCitas(idCliente);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Próximas citas obtenidas", citas)
            );
        } catch (Exception e) {
            log.error("Error al obtener próximas citas", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Error al obtener citas", e.getMessage()));
        }
    }
    
    /**
     * GET /api/citas/cliente/{idCliente} - Obtener citas de cliente
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<ApiResponse<List<CitaDTO>>> obtenerPorCliente(@PathVariable Integer idCliente) {
        try {
            log.info("GET /citas/cliente/{} - Obteniendo citas del cliente", idCliente);
            List<CitaDTO> citas = citaService.obtenerPorCliente(idCliente);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Citas del cliente obtenidas", citas)
            );
        } catch (Exception e) {
            log.error("Error al obtener citas del cliente", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Error al obtener citas", e.getMessage()));
        }
    }
    
    /**
     * POST /api/citas - Crear nueva cita
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CitaDTO>> crear(@RequestBody CitaDTO citaDTO) {
        try {
            log.info("POST /citas - Creando nueva cita");
            CitaDTO nuevaCita = citaService.crear(citaDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Cita creada exitosamente", nuevaCita));
        } catch (Exception e) {
            log.error("Error al crear cita", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al crear cita", e.getMessage()));
        }
    }
    
    /**
     * PUT /api/citas/{id}/reprogramar - Reprogramar cita
     */
    @PutMapping("/{id}/reprogramar")
    public ResponseEntity<ApiResponse<CitaDTO>> reprogramar(
            @PathVariable Integer id,
            @RequestParam LocalDateTime nuevaFecha) {
        try {
            log.info("PUT /citas/{}/reprogramar - Reprogramando cita", id);
            CitaDTO actualizada = citaService.reprogramar(id, nuevaFecha);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Cita reprogramada exitosamente", actualizada)
            );
        } catch (Exception e) {
            log.error("Error al reprogramar cita", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al reprogramar cita", e.getMessage()));
        }
    }
    
    /**
     * PUT /api/citas/{id}/cancelar - Cancelar cita
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<CitaDTO>> cancelar(@PathVariable Integer id) {
        try {
            log.info("PUT /citas/{}/cancelar - Cancelando cita", id);
            CitaDTO actualizada = citaService.cancelar(id);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Cita cancelada exitosamente", actualizada)
            );
        } catch (Exception e) {
            log.error("Error al cancelar cita", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al cancelar cita", e.getMessage()));
        }
    }
    
    /**
     * PUT /api/citas/{id}/confirmar-asistencia - Confirmar asistencia
     */
    @PutMapping("/{id}/confirmar-asistencia")
    public ResponseEntity<ApiResponse<CitaDTO>> confirmarAsistencia(@PathVariable Integer id) {
        try {
            log.info("PUT /citas/{}/confirmar-asistencia - Confirmando asistencia", id);
            CitaDTO actualizada = citaService.confirmarAsistencia(id);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Asistencia confirmada", actualizada)
            );
        } catch (Exception e) {
            log.error("Error al confirmar asistencia", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al confirmar asistencia", e.getMessage()));
        }
    }
    
    /**
     * PUT /api/citas/{id}/no-show - Marcar como no-show
     */
    @PutMapping("/{id}/no-show")
    public ResponseEntity<ApiResponse<CitaDTO>> marcarNoShow(@PathVariable Integer id) {
        try {
            log.info("PUT /citas/{}/no-show - Marcando como no-show", id);
            CitaDTO actualizada = citaService.marcarNoShow(id);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Cita marcada como no-show", actualizada)
            );
        } catch (Exception e) {
            log.error("Error al marcar no-show", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al marcar no-show", e.getMessage()));
        }
    }
}
