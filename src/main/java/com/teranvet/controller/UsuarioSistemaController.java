package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.entity.UsuarioSistema;
import com.teranvet.service.UsuarioSistemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de Usuarios del Sistema.
 * Endpoints: /api/admin/usuarios
 */
@RestController
@RequestMapping("/api/admin/usuarios")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioSistemaController {

    @Autowired
    private UsuarioSistemaService usuarioService;

    /**
     * GET /api/admin/usuarios
     * Obtener todos los usuarios del sistema.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioSistema>>> obtenerTodos() {
        try {
            List<UsuarioSistema> usuarios = usuarioService.obtenerTodos();
            return ResponseEntity.ok(ApiResponse.exitoso("Usuarios obtenidos correctamente", usuarios));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener usuarios: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/usuarios/{id}
     * Obtener un usuario por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioSistema>> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<UsuarioSistema> usuario = usuarioService.obtenerPorId(id);
            if (usuario.isPresent()) {
                return ResponseEntity.ok(ApiResponse.exitoso("Usuario encontrado", usuario.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Usuario no encontrado con ID: " + id));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener usuario: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/usuarios/email/{email}
     * Obtener un usuario por email.
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UsuarioSistema>> obtenerPorEmail(@PathVariable String email) {
        try {
            Optional<UsuarioSistema> usuario = usuarioService.obtenerPorEmail(email);
            if (usuario.isPresent()) {
                return ResponseEntity.ok(ApiResponse.exitoso("Usuario encontrado", usuario.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Usuario no encontrado con email: " + email));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener usuario: " + e.getMessage()));
        }
    }

    /**
     * POST /api/admin/usuarios
     * Crear un nuevo usuario del sistema.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioSistema>> crear(@RequestBody UsuarioSistema usuario) {
        try {
            UsuarioSistema nuevoUsuario = usuarioService.crear(usuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.exitoso("Usuario creado correctamente", nuevoUsuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear usuario: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/usuarios/{id}
     * Actualizar un usuario existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioSistema>> actualizar(
            @PathVariable Integer id,
            @RequestBody UsuarioSistema usuarioActualizado) {
        try {
            UsuarioSistema usuarioModificado = usuarioService.actualizar(id, usuarioActualizado);
            return ResponseEntity.ok(ApiResponse.exitoso("Usuario actualizado correctamente", usuarioModificado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al actualizar usuario: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/admin/usuarios/{id}
     * Eliminar un usuario.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.ok(ApiResponse.exitoso("Usuario eliminado correctamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al eliminar usuario: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/usuarios/rol/{rol}
     * Obtener usuarios por rol.
     */
    @GetMapping("/rol/{rol}")
    public ResponseEntity<ApiResponse<List<UsuarioSistema>>> obtenerPorRol(@PathVariable String rol) {
        try {
            List<UsuarioSistema> usuarios = usuarioService.obtenerPorRol(rol);
            return ResponseEntity.ok(ApiResponse.exitoso("Usuarios obtenidos por rol correctamente", usuarios));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener usuarios por rol: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/usuarios/{id}/cambiar-contraseña
     * Cambiar contraseña de un usuario.
     */
    @PutMapping("/{id}/cambiar-contraseña")
    public ResponseEntity<ApiResponse<UsuarioSistema>> cambiarContraseña(
            @PathVariable Integer id,
            @RequestBody ChangePasswordRequest request) {
        try {
            UsuarioSistema usuarioActualizado = usuarioService.cambiarContraseña(id, request.getNuevaContraseña());
            return ResponseEntity.ok(ApiResponse.exitoso("Contraseña actualizada correctamente", usuarioActualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al cambiar contraseña: " + e.getMessage()));
        }
    }

    /**
     * DTO para cambio de contraseña.
     */
    public static class ChangePasswordRequest {
        private String nuevaContraseña;

        public String getNuevaContraseña() {
            return nuevaContraseña;
        }

        public void setNuevaContraseña(String nuevaContraseña) {
            this.nuevaContraseña = nuevaContraseña;
        }
    }
}
