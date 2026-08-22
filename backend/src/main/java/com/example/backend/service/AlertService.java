package com.example.backend.service;

import com.example.backend.dto.FileMetrics;
import com.example.backend.dto.MemoryMetrics;
import com.example.backend.dto.ProcessMetrics;
import com.example.backend.model.MonitorAlertas;
import com.example.backend.repository.AlertasRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlertService {
    private final AlertasRepository alertasRepository;

    public AlertService(AlertasRepository alertasRepository) {
        this.alertasRepository = alertasRepository;
    }

    public List<MonitorAlertas> evaluarYGuardarAlertas(ProcessMetrics pm, MemoryMetrics mm, FileMetrics fm) {
        List<MonitorAlertas> alertas = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        // 1. Evaluar Procesos[cite: 1]
        if (pm.getSesionesBloqueadas() != null && pm.getSesionesBloqueadas() > 0) {
            alertas.add(crearAlerta(ahora, "Procesos", "Sesiones Bloqueadas",
                    String.valueOf(pm.getSesionesBloqueadas()), "0", "Crítico", "Existen sesiones bloqueadas en la base de datos"));
        }

        int procActuales = pm.getProcesosActuales() != null ? pm.getProcesosActuales() : 0;
        int procLimite = (pm.getLimiteProcesos() != null && pm.getLimiteProcesos() > 0) ? pm.getLimiteProcesos() : 1000;
        double usoProcesos = (procActuales / (double) procLimite) * 100;

        if (usoProcesos >= 95) {
            alertas.add(crearAlerta(ahora, "Procesos", "Uso de Procesos", String.format("%.1f%%", usoProcesos), "95%", "Crítico", "Uso de procesos al límite"));
        } else if (usoProcesos >= 85) {
            alertas.add(crearAlerta(ahora, "Procesos", "Uso de Procesos", String.format("%.1f%%", usoProcesos), "85%", "Advertencia", "Uso de procesos elevado"));
        }

        // 2. Evaluar Memoria[cite: 1]
        if (mm.getPgaOverAllocation() != null && mm.getPgaOverAllocation() > 0) {
            alertas.add(crearAlerta(ahora, "Memoria", "PGA Over Allocation",
                    String.valueOf(mm.getPgaOverAllocation()), "0", "Advertencia", "Se detectó sobreasignación (presión) en la PGA"));
        }

        // 3. Evaluar Archivos[cite: 1]
        if (fm.getDatafilesOffline() != null && fm.getDatafilesOffline() > 0) {
            alertas.add(crearAlerta(ahora, "Archivos", "Datafiles Offline",
                    String.valueOf(fm.getDatafilesOffline()), "0", "Crítico", "Existen archivos de datos fuera de línea"));
        }

        if (fm.getRedoLogsProblemas() != null && fm.getRedoLogsProblemas() > 0) {
            alertas.add(crearAlerta(ahora, "Archivos", "Redo Logs",
                    String.valueOf(fm.getRedoLogsProblemas()), "0", "Crítico", "Problemas detectados en grupos de Redo Log"));
        }

        // Guardar todas las alertas generadas en Oracle[cite: 1]
        if (!alertas.isEmpty()) {
            alertasRepository.saveAll(alertas);
        }

        return alertas; // Retornamos la lista por si el controlador quiere mostrarla
    }

    private MonitorAlertas crearAlerta(LocalDateTime fecha, String componente, String variable, String valor, String umbral, String nivel, String descripcion) {
        MonitorAlertas alerta = new MonitorAlertas();
        alerta.setFechaHora(fecha);
        alerta.setComponente(componente);
        alerta.setVariable(variable);
        alerta.setValor(valor);
        alerta.setUmbral(umbral);
        alerta.setNivel(nivel);
        alerta.setDescripcion(descripcion);
        return alerta;
    }
}
