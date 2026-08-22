package com.example.backend.repository;

import com.example.backend.dto.ProcessMetrics;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
