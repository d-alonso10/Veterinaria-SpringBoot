package com.teranvet.service;

import com.teranvet.dto.MetricasDashboardDTO;
import com.teranvet.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Obtener métricas del dashboard para un rango de fechas.
     * Incluye: total clientes, total mascotas, citas hoy, ingresos periodo,
     * atenciones en curso.
     * 
     * NOTA: Usa JdbcTemplate en lugar de JPA Repository
     * porque @Query(nativeQuery=true)
     * no mapea correctamente stored procedures con múltiples columnas a List<Map>.
     */
    @Transactional(readOnly = true)
    public MetricasDashboardDTO obtenerMetricas(LocalDate fechaInicio, LocalDate fechaFin) {
        // Validaciones
        if (fechaInicio == null) {
            throw new IllegalArgumentException("La fecha de inicio es requerida");
        }

        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        // Llamar al SP usando JdbcTemplate
        String sql = "CALL sp_ObtenerMetricasDashboard(?, ?)";

        return jdbcTemplate.queryForObject(sql,
                new Object[] { fechaInicio, fechaFin },
                (rs, rowNum) -> new MetricasDashboardDTO(
                        rs.getInt("total_clientes"),
                        rs.getInt("total_mascotas"),
                        rs.getInt("citas_hoy"),
                        rs.getBigDecimal("ingresos_periodo"),
                        rs.getInt("atenciones_en_curso")));
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
