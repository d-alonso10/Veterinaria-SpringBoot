package com.teranvet.service;

import com.teranvet.entity.AuditLog;
import com.teranvet.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de Logs de Auditoría.
 */
@Service
@Transactional
public class AuditService {

    @Autowired
    private AuditLogRepository auditRepository;

    /**
     * Obtener todos los logs de auditoría.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> obtenerTodos() {
        return auditRepository.findAll();
    }

    /**
     * Obtener un log por ID.
     */
    @Transactional(readOnly = true)
    public Optional<AuditLog> obtenerPorId(Integer idLog) {
        if (idLog == null || idLog <= 0) {
            throw new IllegalArgumentException("ID de log inválido");
        }
        return auditRepository.findById(idLog);
    }

    /**
     * Obtener logs por usuario.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> obtenerPorUsuario(Integer idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        return auditRepository.findAll().stream()
                .filter(log -> log.getIdUsuario() != null && log.getIdUsuario().equals(idUsuario))
                .toList();
    }

    /**
     * Obtener logs por fecha.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> obtenerPorFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es requerida");
        }
        return auditRepository.findAll().stream()
                .filter(log -> log.getTimestamp() != null && 
                       log.getTimestamp().toLocalDate().equals(fecha))
                .toList();
    }

    /**
     * Obtener logs por acción.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> obtenerPorAccion(String accion) {
        if (accion == null || accion.trim().isEmpty()) {
            throw new IllegalArgumentException("La acción es requerida");
        }
        return auditRepository.findAll().stream()
                .filter(log -> log.getAccion() != null && 
                       log.getAccion().equalsIgnoreCase(accion))
                .toList();
    }

    /**
     * Registrar un log de auditoría.
     */
    public AuditLog registrar(AuditLog auditLog) {
        if (auditLog == null) {
            throw new IllegalArgumentException("El log de auditoría no puede ser nulo");
        }
        return auditRepository.save(auditLog);
    }

    /**
     * Eliminar un log.
     */
    public void eliminar(Integer idLog) {
        if (idLog == null || idLog <= 0) {
            throw new IllegalArgumentException("ID de log inválido");
        }
        
        if (!auditRepository.existsById(idLog)) {
            throw new IllegalArgumentException("Log no encontrado con ID: " + idLog);
        }
        
        auditRepository.deleteById(idLog);
    }

    /**
     * Obtener logs de auditoría con límite.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> obtenerConLimite(Integer limite) {
        if (limite == null || limite <= 0) {
            throw new IllegalArgumentException("El límite debe ser mayor a 0");
        }
        return auditRepository.findAll().stream()
                .limit(limite)
                .toList();
    }
}
