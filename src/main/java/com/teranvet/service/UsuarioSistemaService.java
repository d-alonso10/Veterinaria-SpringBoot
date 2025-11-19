package com.teranvet.service;

import com.teranvet.entity.UsuarioSistema;
import com.teranvet.repository.UsuarioSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de Usuarios del Sistema.
 * Incluye CRUD básico y validación de credenciales.
 */
@Service
@Transactional
public class UsuarioSistemaService {

    @Autowired
    private UsuarioSistemaRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtener todos los usuarios del sistema.
     *
     * @return Lista de todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<UsuarioSistema> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtener un usuario por ID.
     *
     * @param idUsuario ID del usuario
     * @return Optional con el usuario si existe
     */
    @Transactional(readOnly = true)
    public Optional<UsuarioSistema> obtenerPorId(Integer idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        return usuarioRepository.findById(idUsuario);
    }

    /**
     * Obtener un usuario por email.
     *
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    @Transactional(readOnly = true)
    public Optional<UsuarioSistema> obtenerPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Crear un nuevo usuario del sistema.
     *
     * @param usuario Objeto UsuarioSistema con los datos
     * @return Usuario creado
     */
    public UsuarioSistema crear(UsuarioSistema usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        // Validaciones
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario es requerido");
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email del usuario es requerido");
        }

        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña del usuario es requerida");
        }

        // Validar que el email sea único
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail());
        }

        // Encriptar contraseña
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));

        return usuarioRepository.save(usuario);
    }

    /**
     * Actualizar un usuario existente.
     *
     * @param idUsuario          ID del usuario a actualizar
     * @param usuarioActualizado Datos actualizados
     * @return Usuario actualizado
     */
    public UsuarioSistema actualizar(Integer idUsuario, UsuarioSistema usuarioActualizado) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }

        UsuarioSistema usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));

        if (usuarioActualizado.getNombre() != null && !usuarioActualizado.getNombre().trim().isEmpty()) {
            usuarioExistente.setNombre(usuarioActualizado.getNombre());
        }

        if (usuarioActualizado.getEmail() != null && !usuarioActualizado.getEmail().trim().isEmpty()) {
            String nuevoEmail = usuarioActualizado.getEmail();
            // Verificar que el nuevo email no esté en uso por otro usuario
            Optional<UsuarioSistema> existente = usuarioRepository.findByEmail(nuevoEmail);
            if (existente.isPresent() && !existente.get().getIdUsuario().equals(idUsuario)) {
                throw new IllegalArgumentException("Ya existe un usuario con el email: " + nuevoEmail);
            }
            usuarioExistente.setEmail(nuevoEmail);
        }

        if (usuarioActualizado.getRol() != null) {
            usuarioExistente.setRol(usuarioActualizado.getRol());
        }

        if (usuarioActualizado.getPasswordHash() != null && !usuarioActualizado.getPasswordHash().trim().isEmpty()) {
            usuarioExistente.setPasswordHash(passwordEncoder.encode(usuarioActualizado.getPasswordHash()));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * Eliminar un usuario.
     *
     * @param idUsuario ID del usuario a eliminar
     */
    public void eliminar(Integer idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario);
        }

        usuarioRepository.deleteById(idUsuario);
    }

    /**
     * Obtener usuarios por rol.
     *
     * @param rol Rol del usuario
     * @return Lista de usuarios con ese rol
     */
    @Transactional(readOnly = true)
    public List<UsuarioSistema> obtenerPorRol(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            throw new IllegalArgumentException("El rol es requerido");
        }

        return usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() != null && u.getRol().toString().equalsIgnoreCase(rol))
                .collect(Collectors.toList());
    }

    /**
     * Validar credenciales de usuario.
     *
     * @param email        Email del usuario
     * @param passwordHash Hash de la contraseña
     * @return Optional con el usuario si las credenciales son válidas
     */
    /**
     * Validar credenciales de usuario.
     * Soporta migración "lazy" de contraseñas en texto plano a BCrypt.
     *
     * @param email       Email del usuario
     * @param rawPassword Contraseña en texto plano
     * @return Optional con el usuario si las credenciales son válidas
     */
    public Optional<UsuarioSistema> validarCredenciales(String email, String rawPassword) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }

        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }

        Optional<UsuarioSistema> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            UsuarioSistema usuario = usuarioOpt.get();
            String storedPassword = usuario.getPasswordHash();

            // 1. Verificar si es un hash BCrypt válido (comienza con $2a$)
            if (passwordEncoder.matches(rawPassword, storedPassword)) {
                return usuarioOpt;
            }

            // 2. Si falla, verificar si es texto plano (Legacy)
            // Esto permite que los usuarios antiguos sigan entrando y se migren
            // automáticamente
            if (storedPassword.equals(rawPassword)) {
                // Migración automática: Encriptar y guardar
                usuario.setPasswordHash(passwordEncoder.encode(rawPassword));
                usuarioRepository.save(usuario);
                return usuarioOpt;
            }
        }

        return Optional.empty();
    }

    /**
     * Verificar existencia de usuario.
     *
     * @param idUsuario ID del usuario
     * @return true si existe, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean existe(Integer idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            return false;
        }
        return usuarioRepository.existsById(idUsuario);
    }

    /**
     * Cambiar contraseña de un usuario.
     *
     * @param idUsuario       ID del usuario
     * @param nuevaContraseña Nueva contraseña (hash)
     * @return Usuario actualizado
     */
    public UsuarioSistema cambiarContraseña(Integer idUsuario, String nuevaContraseña) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }

        if (nuevaContraseña == null || nuevaContraseña.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }

        UsuarioSistema usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));

        usuario.setPasswordHash(passwordEncoder.encode(nuevaContraseña));
        return usuarioRepository.save(usuario);
    }
}
