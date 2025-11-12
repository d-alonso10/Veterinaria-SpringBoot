package com.teranvet.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * JwtRequestFilter - Filtro para procesar tokens JWT en peticiones
 * Intercepta TODAS las peticiones HTTP y:
 * 1. Extrae el token JWT del Authorization header
 * 2. Valida el token
 * 3. Extrae la información del usuario
 * 4. Establece la autenticación en el contexto de Spring Security
 */
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extrae el JWT token del request
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Token válido - extrae información del usuario
                Long userId = tokenProvider.getUserIdFromToken(jwt);
                String nombreUsuario = tokenProvider.getNombreFromToken(jwt);
                String rol = tokenProvider.getRolFromToken(jwt);

                logger.debug("Token JWT válido para usuario: {} con rol: {}", nombreUsuario, rol);

                // Crea objeto de autenticación
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                Collections.singletonList(authority)
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establece en contexto de Spring Security
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Autenticación establecida en SecurityContext para usuario: {}", nombreUsuario);
            } else if (StringUtils.hasText(jwt)) {
                logger.warn("Token JWT inválido en la petición");
            }
        } catch (Exception ex) {
            logger.error("No se pudo establecer autenticación del usuario en contexto de seguridad: {}", ex.getMessage());
        }

        // Continúa con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el JWT token del Authorization header
     * Espera formato: "Bearer <token>"
     *
     * @param request HttpServletRequest
     * @return Token JWT o null si no existe
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
