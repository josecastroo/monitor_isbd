package com.example.backend.service;

import com.example.backend.dto.FileMetrics;
import com.example.backend.dto.HealthResult;
import com.example.backend.dto.MemoryMetrics;
import com.example.backend.dto.ProcessMetrics;
import org.springframework.stereotype.Service;

@Service
public class CalculationService {
    // Pesos iniciales propuestos en el diseño[cite: 1]
    private static final double WP = 0.30;
    private static final double WM = 0.35;
    private static final double WA = 0.35;

    public HealthResult calculateHealth(ProcessMetrics pMetrics, MemoryMetrics mMetrics, FileMetrics fMetrics) {
        HealthResult result = new HealthResult();

        // 1. Calcular IP (Índice de Procesos)[cite: 1]
        int procActuales = pMetrics.getProcesosActuales();
        int procMaximos = pMetrics.getProcesosMaximos() > 0 ? pMetrics.getProcesosMaximos() : 1000; // Evita división por cero
        int sesBloqueadas = pMetrics.getSesionesBloqueadas();

        double usoProcesos = (procActuales / (double) procMaximos) * 100;
        double ip = 100.0 - usoProcesos;
        if (sesBloqueadas > 0) {
            ip -= (sesBloqueadas * 10);
        }
        result.setIndiceProcesos(Math.max(0, Math.min(100, ip)));

        // 2. Calcular IM (Índice de Memoria)[cite: 1]
        double cacheHit = mMetrics.getPgaCacheHit() != null ? mMetrics.getPgaCacheHit() : 100.0;
        long overAlloc = mMetrics.getPgaOverAllocation() != null ? mMetrics.getPgaOverAllocation() : 0L;

        double im = cacheHit;
        if (overAlloc > 0) {
            im -= 20;
        }
        result.setIndiceMemoria(Math.max(0, Math.min(100, im)));

        // 3. Calcular IA (Índice de Archivos)[cite: 1]
        int dfOffline = fMetrics.getDatafilesOffline();
        int archInvalidos = fMetrics.getArchivosInvalidos();

        double ia = 100.0;
        if (dfOffline > 0) ia -= 50;
        if (archInvalidos > 0) ia -= 50;
        result.setIndiceArchivos(Math.max(0, Math.min(100, ia)));

        // 4. Calcular ISBD global (Promedio ponderado)[cite: 1]
        double isbd = (result.getIndiceProcesos() * WP) +
                (result.getIndiceMemoria() * WM) +
                (result.getIndiceArchivos() * WA);

        // Regla: El índice global no debe ocultar un problema crítico[cite: 1]
        if (result.getIndiceProcesos() < 40 || result.getIndiceMemoria() < 40 || result.getIndiceArchivos() < 40) {
            isbd = Math.min(isbd, 39.0);
        }

        result.setIsbd(Math.round(isbd * 100.0) / 100.0);
        result.setEstado(determinarEstado(result.getIsbd()));

        return result;
    }

    // Escala del índice[cite: 1]
    private String determinarEstado(double isbd) {
        if (isbd >= 90) return "Óptimo";
        if (isbd >= 75) return "Saludable";
        if (isbd >= 60) return "Advertencia";
        if (isbd >= 40) return "Degradado";
        return "Crítico";
    }
}
