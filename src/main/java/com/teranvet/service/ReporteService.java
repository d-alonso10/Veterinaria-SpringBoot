package com.teranvet.service;

import com.teranvet.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Servicio para generación de Reportes.
 * Proporciona reportes detallados sobre operaciones del negocio.
 */
@Service
@Transactional
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    /**
     * Obtener reporte de ingresos por rango de fechas y sucursal.
     *
     * @param fechaInicio Fecha de inicio del reporte
     * @param fechaFin Fecha de fin del reporte
     * @param idSucursal ID de la sucursal
     * @return Reporte de ingresos
     */
    @Transactional(readOnly = true)
    public List<Map> reporteIngresos(LocalDate fechaInicio, LocalDate fechaFin, Integer idSucursal) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son requeridas");
        }
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        if (idSucursal == null || idSucursal <= 0) {
            throw new IllegalArgumentException("ID de sucursal inválido");
        }
        
        return reporteRepository.reporteIngresos(fechaInicio, fechaFin, idSucursal);
    }

    /**
     * Obtener reporte de clientes frecuentes.
     * Muestra los clientes con más atenciones registradas.
     *
     * @return Reporte de clientes frecuentes
     */
    @Transactional(readOnly = true)
    public List<Map> reporteClientesFrecuentes() {
        return reporteRepository.clientesFrecuentes();
    }

    /**
     * Obtener reporte de servicios más solicitados.
     *
     * @param fechaInicio Fecha de inicio del análisis
     * @param fechaFin Fecha de fin del análisis
     * @return Reporte de servicios más solicitados
     */
    @Transactional(readOnly = true)
    public List<Map> reporteServiciosMasSolicitados(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son requeridas");
        }
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        return reporteRepository.serviciosMasSolicitados(fechaInicio, fechaFin);
    }

    /**
     * Obtener reporte de facturas por cliente.
     *
     * @param idCliente ID del cliente
     * @return Facturas del cliente
     */
    @Transactional(readOnly = true)
    public List<Map> reporteFacturasPorCliente(Integer idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        
        return reporteRepository.facturasPorCliente(idCliente);
    }

    /**
     * Obtener reporte de pagos por factura.
     *
     * @param idFactura ID de la factura
     * @return Pagos registrados para la factura
     */
    @Transactional(readOnly = true)
    public List<Map> reportePagosPorFactura(Integer idFactura) {
        if (idFactura == null || idFactura <= 0) {
            throw new IllegalArgumentException("ID de factura inválido");
        }
        
        return reporteRepository.pagosPorFactura(idFactura);
    }

    /**
     * Obtener reporte de logs de auditoría.
     *
     * @param limite Número máximo de registros
     * @param entidad Tipo de entidad (opcional)
     * @param accion Tipo de acción (INSERT, UPDATE, DELETE)
     * @return Logs de auditoría
     */
    @Transactional(readOnly = true)
    public List<Map> reporteLogsAuditoria(Integer limite, String entidad, String accion) {
        if (limite == null || limite <= 0) {
            limite = 100;
        }
        
        return reporteRepository.logsAuditoria(limite, entidad, accion);
    }

    /**
     * Obtener resumen general del negocio.
     * Combina métricas clave para una vista rápida de la salud del negocio.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Resumen general
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerResumeGeneral(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son requeridas");
        }
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        Map<String, Object> resumen = new java.util.HashMap<>();
        
        // Aquí se podrían agregar llamadas a múltiples SPs para construir un resumen completo
        // Por ahora es un placeholder que será expandido
        resumen.put("fechaInicio", fechaInicio);
        resumen.put("fechaFin", fechaFin);
        resumen.put("mensaje", "Resumen general del período");
        
        return resumen;
    }
}
