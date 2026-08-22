package com.example.backend.controller;

import com.example.backend.dto.FileMetrics;
import com.example.backend.dto.HealthResult;
import com.example.backend.dto.MemoryMetrics;
import com.example.backend.dto.ProcessMetrics;
import com.example.backend.model.MonitorAlertas;
import com.example.backend.model.MonitorIndices;
import com.example.backend.repository.AlertasRepository;
import com.example.backend.repository.IndicesRepository;
import com.example.backend.repository.OracleExtractionRepository;
import com.example.backend.service.CalculationService;
import com.example.backend.service.HistoryService;
import com.example.backend.service.AlertService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private final OracleExtractionRepository oracleExtractionRepository;
    private final CalculationService calculationService;
    private final HistoryService historyService;
    private final AlertService alertService;
    private final IndicesRepository indicesRepository;
    private final AlertasRepository alertasRepository;

    public TestController(OracleExtractionRepository oracleExtractionRepository,
                          CalculationService calculationService,
                          HistoryService historyService,
                          AlertService alertService,
                          IndicesRepository indicesRepository,
                          AlertasRepository alertasRepository) {
        this.oracleExtractionRepository = oracleExtractionRepository;
        this.calculationService = calculationService;
        this.historyService = historyService;
        this.alertService = alertService;
        this.indicesRepository = indicesRepository;
        this.alertasRepository = alertasRepository;
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
        ProcessMetrics pm = oracleExtractionRepository.getProcessMetrics();
        MemoryMetrics mm = oracleExtractionRepository.getMemoryMetrics();
        FileMetrics fm = oracleExtractionRepository.getFileMetrics();

        // Calcular índices
        HealthResult resultado = calculationService.calculateHealth(pm, mm, fm);

        // Disparar motor de alertas[cite: 1]
        alertService.evaluarYGuardarAlertas(pm, mm, fm);

        // Guardar en historial y retornar
        return historyService.guardarHistorial(resultado);
    }

    @GetMapping("/historial")
    public List<MonitorIndices> getHistorial() {
        return indicesRepository.findTop20ByOrderByFechaHoraDesc();
    }

    // NUEVO: Endpoint para obtener las alertas
    @GetMapping("/alertas")
    public List<MonitorAlertas> getAlertas() {
        return alertasRepository.findTop20ByOrderByFechaHoraDesc();
    }
}
