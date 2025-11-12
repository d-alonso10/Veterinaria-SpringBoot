package com.teranvet.service;

import com.teranvet.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Servicio para métricas del Dashboard.
 * Proporciona datos agregados para la visualización en el dashboard.
 */
@Service
@Transactional
public class DashboardService {

    @Autowired
    private ReporteRepository reporteRepository;

    /**
     * Obtener métricas del dashboard para un rango de fechas.
     * Incluye: total clientes, citas hoy, ingresos, servicios completados, etc.
     */
    @Transactional(readOnly = true)
    public List<Map> obtenerMetricas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son requeridas");
        }
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        return reporteRepository.metricasDashboard(fechaInicio, fechaFin);
    }

    /**
     * Obtener cola actual en una sucursal específica.
     * Incluye: mascotas en espera, tiempo estimado, groomer asignado, etc.
     */
    @Transactional(readOnly = true)
    public List<Map> obtenerColaActual(Integer idSucursal) {
        if (idSucursal == null || idSucursal <= 0) {
            throw new IllegalArgumentException("ID de sucursal inválido");
        }
        
        return reporteRepository.colaActual(idSucursal);
    }

    /**
     * Obtener estadísticas mensuales para un año y mes específico.
     */
    @Transactional(readOnly = true)
    public List<Map> obtenerEstadisticasMensuales(Integer anio, Integer mes) {
        if (anio == null || anio < 2020 || anio > 2100) {
            throw new IllegalArgumentException("Año inválido");
        }
        
        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mes inválido (debe estar entre 1 y 12)");
        }
        
        return reporteRepository.estadisticasMensuales(anio, mes);
    }

    /**
     * Obtener próximas citas de un cliente.
     */
    @Transactional(readOnly = true)
    public List<Map> obtenerProximasCitas(Integer idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        
        return reporteRepository.proximasCitas(idCliente);
    }

    /**
     * Obtener historial de servicios de una mascota.
     */
    @Transactional(readOnly = true)
    public List<Map> obtenerHistorialMascota(Integer idMascota) {
        if (idMascota == null || idMascota <= 0) {
            throw new IllegalArgumentException("ID de mascota inválido");
        }
        
        return reporteRepository.historialMascota(idMascota);
    }
}
