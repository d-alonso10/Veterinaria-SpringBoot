package com.teranvet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaDTO implements Serializable {
    
    private Integer idCita;
    private Integer idMascota;
    private Integer idCliente;
    private Integer idSucursal;
    private Integer idServicio;
    private LocalDateTime fechaProgramada;
    private String modalidad;
    private String estado;
    private String notas;
}
