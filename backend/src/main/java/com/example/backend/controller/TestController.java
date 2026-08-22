package com.example.backend.controller;

import com.example.backend.dto.FileMetrics;
import com.example.backend.dto.HealthResult;
import com.example.backend.dto.MemoryMetrics;
import com.example.backend.dto.ProcessMetrics;
import com.example.backend.model.MonitorIndices;
import com.example.backend.repository.OracleExtractionRepository;
import com.example.backend.service.CalculationService;
import com.example.backend.service.HistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private final OracleExtractionRepository oracleExtractionRepository;
    private final CalculationService calculationService;
    private final HistoryService historyService;

    public TestController(OracleExtractionRepository oracleExtractionRepository,
                          CalculationService calculationService,
                          HistoryService historyService) {
        this.oracleExtractionRepository = oracleExtractionRepository;
        this.calculationService = calculationService;
        this.historyService = historyService;
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
    public MonitorIndices getSaludGlobal() {
        // 1. Extraer datos[cite: 1]
        ProcessMetrics pm = oracleExtractionRepository.getProcessMetrics();
        MemoryMetrics mm = oracleExtractionRepository.getMemoryMetrics();
        FileMetrics fm = oracleExtractionRepository.getFileMetrics();

        // 2. Calcular los índices[cite: 1]
        HealthResult resultado = calculationService.calculateHealth(pm, mm, fm);

        // 3. Guardar en Oracle y retornar el registro guardado[cite: 1]
        return historyService.guardarHistorial(resultado);
    }
}
