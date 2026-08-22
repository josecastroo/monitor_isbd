package com.example.backend.repository;

import com.example.backend.model.MonitorIndices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.management.monitor.Monitor;

@Repository
public interface IndicesRepository extends JpaRepository<MonitorIndices, Long> {
}
