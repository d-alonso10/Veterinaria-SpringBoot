package com.teranvet.service;

import com.teranvet.entity.Sucursal;
import com.teranvet.repository.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de Sucursales.
 * Incluye CRUD básico.
 */
@Service
@Transactional
public class SucursalService {

    @Autowired
    private SucursalRepository sucursalRepository;

    /**
     * Obtener todas las sucursales.
     */
    @Transactional(readOnly = true)
    public List<Sucursal> obtenerTodas() {
        return sucursalRepository.findAll();
    }

    /**
     * Obtener una sucursal por ID.
     */
    @Transactional(readOnly = true)
    public Optional<Sucursal> obtenerPorId(Integer idSucursal) {
        if (idSucursal == null || idSucursal <= 0) {
            throw new IllegalArgumentException("ID de sucursal inválido");
        }
        return sucursalRepository.findById(idSucursal);
    }

    /**
     * Crear una nueva sucursal.
     */
    public Sucursal crear(Sucursal sucursal) {
        if (sucursal == null || sucursal.getNombre() == null || sucursal.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sucursal es requerido");
        }
        
        if (sucursal.getDireccion() == null || sucursal.getDireccion().trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección de la sucursal es requerida");
        }
        
        return sucursalRepository.save(sucursal);
    }

    /**
     * Actualizar una sucursal existente.
     */
    public Sucursal actualizar(Integer idSucursal, Sucursal sucursalActualizada) {
        if (idSucursal == null || idSucursal <= 0) {
            throw new IllegalArgumentException("ID de sucursal inválido");
        }
        
        Sucursal sucursalExistente = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada con ID: " + idSucursal));
        
        if (sucursalActualizada.getNombre() != null && !sucursalActualizada.getNombre().trim().isEmpty()) {
            sucursalExistente.setNombre(sucursalActualizada.getNombre());
        }
        
        if (sucursalActualizada.getDireccion() != null && !sucursalActualizada.getDireccion().trim().isEmpty()) {
            sucursalExistente.setDireccion(sucursalActualizada.getDireccion());
        }
        
        if (sucursalActualizada.getTelefono() != null && !sucursalActualizada.getTelefono().trim().isEmpty()) {
            sucursalExistente.setTelefono(sucursalActualizada.getTelefono());
        }
        
        return sucursalRepository.save(sucursalExistente);
    }

    /**
     * Eliminar una sucursal.
     */
    public void eliminar(Integer idSucursal) {
        if (idSucursal == null || idSucursal <= 0) {
            throw new IllegalArgumentException("ID de sucursal inválido");
        }
        
        if (!sucursalRepository.existsById(idSucursal)) {
            throw new IllegalArgumentException("Sucursal no encontrada con ID: " + idSucursal);
        }
        
        sucursalRepository.deleteById(idSucursal);
    }

    /**
     * Verificar existencia de sucursal.
     */
    @Transactional(readOnly = true)
    public boolean existe(Integer idSucursal) {
        if (idSucursal == null || idSucursal <= 0) {
            return false;
        }
        return sucursalRepository.existsById(idSucursal);
    }
}
