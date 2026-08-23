package com.example.backend.service;

import com.example.backend.dto.FileMetrics;
import com.example.backend.dto.MemoryMetrics;
import com.example.backend.dto.ProcessMetrics;
import org.springframework.stereotype.Service;

@Service
public class SimulationService {

    public ProcessMetrics getSimulatedProcesses(String escenario) {
        ProcessMetrics pm = new ProcessMetrics();
        // Valores base (Óptimos)
        pm.setProcesosActuales(150);
        pm.setProcesosMaximos(1000);
        pm.setSesionesActuales(120);
        pm.setSesionesActivas(20);
        pm.setSesionesInactivas(100);
        pm.setSesionesBloqueadas(0);
        pm.setOperacionesProlongadas(0);
        pm.setUsoRecursos(15);

        switch (escenario != null ? escenario.toLowerCase() : "") {
            case "optimo":
                pm.setProcesosActuales(200); // Uso muy bajo
                break;
            case "saludable":
                pm.setProcesosActuales(400); // Uso normal
                break;
            case "advertencia":
                pm.setProcesosActuales(650); // Uso en 65%
                pm.setOperacionesProlongadas(1);
                break;
            case "degradado":
                pm.setProcesosActuales(800); // Uso al 80%
                pm.setOperacionesProlongadas(3);
                break;
            case "critico_cap": // Cae procesos, salva el resto
                pm.setProcesosActuales(950);
                pm.setSesionesBloqueadas(2); // Esto tumba el índice de procesos
                break;
            case "critico_real": // Todo cae
                pm.setProcesosActuales(900);
                pm.setSesionesBloqueadas(5);
                break;
        }
        return pm;
    }

    public MemoryMetrics getSimulatedMemory(String escenario) {
        MemoryMetrics mm = new MemoryMetrics();
        // Valores base (Óptimos)
        mm.setSgaTotal(4294967296L);
        mm.setSgaLibre(1073741824L);
        mm.setSharedPool(1073741824L);
        mm.setBufferCache(2147483648L);
        mm.setPgaAsignada(1073741824L);
        mm.setPgaUtilizada(536870912L);
        mm.setPgaMaxima(1073741824L);
        mm.setPgaOverAllocation(0L);
        mm.setPgaCacheHit(100.0);

        switch (escenario != null ? escenario.toLowerCase() : "") {
            case "optimo":
                mm.setPgaCacheHit(98.0);
                break;
            case "saludable":
                mm.setPgaCacheHit(85.0);
                break;
            case "advertencia":
                mm.setPgaCacheHit(72.0);
                break;
            case "degradado":
                mm.setPgaCacheHit(55.0);
                break;
            case "critico_cap":
                mm.setPgaCacheHit(99.0); // Memoria perfecta
                break;
            case "critico_real":
                mm.setPgaCacheHit(20.0); // Memoria destruida
                mm.setPgaOverAllocation(500L);
                break;
        }
        return mm;
    }

    public FileMetrics getSimulatedFiles(String escenario) {
        FileMetrics fm = new FileMetrics();
        // Valores base (Óptimos)
        fm.setDatafilesOnline(10);
        fm.setDatafilesOffline(0);
        fm.setTamanoDatafiles(53687091200L);
        fm.setEspacioTablespaces(42949672960L);
        fm.setTempfiles(2);
        fm.setRedoLogs(3);
        fm.setArchivosInvalidos(0);
        fm.setArchivosInaccesibles(0);

        switch (escenario != null ? escenario.toLowerCase() : "") {
            case "optimo":
            case "saludable":
                break; // Quedan en 100
            case "advertencia":
                fm.setArchivosInvalidos(1); // Pequeña penalización
                break;
            case "degradado":
                fm.setArchivosInvalidos(3); // Penalización media
                break;
            case "critico_cap":
                break; // Quedan en 100
            case "critico_real":
                fm.setDatafilesOffline(2); // Falla crítica de archivos
                fm.setArchivosInaccesibles(1);
                break;
        }
        return fm;
    }
}
