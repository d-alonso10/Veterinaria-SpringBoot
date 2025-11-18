package com.teranvet.config.security;

import com.teranvet.entity.UsuarioSistema;
import com.teranvet.repository.UsuarioSistemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * CustomUserDetailsService - Carga detalles del usuario desde la BD
 * Requerido por Spring Security para autenticación
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UsuarioSistemaRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Buscando usuario con email: {}", email);

        UsuarioSistema usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado con email: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado: " + email);
                });

        logger.info("Usuario encontrado: {} con rol: {}", email, usuario.getRol());

        // Retorna UserDetails de Spring Security
        return new User(
                usuario.getEmail(),
                usuario.getPasswordHash(),
                Collections.singletonList(
                        // CORREGIDO: Se obtiene el nombre del Enum (.name()) antes de convertir a mayúsculas
                        new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name().toUpperCase())
                )
        );
    }
}