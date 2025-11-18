package com.teranvet.controller;

import com.teranvet.config.security.JwtTokenProvider;
import com.teranvet.dto.ApiResponse;
import com.teranvet.dto.LoginRequest;
import com.teranvet.dto.LoginResponse;
import com.teranvet.entity.UsuarioSistema;
import com.teranvet.service.UsuarioSistemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador REST para Autenticación con JWT.
 * Endpoints: /api/auth
 * Maneja el login y generación de JWT tokens.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioSistemaService usuarioService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * POST /api/auth/login
     * Autenticar usuario con email y contraseña (hash).
     * Retorna JWT token en la respuesta si es exitoso.
     *
     * @param loginRequest Objeto con email y passwordHash
     * @return LoginResponse con JWT token y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Intento de login con email: {}", loginRequest != null ? loginRequest.getEmail() : "null");

            // Validar entrada
            // CORREGIDO: Se usa getPassword()
            if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                logger.warn("Datos incompletos en login request");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Email y contraseña son requeridos"));
            }

            // CORREGIDO: Se usa getPassword()
            if (loginRequest.getEmail().trim().isEmpty() || loginRequest.getPassword().trim().isEmpty()) {
                logger.warn("Email o contraseña vacíos");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Email y contraseña no pueden estar vacíos"));
            }

            // Validar credenciales
            // CORREGIDO: Se usa getPassword()
            Optional<UsuarioSistema> usuario = usuarioService.validarCredenciales(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            );

            if (usuario.isPresent()) {
                UsuarioSistema usuarioAutenticado = usuario.get();
                logger.info("Login exitoso para usuario: {}", usuarioAutenticado.getNombre());

                // Generar JWT token
                // CORREGIDO: Se convierte Integer (getIdUsuario) a Long y se usa .name() para el Rol.
                String jwtToken = tokenProvider.generateToken(
                        Long.valueOf(usuarioAutenticado.getIdUsuario()),
                        usuarioAutenticado.getNombre(),
                        usuarioAutenticado.getRol().name()
                );

                logger.debug("JWT token generado para usuario: {}", usuarioAutenticado.getNombre());

                // Construcción de LoginResponse con JWT
                LoginResponse response = new LoginResponse();
                response.setIdUsuario(usuarioAutenticado.getIdUsuario());
                response.setNombre(usuarioAutenticado.getNombre());
                response.setEmail(usuarioAutenticado.getEmail());
                response.setRol(usuarioAutenticado.getRol() != null ? usuarioAutenticado.getRol().name() : "");
                response.setMensaje("Login exitoso");
                response.setToken(jwtToken);  // Agregar token JWT
                response.setTokenType("Bearer");

                return ResponseEntity.ok(ApiResponse.exitoso("Autenticación exitosa", response));
            } else {
                logger.warn("Credenciales inválidas para email: {}", loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Credenciales inválidas"));
            }

        } catch (IllegalArgumentException e) {
            logger.error("Error de validación en login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado en login: {}", e.getMessage(), e);
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