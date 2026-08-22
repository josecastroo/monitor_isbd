package com.example.backend.repository;

import com.example.backend.dto.FileMetrics;
import com.example.backend.dto.MemoryMetrics;
import com.example.backend.dto.ProcessMetrics;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OracleExtractionRepository {
    private final JdbcTemplate jdbcTemplate;

    public OracleExtractionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ProcessMetrics getProcessMetrics() {
        ProcessMetrics metrics = new ProcessMetrics();

        // 1. Procesos actuales y límite (V$RESOURCE_LIMIT)[cite: 1]
        String sqlProcessLimit = "SELECT CURRENT_UTILIZATION, LIMIT_VALUE FROM V$RESOURCE_LIMIT WHERE RESOURCE_NAME = 'processes'";
        jdbcTemplate.query(sqlProcessLimit, rs -> {
            metrics.setProcesosActuales(rs.getInt("CURRENT_UTILIZATION"));
            metrics.setLimiteProcesos(rs.getInt("LIMIT_VALUE"));
        });

        // 2. Sesiones de usuario totales (V$SESSION)[cite: 1]
        String sqlTotalSessions = "SELECT COUNT(*) FROM V$SESSION WHERE TYPE = 'USER'";
        Integer totalSessions = jdbcTemplate.queryForObject(sqlTotalSessions, Integer.class);
        metrics.setSesionesTotales(totalSessions != null ? totalSessions : 0);

        // 3. Sesiones activas (ejecutando consultas)[cite: 1]
        String sqlActiveSessions = "SELECT COUNT(*) FROM V$SESSION WHERE TYPE = 'USER' AND STATUS = 'ACTIVE'";
        Integer activeSessions = jdbcTemplate.queryForObject(sqlActiveSessions, Integer.class);
        metrics.setSesionesActivas(activeSessions != null ? activeSessions : 0);

        // 4. Sesiones bloqueadas (esperando por otra sesión)[cite: 1]
        String sqlBlockedSessions = "SELECT COUNT(*) FROM V$SESSION WHERE BLOCKING_SESSION IS NOT NULL";
        Integer blockedSessions = jdbcTemplate.queryForObject(sqlBlockedSessions, Integer.class);
        metrics.setSesionesBloqueadas(blockedSessions != null ? blockedSessions : 0);

        return metrics;
    }

    public MemoryMetrics getMemoryMetrics() {
        MemoryMetrics metrics = new MemoryMetrics();

        // SGA: Total y Libre (V$SGAINFO)
        metrics.setSgaTotal(jdbcTemplate.queryForObject("SELECT BYTES FROM V$SGAINFO WHERE NAME = 'Maximum SGA Size'", Long.class));
        metrics.setSgaLibre(jdbcTemplate.queryForObject("SELECT BYTES FROM V$SGAINFO WHERE NAME = 'Free SGA Memory Available'", Long.class));

        // SGA: Shared Pool y Buffer Cache (V$SGASTAT)[cite: 1]
        metrics.setSharedPool(jdbcTemplate.queryForObject("SELECT SUM(BYTES) FROM V$SGASTAT WHERE POOL = 'shared pool'", Long.class));
        metrics.setBufferCache(jdbcTemplate.queryForObject("SELECT SUM(BYTES) FROM V$SGASTAT WHERE NAME = 'buffer_cache'", Long.class));

        // PGA: Estadísticas (V$PGASTAT)[cite: 1]
        metrics.setPgaAsignada(jdbcTemplate.queryForObject("SELECT VALUE FROM V$PGASTAT WHERE NAME = 'total PGA allocated'", Long.class));
        metrics.setPgaUtilizada(jdbcTemplate.queryForObject("SELECT VALUE FROM V$PGASTAT WHERE NAME = 'total PGA inuse'", Long.class));
        metrics.setPgaMaxima(jdbcTemplate.queryForObject("SELECT VALUE FROM V$PGASTAT WHERE NAME = 'maximum PGA allocated'", Long.class));
        metrics.setPgaOverAllocation(jdbcTemplate.queryForObject("SELECT VALUE FROM V$PGASTAT WHERE NAME = 'over allocation count'", Long.class));
        metrics.setPgaCacheHit(jdbcTemplate.queryForObject("SELECT VALUE FROM V$PGASTAT WHERE NAME = 'cache hit percentage'", Double.class));

        return metrics;
    }

    public FileMetrics getFileMetrics() {
        FileMetrics metrics = new FileMetrics();

        // Datafiles: Online/System vs Offline/Recover (V$DATAFILE)[cite: 1]
        metrics.setDatafilesOnline(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM V$DATAFILE WHERE STATUS IN ('ONLINE', 'SYSTEM')", Integer.class));
        metrics.setDatafilesOffline(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM V$DATAFILE WHERE STATUS NOT IN ('ONLINE', 'SYSTEM')", Integer.class));

        // Redo Logs: Normales vs Problemas (V$LOG)[cite: 1]
        metrics.setRedoLogsNormales(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM V$LOG WHERE STATUS IN ('CURRENT', 'INACTIVE', 'ACTIVE')", Integer.class));
        metrics.setRedoLogsProblemas(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM V$LOG WHERE STATUS NOT IN ('CURRENT', 'INACTIVE', 'ACTIVE')", Integer.class));

        return metrics;
    }
}
