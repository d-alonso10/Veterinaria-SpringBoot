package com.teranvet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Repositorio para acceso a datos de Reportes.
 * Mapea SPs para generación de reportes y métricas.
 */
@Repository
public interface ReporteRepository extends JpaRepository<Map, Integer> {

    /**
     * Obtener reporte de ingresos por rango de fechas.
     * Llamada a: sp_ReporteIngresos
     */
    @Query(value = "CALL sp_ReporteIngresos(:fechaInicio, :fechaFin, :idSucursal)", nativeQuery = true)
    List<Map> reporteIngresos(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("idSucursal") Integer idSucursal
    );

    /**
     * Obtener clientes frecuentes.
     * Llamada a: sp_ClientesFrecuentes
     */
    @Query(value = "CALL sp_ClientesFrecuentes()", nativeQuery = true)
    List<Map> clientesFrecuentes();

    /**
     * Obtener servicios más solicitados.
     * Llamada a: sp_ServiciosMasSolicitados
     */
    @Query(value = "CALL sp_ServiciosMasSolicitados(:fechaInicio, :fechaFin)", nativeQuery = true)
    List<Map> serviciosMasSolicitados(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    /**
     * Obtener métricas del dashboard.
     * Llamada a: sp_ObtenerMetricasDashboard
     */
    @Query(value = "CALL sp_ObtenerMetricasDashboard(:fechaInicio, :fechaFin)", nativeQuery = true)
    List<Map> metricasDashboard(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    /**
     * Obtener cola actual en una sucursal.
     * Llamada a: sp_ObtenerColaActual
     */
    @Query(value = "CALL sp_ObtenerColaActual(:idSucursal)", nativeQuery = true)
    List<Map> colaActual(@Param("idSucursal") Integer idSucursal);

    /**
     * Obtener estadísticas mensuales.
     * Llamada a: sp_ObtenerEstadisticasMensuales
     */
    @Query(value = "CALL sp_ObtenerEstadisticasMensuales(:anio, :mes)", nativeQuery = true)
    List<Map> estadisticasMensuales(
            @Param("anio") Integer anio,
            @Param("mes") Integer mes
    );

    /**
     * Obtener próximas citas de un cliente.
     * Llamada a: sp_ObtenerProximasCitas
     */
    @Query(value = "CALL sp_ObtenerProximasCitas(:idCliente)", nativeQuery = true)
    List<Map> proximasCitas(@Param("idCliente") Integer idCliente);

    /**
     * Obtener facturas por cliente.
     * Llamada a: sp_ObtenerFacturasPorCliente
     */
    @Query(value = "CALL sp_ObtenerFacturasPorCliente(:idCliente)", nativeQuery = true)
    List<Map> facturasPorCliente(@Param("idCliente") Integer idCliente);

    /**
     * Obtener pagos por factura.
     * Llamada a: sp_ObtenerPagosPorFactura
     */
    @Query(value = "CALL sp_ObtenerPagosPorFactura(:idFactura)", nativeQuery = true)
    List<Map> pagosPorFactura(@Param("idFactura") Integer idFactura);

    /**
     * Obtener historial de mascota.
     * Llamada a: sp_HistorialMascota
     */
    @Query(value = "CALL sp_HistorialMascota(:idMascota)", nativeQuery = true)
    List<Map> historialMascota(@Param("idMascota") Integer idMascota);

    /**
     * Obtener logs de auditoría.
     * Llamada a: sp_ObtenerLogsAuditoria
     */
    @Query(value = "CALL sp_ObtenerLogsAuditoria(:limite, :entidad, :accion)", nativeQuery = true)
    List<Map> logsAuditoria(
            @Param("limite") Integer limite,
            @Param("entidad") String entidad,
            @Param("accion") String accion
    );
}
