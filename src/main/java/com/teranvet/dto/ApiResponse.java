package com.teranvet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> implements Serializable {
    
    private boolean exito;
    private String mensaje;
    private T datos;
    private String error;
    
    public static <T> ApiResponse<T> exitoso(String mensaje, T datos) {
        return new ApiResponse<>(true, mensaje, datos, null);
    }
    
    public static <T> ApiResponse<T> error(String mensaje, String error) {
        return new ApiResponse<>(false, mensaje, null, error);
    }
    
    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, "Error al procesar la solicitud", null, error);
    }
}
