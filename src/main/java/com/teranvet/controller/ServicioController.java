package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.Servicio;
import com.teranvet.service.ServicioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicios")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class ServicioController {
    
    @Autowired
    private ServicioService servicioService;
    
    /**
     * GET /api/servicios - Obtener todos los servicios
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Servicio>>> obtenerTodos() {
        try {
            log.info("GET /servicios - Obteniendo todos los servicios");
            List<Servicio> servicios = servicioService.obtenerTodos();
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Servicios obtenidos exitosamente", servicios)
            );
        } catch (Exception e) {
            log.error("Error al obtener servicios", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener servicios", e.getMessage()));
        }
    }
    
    /**
     * GET /api/servicios/{id} - Obtener servicio por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Servicio>> obtenerPorId(@PathVariable Integer id) {
        try {
            log.info("GET /servicios/{} - Obteniendo servicio", id);
            Servicio servicio = servicioService.obtenerPorId(id);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Servicio obtenido exitosamente", servicio)
            );
        } catch (Exception e) {
            log.error("Error al obtener servicio", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Servicio no encontrado", e.getMessage()));
        }
    }
    
    /**
     * GET /api/servicios/categoria/{categoria} - Servicios por categoría
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<ApiResponse<List<Servicio>>> obtenerPorCategoria(
            @PathVariable String categoria) {
        try {
            log.info("GET /servicios/categoria/{} - Obteniendo servicios", categoria);
            List<Servicio> servicios = servicioService.obtenerPorCategoria(categoria);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Servicios obtenidos exitosamente", servicios)
            );
        } catch (Exception e) {
            log.error("Error al obtener servicios por categoría", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al obtener servicios", e.getMessage()));
        }
    }
    
    /**
     * GET /api/servicios/buscar/{nombre} - Buscar servicios por nombre
     */
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<ApiResponse<List<Servicio>>> buscarPorNombre(@PathVariable String nombre) {
        try {
            log.info("GET /servicios/buscar/{} - Buscando servicios", nombre);
            List<Servicio> servicios = servicioService.buscarPorNombre(nombre);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Búsqueda completada", servicios)
            );
        } catch (Exception e) {
            log.error("Error en búsqueda de servicios", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error en búsqueda", e.getMessage()));
        }
    }
    
    /**
     * POST /api/servicios - Crear nuevo servicio
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Servicio>> crear(@RequestBody Servicio servicio) {
        try {
            log.info("POST /servicios - Creando nuevo servicio");
            Servicio nuevoServicio = servicioService.crear(servicio);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Servicio creado exitosamente", nuevoServicio));
        } catch (Exception e) {
            log.error("Error al crear servicio", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al crear servicio", e.getMessage()));
        }
    }
    
    /**
     * PUT /api/servicios/{id} - Actualizar servicio
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Servicio>> actualizar(
            @PathVariable Integer id,
            @RequestBody Servicio servicioDTO) {
        try {
            log.info("PUT /servicios/{} - Actualizando servicio", id);
            Servicio actualizado = servicioService.actualizar(id, servicioDTO);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Servicio actualizado exitosamente", actualizado)
            );
        } catch (Exception e) {
            log.error("Error al actualizar servicio", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al actualizar servicio", e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/servicios/{id} - Eliminar servicio
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        try {
            log.info("DELETE /servicios/{} - Eliminando servicio", id);
            servicioService.eliminar(id);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Servicio eliminado exitosamente", null)
            );
        } catch (Exception e) {
            log.error("Error al eliminar servicio", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al eliminar servicio", e.getMessage()));
        }
    }
}
