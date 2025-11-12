package com.teranvet.service;

import com.teranvet.entity.UsuarioSistema;
import com.teranvet.repository.UsuarioSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de Usuarios del Sistema.
 * Incluye CRUD básico y validación de credenciales.
 */
@Service
@Transactional
public class UsuarioSistemaService {

    @Autowired
    private UsuarioSistemaRepository usuarioRepository;

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
        
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualizar un usuario existente.
     *
     * @param idUsuario ID del usuario a actualizar
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
            usuarioExistente.setPasswordHash(usuarioActualizado.getPasswordHash());
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
                .toList();
    }

    /**
     * Validar credenciales de usuario.
     *
     * @param email Email del usuario
     * @param passwordHash Hash de la contraseña
     * @return Optional con el usuario si las credenciales son válidas
     */
    @Transactional(readOnly = true)
    public Optional<UsuarioSistema> validarCredenciales(String email, String passwordHash) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }
        
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }
        
        Optional<UsuarioSistema> usuario = usuarioRepository.findByEmail(email);
        
        if (usuario.isPresent() && usuario.get().getPasswordHash().equals(passwordHash)) {
            return usuario;
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
     * @param idUsuario ID del usuario
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
        
        usuario.setPasswordHash(nuevaContraseña);
        return usuarioRepository.save(usuario);
    }
}
