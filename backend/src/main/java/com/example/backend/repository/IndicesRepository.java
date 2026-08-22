package com.example.backend.repository;

import com.example.backend.model.MonitorIndices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.management.monitor.Monitor;
import java.util.List;

@Repository
public interface IndicesRepository extends JpaRepository<MonitorIndices, Long> {
    // Magia de Spring: Crea la consulta automáticamente por el nombre del método
    List<MonitorIndices> findTop20ByOrderByFechaHoraDesc();
}
