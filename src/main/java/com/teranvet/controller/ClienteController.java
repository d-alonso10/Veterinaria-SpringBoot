package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.dto.ClienteDTO;
import com.teranvet.service.ClienteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;
    
    /**
     * GET /api/clientes - Obtener todos los clientes
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteDTO>>> obtenerTodos() {
        try {
            log.info("GET /clientes - Obteniendo todos los clientes");
            List<ClienteDTO> clientes = clienteService.obtenerTodos();
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Clientes obtenidos exitosamente", clientes)
            );
        } catch (Exception e) {
            log.error("Error al obtener clientes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener clientes", e.getMessage()));
        }
    }
    
    /**
     * GET /api/clientes/{id} - Obtener cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            log.info("GET /clientes/{} - Obteniendo cliente", id);
            ClienteDTO cliente = clienteService.obtenerPorId(id);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Cliente obtenido exitosamente", cliente)
            );
        } catch (Exception e) {
            log.error("Error al obtener cliente", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Cliente no encontrado", e.getMessage()));
        }
    }
    
    /**
     * GET /api/clientes/buscar/termino - Buscar clientes por término
     */
    @GetMapping("/buscar/{termino}")
    public ResponseEntity<ApiResponse<List<ClienteDTO>>> buscarPorTermino(@PathVariable String termino) {
        try {
            log.info("GET /clientes/buscar/{} - Buscando clientes", termino);
            List<ClienteDTO> clientes = clienteService.buscarPorTermino(termino);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Búsqueda completada", clientes)
            );
        } catch (Exception e) {
            log.error("Error en búsqueda de clientes", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error en búsqueda", e.getMessage()));
        }
    }
    
    /**
     * POST /api/clientes - Crear nuevo cliente
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ClienteDTO>> crear(@RequestBody ClienteDTO clienteDTO) {
        try {
            log.info("POST /clientes - Creando nuevo cliente");
            ClienteDTO nuevoCliente = clienteService.crear(clienteDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Cliente creado exitosamente", nuevoCliente));
        } catch (Exception e) {
            log.error("Error al crear cliente", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al crear cliente", e.getMessage()));
        }
    }
    
    /**
     * PUT /api/clientes/{id} - Actualizar cliente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> actualizar(
            @PathVariable Integer id,
            @RequestBody ClienteDTO clienteDTO) {
        try {
            log.info("PUT /clientes/{} - Actualizando cliente", id);
            ClienteDTO actualizado = clienteService.actualizar(id, clienteDTO);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Cliente actualizado exitosamente", actualizado)
            );
        } catch (Exception e) {
            log.error("Error al actualizar cliente", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al actualizar cliente", e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/clientes/{id} - Eliminar cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        try {
            log.info("DELETE /clientes/{} - Eliminando cliente", id);
            clienteService.eliminar(id);
            return ResponseEntity.ok(
                    ApiResponse.exitoso("Cliente eliminado exitosamente", null)
            );
        } catch (Exception e) {
            log.error("Error al eliminar cliente", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al eliminar cliente", e.getMessage()));
        }
    }
}
