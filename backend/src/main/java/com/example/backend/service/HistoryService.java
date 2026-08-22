package com.example.backend.service;

import com.example.backend.dto.HealthResult;
import com.example.backend.model.MonitorIndices;
import com.example.backend.repository.IndicesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HistoryService {
    private final IndicesRepository indicesRepository;

    public HistoryService(IndicesRepository indicesRepository) {
        this.indicesRepository = indicesRepository;
    }

    public MonitorIndices guardarHistorial(HealthResult result) {
        MonitorIndices registro = new MonitorIndices();
        registro.setFechaHora(LocalDateTime.now());
        registro.setIndiceProcesos(result.getIndiceProcesos());
        registro.setIndiceMemoria(result.getIndiceMemoria());
        registro.setIndiceArchivos(result.getIndiceArchivos());
        registro.setIndiceSalud(result.getIsbd());
        registro.setEstado(result.getEstado());

        // Guarda el registro en la tabla MONITOR_INDICES en Oracle[cite: 1]
        return indicesRepository.save(registro);
    }
}
