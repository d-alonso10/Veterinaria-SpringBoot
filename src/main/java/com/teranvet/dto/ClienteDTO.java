package com.teranvet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO implements Serializable {
    
    private Integer idCliente;
    private String nombre;
    private String apellido;
    private String dniRuc;
    private String email;
    private String telefono;
    private String direccion;
    private String preferencias;
}
