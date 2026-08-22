package com.example.backend.controller;

import com.example.backend.dto.FileMetrics;
import com.example.backend.dto.MemoryMetrics;
import com.example.backend.dto.ProcessMetrics;
import com.example.backend.repository.OracleExtractionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private final OracleExtractionRepository oracleExtractionRepository;

    public TestController(OracleExtractionRepository oracleExtractionRepository) {
        this.oracleExtractionRepository = oracleExtractionRepository;
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
}
