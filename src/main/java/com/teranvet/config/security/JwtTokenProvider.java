package com.teranvet.config.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * JwtTokenProvider - Generador y validador de tokens JWT
 * Responsable de:
 * - Generar JWT tokens para usuarios autenticados
 * - Validar JWT tokens en peticiones entrantes
 * - Extraer información del usuario desde el token
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret:TeranVetSecretKeyForJWTTokenGenerationAndValidation2025}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")  // 24 horas en milisegundos
    private int jwtExpirationMs;

    /**
     * Genera un JWT token con la información del usuario
     *
     * @param idUsuario ID del usuario autenticado
     * @param nombreUsuario Nombre del usuario
     * @param rol Rol del usuario (recepcionista, admin, groomer, etc)
     * @return Token JWT generado
     */
    public String generateToken(Long idUsuario, String nombreUsuario, String rol) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        logger.info("Generando JWT token para usuario: {} con rol: {}", nombreUsuario, rol);

        return Jwts.builder()
                .setSubject(String.valueOf(idUsuario))
                .claim("nombre", nombreUsuario)
                .claim("rol", rol)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Extrae el ID del usuario desde el token JWT
     *
     * @param token Token JWT
     * @return ID del usuario
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extrae el nombre del usuario desde el token JWT
     *
     * @param token Token JWT
     * @return Nombre del usuario
     */
    public String getNombreFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("nombre", String.class);
    }

    /**
     * Extrae el rol del usuario desde el token JWT
     *
     * @param token Token JWT
     * @return Rol del usuario
     */
    public String getRolFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("rol", String.class);
    }

    /**
     * Valida si un token JWT es válido
     * - Verifica la firma
     * - Verifica que no esté expirado
     * - Verifica que contenga información requerida
     *
     * @param token Token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);
            logger.debug("Token JWT validado correctamente");
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT inválido: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT no soportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("Claims del token JWT vacío: {}", ex.getMessage());
        } catch (SignatureException ex) {
            logger.error("Firma del token JWT inválida: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Extrae el token JWT del Authorization header
     * Espera formato: "Bearer <token>"
     *
     * @param token Valor del Authorization header
     * @return Token limpio sin el prefijo "Bearer "
     */
    public String getTokenFromBearerString(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}
