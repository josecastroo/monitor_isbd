package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "MONITOR_INDICES")
public class MonitorIndices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "indice_procesos")
    private Double indiceProcesos;

    @Column(name = "indice_memoria")
    private Double indiceMemoria;

    @Column(name = "indice_archivos")
    private Double indiceArchivos;

    @Column(name = "indice_salud")
    private Double indiceSalud;

    @Column(name = "estado")
    private String estado; // optimo, saludable, advertencia, degradado, critico
}
