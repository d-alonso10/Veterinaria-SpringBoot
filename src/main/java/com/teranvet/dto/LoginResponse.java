package com.teranvet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements Serializable {
    
    private Integer idUsuario;
    private String nombre;
    private String email;
    private String rol;
    private String token;
}
