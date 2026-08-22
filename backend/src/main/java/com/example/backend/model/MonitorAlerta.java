package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "MONITOR_ALERTAS")
public class MonitorAlerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "componente")
    private String componente; // Ej: "Procesos", "Memoria", "Archivos"[cite: 1]

    @Column(name = "variable")
    private String variable; // Ej: "Uso de Buffer Cache", "PGA elevada"[cite: 1]

    @Column(name = "valor")
    private String valor;

    @Column(name = "umbral")
    private String umbral;

    @Column(name = "nivel")
    private String nivel; // normal, advertencia, crítico

    @Column(name = "descripcion")
    private String descripcion;
}
