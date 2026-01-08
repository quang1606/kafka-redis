package com.example.kafkaredis.repository;

import com.example.kafkaredis.model.ViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewLogRepository extends JpaRepository<ViewLog, Long> {
}