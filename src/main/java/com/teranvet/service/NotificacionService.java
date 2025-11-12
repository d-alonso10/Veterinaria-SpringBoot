package com.teranvet.service;

import com.teranvet.entity.Notificacion;
import com.teranvet.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de Notificaciones.
 */
@Service
@Transactional
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    /**
     * Obtener todas las notificaciones.
     */
    @Transactional(readOnly = true)
    public List<Notificacion> obtenerTodas() {
        return notificacionRepository.findAll();
    }

    /**
     * Obtener una notificación por ID.
     */
    @Transactional(readOnly = true)
    public Optional<Notificacion> obtenerPorId(Integer idNotificacion) {
        if (idNotificacion == null || idNotificacion <= 0) {
            throw new IllegalArgumentException("ID de notificación inválido");
        }
        return notificacionRepository.findById(idNotificacion);
    }

    /**
     * Obtener notificaciones por cliente.
     */
    @Transactional(readOnly = true)
    public List<Notificacion> obtenerPorCliente(Integer idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        return notificacionRepository.findAll().stream()
                .filter(n -> n.getDestinatarioId() != null && n.getDestinatarioId().equals(idCliente))
                .toList();
    }

    /**
     * Obtener notificaciones pendientes.
     */
    @Transactional(readOnly = true)
    public List<Notificacion> obtenerPendientes() {
        return notificacionRepository.findAll().stream()
                .filter(n -> n.getEstado() != null && n.getEstado().equals("pendiente"))
                .toList();
    }

    /**
     * Crear una nueva notificación.
     */
    public Notificacion crear(Notificacion notificacion) {
        if (notificacion == null) {
            throw new IllegalArgumentException("La notificación no puede ser nula");
        }
        
        if (notificacion.getDestinatarioId() == null || notificacion.getDestinatarioId() <= 0) {
            throw new IllegalArgumentException("El ID del destinatario es requerido");
        }
        
        if (notificacion.getContenido() == null || notificacion.getContenido().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido es requerido");
        }
        
        // Establecer estado por defecto si no está definido
        if (notificacion.getEstado() == null || notificacion.getEstado().isEmpty()) {
            notificacion.setEstado("pendiente");
        }
        
        return notificacionRepository.save(notificacion);
    }

    /**
     * Actualizar una notificación.
     */
    public Notificacion actualizar(Integer idNotificacion, Notificacion notificacionActualizada) {
        if (idNotificacion == null || idNotificacion <= 0) {
            throw new IllegalArgumentException("ID de notificación inválido");
        }
        
        Notificacion notificacionExistente = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada con ID: " + idNotificacion));
        
        if (notificacionActualizada.getEstado() != null && !notificacionActualizada.getEstado().isEmpty()) {
            notificacionExistente.setEstado(notificacionActualizada.getEstado());
        }
        
        if (notificacionActualizada.getContenido() != null && !notificacionActualizada.getContenido().isEmpty()) {
            notificacionExistente.setContenido(notificacionActualizada.getContenido());
        }
        
        return notificacionRepository.save(notificacionExistente);
    }

    /**
     * Marcar notificación como enviada.
     */
    public Notificacion marcarEnviada(Integer idNotificacion) {
        if (idNotificacion == null || idNotificacion <= 0) {
            throw new IllegalArgumentException("ID de notificación inválido");
        }
        
        Notificacion notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada con ID: " + idNotificacion));
        
        notificacion.setEstado("enviado");
        return notificacionRepository.save(notificacion);
    }

    /**
     * Marcar notificación como leída.
     */
    public Notificacion marcarLeida(Integer idNotificacion) {
        if (idNotificacion == null || idNotificacion <= 0) {
            throw new IllegalArgumentException("ID de notificación inválido");
        }
        
        Notificacion notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada con ID: " + idNotificacion));
        
        notificacion.setEstado("leido");
        return notificacionRepository.save(notificacion);
    }

    /**
     * Eliminar una notificación.
     */
    public void eliminar(Integer idNotificacion) {
        if (idNotificacion == null || idNotificacion <= 0) {
            throw new IllegalArgumentException("ID de notificación inválido");
        }
        
        if (!notificacionRepository.existsById(idNotificacion)) {
            throw new IllegalArgumentException("Notificación no encontrada con ID: " + idNotificacion);
        }
        
        notificacionRepository.deleteById(idNotificacion);
    }

    /**
     * Obtener notificaciones no leídas de un cliente.
     */
    @Transactional(readOnly = true)
    public List<Notificacion> obtenerNoLeidas(Integer idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        
        return notificacionRepository.findAll().stream()
                .filter(n -> n.getDestinatarioId() != null && n.getDestinatarioId().equals(idCliente) &&
                           n.getEstado() != null && !n.getEstado().equals("leido"))
                .toList();
    }
}
