package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.dto.MascotaDTO;
import com.teranvet.service.MascotaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mascotas")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class MascotaController {
    
    @Autowired
    private MascotaService mascotaService;
    
    /**
     * GET /api/mascotas - Obtener todas las mascotas
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MascotaDTO>>> obtenerTodas() {
        try {
            log.info("GET /mascotas - Obteniendo todas las mascotas");
            List<MascotaDTO> mascotas = mascotaService.obtenerTodas();
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Mascotas obtenidas exitosamente", mascotas)
            );
        } catch (Exception e) {
            log.error("Error al obtener mascotas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener mascotas", e.getMessage()));
        }
    }
    
    /**
     * GET /api/mascotas/{id} - Obtener mascota por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MascotaDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            log.info("GET /mascotas/{} - Obteniendo mascota", id);
            MascotaDTO mascota = mascotaService.obtenerPorId(id);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Mascota obtenida exitosamente", mascota)
            );
        } catch (Exception e) {
            log.error("Error al obtener mascota", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Mascota no encontrada", e.getMessage()));
        }
    }
    
    /**
     * GET /api/mascotas/cliente/{idCliente} - Obtener mascotas por cliente
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<ApiResponse<List<MascotaDTO>>> obtenerPorCliente(@PathVariable Integer idCliente) {
        try {
            log.info("GET /mascotas/cliente/{} - Obteniendo mascotas del cliente", idCliente);
            List<MascotaDTO> mascotas = mascotaService.obtenerPorCliente(idCliente);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Mascotas del cliente obtenidas exitosamente", mascotas)
            );
        } catch (Exception e) {
            log.error("Error al obtener mascotas del cliente", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Error al obtener mascotas", e.getMessage()));
        }
    }
    
    /**
     * GET /api/mascotas/buscar/{termino} - Buscar mascotas por término
     */
    @GetMapping("/buscar/{termino}")
    public ResponseEntity<ApiResponse<List<MascotaDTO>>> buscarPorTermino(@PathVariable String termino) {
        try {
            log.info("GET /mascotas/buscar/{} - Buscando mascotas", termino);
            List<MascotaDTO> mascotas = mascotaService.buscarPorTermino(termino);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Búsqueda completada", mascotas)
            );
        } catch (Exception e) {
            log.error("Error en búsqueda de mascotas", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error en búsqueda", e.getMessage()));
        }
    }
    
    /**
     * POST /api/mascotas - Crear nueva mascota
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MascotaDTO>> crear(@RequestBody MascotaDTO mascotaDTO) {
        try {
            log.info("POST /mascotas - Creando nueva mascota");
            MascotaDTO nuevaMascota = mascotaService.crear(mascotaDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Mascota creada exitosamente", nuevaMascota));
        } catch (Exception e) {
            log.error("Error al crear mascota", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al crear mascota", e.getMessage()));
        }
    }
    
    /**
     * PUT /api/mascotas/{id} - Actualizar mascota
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MascotaDTO>> actualizar(
            @PathVariable Integer id,
            @RequestBody MascotaDTO mascotaDTO) {
        try {
            log.info("PUT /mascotas/{} - Actualizando mascota", id);
            MascotaDTO actualizada = mascotaService.actualizar(id, mascotaDTO);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Mascota actualizada exitosamente", actualizada)
            );
        } catch (Exception e) {
            log.error("Error al actualizar mascota", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al actualizar mascota", e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/mascotas/{id} - Eliminar mascota
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        try {
            log.info("DELETE /mascotas/{} - Eliminando mascota", id);
            mascotaService.eliminar(id);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Mascota eliminada exitosamente", null)
            );
        } catch (Exception e) {
            log.error("Error al eliminar mascota", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al eliminar mascota", e.getMessage()));
        }
    }
}
