package com.example.backend.repository;

import com.example.backend.model.MonitorAlertas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertasRepository extends JpaRepository<MonitorAlertas, Long> {
}
