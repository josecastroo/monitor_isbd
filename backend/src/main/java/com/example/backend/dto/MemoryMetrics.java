package com.example.backend.dto;

import lombok.Data;

@Data
public class MemoryMetrics {
    private Long sgaTotal;
    private Long sgaLibre;
    private Long sharedPool;
    private Long bufferCache;
    private Long pgaAsignada;
    private Long pgaUtilizada;
    private Long pgaMaxima;
    private Long pgaOverAllocation;
    private Double pgaCacheHit;
}
