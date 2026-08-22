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
import com.example.backend.service.SimulationService;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final SimulationService simulationService;

    public TestController(OracleExtractionRepository oracleExtractionRepository,
                          CalculationService calculationService,
                          HistoryService historyService,
                          AlertService alertService,
                          IndicesRepository indicesRepository,
                          AlertasRepository alertasRepository,
                          SimulationService simulationService) {
        this.oracleExtractionRepository = oracleExtractionRepository;
        this.calculationService = calculationService;
        this.historyService = historyService;
        this.alertService = alertService;
        this.indicesRepository = indicesRepository;
        this.alertasRepository = alertasRepository;
        this.simulationService = simulationService;
    }

    @GetMapping("/procesos")
    public ProcessMetrics getProcesos(@RequestParam(required = false, defaultValue = "real") String escenario) {
        if ("real".equalsIgnoreCase(escenario)) {
            return oracleExtractionRepository.getProcessMetrics();
        }
        return simulationService.getSimulatedProcesses(escenario);
    }

    @GetMapping("/memoria")
    public MemoryMetrics getMemoria(@RequestParam(required = false, defaultValue = "real") String escenario) {
        if ("real".equalsIgnoreCase(escenario)) {
            return oracleExtractionRepository.getMemoryMetrics();
        }
        return simulationService.getSimulatedMemory(escenario);
    }

    @GetMapping("/archivos")
    public FileMetrics getArchivos(@RequestParam(required = false, defaultValue = "real") String escenario) {
        if ("real".equalsIgnoreCase(escenario)) {
            return oracleExtractionRepository.getFileMetrics();
        }
        return simulationService.getSimulatedFiles(escenario);
    }

    @GetMapping("/salud")
    public MonitorIndices getSaludGlobal(
            @RequestParam(required = false, defaultValue = "real") String escenario) { // <-- Agregamos el parámetro

        ProcessMetrics pm;
        MemoryMetrics mm;
        FileMetrics fm;

        // Evaluamos si usamos los datos reales de Oracle o los simulados
        if ("real".equalsIgnoreCase(escenario)) {
            pm = oracleExtractionRepository.getProcessMetrics();
            mm = oracleExtractionRepository.getMemoryMetrics();
            fm = oracleExtractionRepository.getFileMetrics();
        } else {
            pm = simulationService.getSimulatedProcesses(escenario);
            mm = simulationService.getSimulatedMemory(escenario);
            fm = simulationService.getSimulatedFiles(escenario);
        }

        // El motor de cálculo y alertas no se entera de que los datos son simulados
        HealthResult resultado = calculationService.calculateHealth(pm, mm, fm);
        alertService.evaluarYGuardarAlertas(pm, mm, fm);

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
