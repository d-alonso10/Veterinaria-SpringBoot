package com.teranvet.controller;

import com.teranvet.dto.ApiResponse;
import com.teranvet.dto.LoginRequest;
import com.teranvet.dto.LoginResponse;
import com.teranvet.entity.UsuarioSistema;
import com.teranvet.service.UsuarioSistemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador REST para Autenticación.
 * Endpoints: /api/auth
 * Maneja el login y validación de credenciales de usuarios.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UsuarioSistemaService usuarioService;

    /**
     * POST /api/auth/login
     * Autenticar usuario con email y contraseña (hash).
     *
     * @param loginRequest Objeto con email y passwordHash
     * @return LoginResponse con datos del usuario si es exitoso
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Validar entrada
            if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getPasswordHash() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Email y contraseña son requeridos"));
            }
            
            if (loginRequest.getEmail().trim().isEmpty() || loginRequest.getPasswordHash().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Email y contraseña no pueden estar vacíos"));
            }
            
            // Validar credenciales
            Optional<UsuarioSistema> usuario = usuarioService.validarCredenciales(
                    loginRequest.getEmail(),
                    loginRequest.getPasswordHash()
            );
            
            if (usuario.isPresent()) {
                // Construcción de LoginResponse
                LoginResponse response = new LoginResponse();
                response.setIdUsuario(usuario.get().getIdUsuario());
                response.setNombre(usuario.get().getNombre());
                response.setEmail(usuario.get().getEmail());
                response.setRol(usuario.get().getRol() != null ? usuario.get().getRol().toString() : "");
                response.setMensaje("Login exitoso");
                
                return ResponseEntity.ok(ApiResponse.exitoso("Autenticación exitosa", response));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Credenciales inválidas"));
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al autenticar: " + e.getMessage()));
        }
    }

    /**
     * POST /api/auth/validar
     * Validar si un usuario existe por email.
     *
     * @param email Email del usuario
     * @return true si existe, false en caso contrario
     */
    @PostMapping("/validar")
    public ResponseEntity<ApiResponse<Boolean>> validarUsuario(@RequestParam String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("El email es requerido"));
            }
            
            Optional<UsuarioSistema> usuario = usuarioService.obtenerPorEmail(email);
            boolean existe = usuario.isPresent();
            
            String mensaje = existe ? "Usuario encontrado" : "Usuario no encontrado";
            return ResponseEntity.ok(ApiResponse.exitoso(mensaje, existe));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al validar usuario: " + e.getMessage()));
        }
    }

    /**
     * POST /api/auth/logout
     * Endpoint simbólico de logout.
     * Nota: En una implementación real con JWT, aquí se invalida el token.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        try {
            // Implementación básica de logout
            // En un sistema real con JWT, se invalidaría el token aquí
            return ResponseEntity.ok(ApiResponse.exitoso("Logout exitoso", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al hacer logout: " + e.getMessage()));
        }
    }

    /**
     * POST /api/auth/cambiar-contraseña
     * Cambiar contraseña de un usuario autenticado.
     *
     * @param idUsuario ID del usuario
     * @param request Objeto con la nueva contraseña
     * @return Usuario actualizado
     */
    @PostMapping("/cambiar-contraseña")
    public ResponseEntity<ApiResponse<UsuarioSistema>> cambiarContraseña(
            @RequestParam Integer idUsuario,
            @RequestBody ChangePasswordRequest request) {
        try {
            if (idUsuario == null || idUsuario <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("ID de usuario inválido"));
            }
            
            if (request == null || request.getNuevaContraseña() == null || request.getNuevaContraseña().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("La nueva contraseña es requerida"));
            }
            
            UsuarioSistema usuario = usuarioService.cambiarContraseña(idUsuario, request.getNuevaContraseña());
            return ResponseEntity.ok(ApiResponse.exitoso("Contraseña actualizada correctamente", usuario));
            
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
