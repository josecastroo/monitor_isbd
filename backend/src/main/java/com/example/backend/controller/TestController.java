package com.example.backend.controller;

import com.example.backend.dto.FileMetrics;
import com.example.backend.dto.HealthResult;
import com.example.backend.dto.MemoryMetrics;
import com.example.backend.dto.ProcessMetrics;
import com.example.backend.repository.OracleExtractionRepository;
import com.example.backend.service.CalculationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private final OracleExtractionRepository oracleExtractionRepository;
    private final CalculationService calculationService;

    public TestController(OracleExtractionRepository oracleExtractionRepository, CalculationService calculationService) {
        this.oracleExtractionRepository = oracleExtractionRepository;
        this.calculationService = calculationService;
    }

    @GetMapping("/procesos")
    public ProcessMetrics getProcesos() {
        // Llamamos al repositorio que ejecuta los SELECT a las vistas de Oracle
        return oracleExtractionRepository.getProcessMetrics();
    }

    @GetMapping("/memoria")
    public MemoryMetrics getMemoria() {
        return oracleExtractionRepository.getMemoryMetrics();
    }

    @GetMapping("/archivos")
    public FileMetrics getArchivos() {
        return oracleExtractionRepository.getFileMetrics();
    }

    @GetMapping("/salud")
    public HealthResult getSaludGlobal() {
        // 1. Extraer los datos
        ProcessMetrics pm = oracleExtractionRepository.getProcessMetrics();
        MemoryMetrics mm = oracleExtractionRepository.getMemoryMetrics();
        FileMetrics fm = oracleExtractionRepository.getFileMetrics();

        // 2. Calcular los índices y retornar el resultado[cite: 1]
        return calculationService.calculateHealth(pm, mm, fm);
    }
}
