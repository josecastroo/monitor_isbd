package com.example.backend.dto;

import lombok.Data;

@Data
public class HealthResult {
    private Double indiceProcesos;
    private Double indiceMemoria;
    private Double indiceArchivos;
    private Double isbd;
    private String estado; // optimo, saludable, advertencia
}
