package com.teranvet.config;

import com.teranvet.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        /**
         * Maneja excepciones genéricas de RuntimeException
         */
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
                        RuntimeException ex,
                        WebRequest request) {
                log.error("RuntimeException: {}", ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Error en la operación", ex.getMessage()));
        }

        /**
         * Maneja excepciones de violación de integridad de datos (ej: unique
         * constraint)
         */
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
                        DataIntegrityViolationException ex,
                        WebRequest request) {
                log.error("Data Integrity Violation: {}", ex.getMessage(), ex);

                String mensaje = "Error de integridad de datos";
                String detalles = ex.getMessage();

                // Detectar violaciones de clave única
                if (detalles != null && detalles.contains("Duplicate entry")) {
                        if (detalles.contains("dni_ruc")) {
                                mensaje = "El DNI/RUC ya existe en el sistema";
                        } else if (detalles.contains("email")) {
                                mensaje = "El email ya existe en el sistema";
                        } else {
                                mensaje = "Ya existe un registro con esos datos";
                        }
                }
                // Detectar violaciones de clave foránea
                else if (detalles != null && detalles.contains("foreign key constraint")) {
                        mensaje = "No se puede realizar la operación porque el registro está relacionado con otros datos";
                }

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ApiResponse.error(mensaje, detalles));
        }

        /**
         * Maneja excepciones de validación
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
                        MethodArgumentNotValidException ex,
                        WebRequest request) {
                log.error("Validation Error: {}", ex.getMessage());
                String mensaje = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .findFirst()
                                .orElse("Error de validación");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Datos inválidos", mensaje));
        }

        /**
         * Maneja ResourceNotFoundException (404)
         */
        @ExceptionHandler(value = IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
                        IllegalArgumentException ex,
                        WebRequest request) {
                log.error("IllegalArgumentException: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error("Recurso no encontrado", ex.getMessage()));
        }

        /**
         * Maneja cualquier otra excepción no prevista
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleGlobalException(
                        Exception ex,
                        WebRequest request) {
                log.error("Excepción general no manejada: {}", ex.getClass().getName(), ex);

                // Mostrar el mensaje real de la excepción en desarrollo
                String mensaje = ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor";

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("Error interno del servidor", mensaje));
        }
}
