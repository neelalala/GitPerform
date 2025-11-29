package com.gitperform.gitperformance.repository;

import com.gitperform.gitperformance.model.DeveloperMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeveloperMetricsRepository extends JpaRepository<DeveloperMetrics, Long> {

    // Найти метрики разработчика в конкретном проекте
    List<DeveloperMetrics> findByDeveloperIdAndProjectId(Long developerId, Long projectId);

    // Найти последние метрики разработчика
    DeveloperMetrics findTopByDeveloperIdAndProjectIdOrderByPeriodEndDesc(Long developerId, Long projectId);

    // Найти все метрики проекта
    List<DeveloperMetrics> findByProjectId(Long projectId);
}