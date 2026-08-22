package com.example.backend.service;

import com.example.backend.dto.FileMetrics;
import com.example.backend.dto.MemoryMetrics;
import com.example.backend.dto.ProcessMetrics;
import org.springframework.stereotype.Service;

@Service
public class SimulationService {
    public ProcessMetrics getSimulatedProcesses(String escenario) {
        ProcessMetrics pm = new ProcessMetrics();
        pm.setProcesosActuales(50);
        pm.setProcesosMaximos(300); // p2
        pm.setSesionesActuales(40); // p3
        pm.setSesionesActivas(10);  // p4
        pm.setSesionesInactivas(30); // p5
        pm.setSesionesBloqueadas(0); // p6
        pm.setOperacionesProlongadas(0); // p7
        pm.setUsoRecursos(20);      // p8

        if ("procesos".equalsIgnoreCase(escenario)) {
            pm.setProcesosActuales(285);
            pm.setSesionesBloqueadas(5); // Falla en p6
        }
        return pm;
    }

    public MemoryMetrics getSimulatedMemory(String escenario) {
        MemoryMetrics mm = new MemoryMetrics();
        mm.setSgaTotal(2147483648L);      // m1
        mm.setSgaLibre(536870912L);       // m2
        mm.setSharedPool(804354560L);     // m3
        mm.setBufferCache(1073741824L);   // m4
        mm.setPgaAsignada(536870912L);    // m5
        mm.setPgaUtilizada(268435456L);   // m6
        mm.setPgaMaxima(1073741824L);     // m7
        mm.setPgaOverAllocation(0L);      // m8
        mm.setPgaCacheHit(99.0);          // m9

        if ("memoria".equalsIgnoreCase(escenario)) {
            mm.setPgaCacheHit(45.0);      // Falla en m9
            mm.setPgaOverAllocation(1500L); // Falla en m8
        }
        return mm;
    }

    public FileMetrics getSimulatedFiles(String escenario) {
        FileMetrics fm = new FileMetrics();
        fm.setDatafilesOnline(4);      // a1
        fm.setDatafilesOffline(0);     // a2
        fm.setTamanoDatafiles(50000000L); // a3
        fm.setEspacioTablespaces(80L); // a4
        fm.setTempfiles(2);            // a5
        fm.setRedoLogs(3);             // a6
        fm.setArchivosInvalidos(0);    // a7
        fm.setArchivosInaccesibles(0); // a8

        if ("archivos".equalsIgnoreCase(escenario)) {
            fm.setDatafilesOffline(1); // Falla en a2
            fm.setArchivosInvalidos(1); // Falla en a7
        }
        return fm;
    }
}
