package com.gitperform.gitperformance.repository;

import com.gitperform.gitperformance.model.TeamMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamMetricsRepository extends JpaRepository<TeamMetrics, Long> {

    // Найти метрики команды для проекта
    List<TeamMetrics> findByProjectId(Long projectId);

    // Найти последние метрики команды
    TeamMetrics findTopByProjectIdOrderByPeriodEndDesc(Long projectId);
}