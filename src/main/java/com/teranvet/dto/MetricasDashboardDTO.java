package com.teranvet.dto;

import java.math.BigDecimal;

/**
 * DTO para las métricas del Dashboard.
 * Representa los datos retornados por sp_ObtenerMetricasDashboard.
 */
public class MetricasDashboardDTO {

    private Integer totalClientes;
    private Integer totalMascotas;
    private Integer citasHoy;
    private BigDecimal ingresosPeriodo;
    private Integer atencionesEnCurso;

    // Constructor vacío
    public MetricasDashboardDTO() {
    }

    // Constructor con todos los campos
    public MetricasDashboardDTO(Integer totalClientes, Integer totalMascotas,
            Integer citasHoy, BigDecimal ingresosPeriodo,
            Integer atencionesEnCurso) {
        this.totalClientes = totalClientes;
        this.totalMascotas = totalMascotas;
        this.citasHoy = citasHoy;
        this.ingresosPeriodo = ingresosPeriodo;
        this.atencionesEnCurso = atencionesEnCurso;
    }

    // Getters y Setters
    public Integer getTotalClientes() {
        return totalClientes;
    }

    public void setTotalClientes(Integer totalClientes) {
        this.totalClientes = totalClientes;
    }

    public Integer getTotalMascotas() {
        return totalMascotas;
    }

    public void setTotalMascotas(Integer totalMascotas) {
        this.totalMascotas = totalMascotas;
    }

    public Integer getCitasHoy() {
        return citasHoy;
    }

    public void setCitasHoy(Integer citasHoy) {
        this.citasHoy = citasHoy;
    }

    public BigDecimal getIngresosPeriodo() {
        return ingresosPeriodo;
    }

    public void setIngresosPeriodo(BigDecimal ingresosPeriodo) {
        this.ingresosPeriodo = ingresosPeriodo;
    }

    public Integer getAtencionesEnCurso() {
        return atencionesEnCurso;
    }

    public void setAtencionesEnCurso(Integer atencionesEnCurso) {
        this.atencionesEnCurso = atencionesEnCurso;
    }

    @Override
    public String toString() {
        return "MetricasDashboardDTO{" +
                "totalClientes=" + totalClientes +
                ", totalMascotas=" + totalMascotas +
                ", citasHoy=" + citasHoy +
                ", ingresosPeriodo=" + ingresosPeriodo +
                ", atencionesEnCurso=" + atencionesEnCurso +
                '}';
    }
}
