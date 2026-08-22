package com.example.backend.repository;

import com.example.backend.dto.FileMetrics;
import com.example.backend.dto.MemoryMetrics;
import com.example.backend.dto.ProcessMetrics;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class OracleExtractionRepository {
    private final JdbcTemplate jdbcTemplate;

    public OracleExtractionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ProcessMetrics getProcessMetrics() {
        ProcessMetrics metrics = new ProcessMetrics();

        // p1 y p2: Procesos actuales y máximos
        String sqlProcessLimit = "SELECT CURRENT_UTILIZATION, LIMIT_VALUE FROM V$RESOURCE_LIMIT WHERE RESOURCE_NAME = 'processes'";
        jdbcTemplate.query(sqlProcessLimit, rs -> {
            metrics.setProcesosActuales(rs.getInt("CURRENT_UTILIZATION"));
            metrics.setProcesosMaximos(rs.getInt("LIMIT_VALUE"));
        });

        // p3: Sesiones actuales
        Integer sesionesActuales = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM V$SESSION", Integer.class);
        metrics.setSesionesActuales(sesionesActuales != null ? sesionesActuales : 0);

        // p4: Sesiones activas
        Integer sesionesActivas = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM V$SESSION WHERE STATUS = 'ACTIVE'", Integer.class);
        metrics.setSesionesActivas(sesionesActivas != null ? sesionesActivas : 0);

        // p5: Sesiones inactivas
        Integer sesionesInactivas = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM V$SESSION WHERE STATUS = 'INACTIVE'", Integer.class);
        metrics.setSesionesInactivas(sesionesInactivas != null ? sesionesInactivas : 0);

        // p6: Sesiones bloqueadas
        Integer sesionesBloqueadas = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM V$SESSION WHERE BLOCKING_SESSION IS NOT NULL", Integer.class);
        metrics.setSesionesBloqueadas(sesionesBloqueadas != null ? sesionesBloqueadas : 0);

        // p7: Operaciones prolongadas
        Integer opProlongadas = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM V$SESSION_LONGOPS WHERE TIME_REMAINING > 0", Integer.class);
        metrics.setOperacionesProlongadas(opProlongadas != null ? opProlongadas : 0);

        // p8: Uso de recursos general (%)
        metrics.setUsoRecursos(0);

        return metrics;
    }

    public MemoryMetrics getMemoryMetrics() {
        MemoryMetrics metrics = new MemoryMetrics();

        // Usamos Number para evitar conflictos de conversión numérica de Oracle
        metrics.setSgaTotal(queryForLong("SELECT BYTES FROM V$SGAINFO WHERE NAME = 'Maximum SGA Size'"));
        metrics.setSgaLibre(queryForLong("SELECT BYTES FROM V$SGAINFO WHERE NAME = 'Free SGA Memory Available'"));
        metrics.setSharedPool(queryForLong("SELECT COALESCE(SUM(BYTES), 0) FROM V$SGASTAT WHERE LOWER(POOL) = 'shared pool'"));

        metrics.setBufferCache(queryForLong("SELECT COALESCE(SUM(BYTES), 0) FROM V$SGASTAT WHERE LOWER(NAME) LIKE '%buffer_cache%'"));

        metrics.setPgaAsignada(queryForLong("SELECT VALUE FROM V$PGASTAT WHERE NAME = 'total PGA allocated'"));
        metrics.setPgaUtilizada(queryForLong("SELECT VALUE FROM V$PGASTAT WHERE NAME = 'total PGA inuse'"));
        metrics.setPgaMaxima(queryForLong("SELECT VALUE FROM V$PGASTAT WHERE NAME = 'maximum PGA allocated'"));
        metrics.setPgaOverAllocation(queryForLong("SELECT VALUE FROM V$PGASTAT WHERE NAME = 'over allocation count'"));

        Double cacheHit = jdbcTemplate.queryForObject("SELECT VALUE FROM V$PGASTAT WHERE NAME = 'cache hit percentage'", Double.class);
        metrics.setPgaCacheHit(cacheHit != null ? cacheHit : 0.0);

        return metrics;
    }

    public FileMetrics getFileMetrics() {
        FileMetrics metrics = new FileMetrics();

        Integer dOnline = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM V$DATAFILE WHERE STATUS IN ('ONLINE', 'SYSTEM')", Integer.class);
        metrics.setDatafilesOnline(dOnline != null ? dOnline : 0);

        Integer dOffline = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM V$DATAFILE WHERE STATUS NOT IN ('ONLINE', 'SYSTEM')", Integer.class);
        metrics.setDatafilesOffline(dOffline != null ? dOffline : 0);

        metrics.setTamanoDatafiles(queryForLong("SELECT COALESCE(SUM(BYTES), 0) FROM V$DATAFILE"));
        metrics.setEspacioTablespaces(0L);

        Integer temp = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM V$TEMPFILE", Integer.class);
        metrics.setTempfiles(temp != null ? temp : 0);

        Integer rLogs = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM V$LOG", Integer.class);
        metrics.setRedoLogs(rLogs != null ? rLogs : 0);

        metrics.setArchivosInvalidos(0);
        metrics.setArchivosInaccesibles(0);

        return metrics;
    }

    // Método auxiliar privado para consultar números grandes sin errores de tipo
    private Long queryForLong(String sql) {
        try {
            Number val = jdbcTemplate.queryForObject(sql, Number.class);
            return val != null ? val.longValue() : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}
