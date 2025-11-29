package com.gitperform.gitperformance.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@Entity
@Table(name = "team_metrics")
public class TeamMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "team_id")
    private String teamId;

    @ElementCollection
    @CollectionTable(name = "team_metrics_data",
            joinColumns = @JoinColumn(name = "team_metrics_id"))
    @MapKeyColumn(name = "metric_name")
    @Column(name = "metric_value")
    private Map<String, String> metrics = new HashMap<>();

    @Column(name = "period_start")
    private LocalDateTime periodStart;

    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    private LocalDateTime calculatedAt;

    // Дополнительные агрегированные поля для удобства
    private Integer totalCommits;
    private Integer totalLinesAdded;
    private Integer totalLinesDeleted;
    private Integer activeDevelopers;
    private Double averageCycleTime;
    private Double codeCoverage;
    private Double teamProductivityScore;

    // Конструкторы
    public TeamMetrics() {
        this.calculatedAt = LocalDateTime.now();
    }

    public TeamMetrics(Long projectId, LocalDateTime periodStart, LocalDateTime periodEnd) {
        this();
        this.projectId = projectId;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }

    public Map<String, String> getMetrics() { return metrics; }
    public void setMetrics(Map<String, String> metrics) { this.metrics = metrics; }

    public LocalDateTime getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }

    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }

    public Integer getTotalCommits() { return totalCommits; }
    public void setTotalCommits(Integer totalCommits) { this.totalCommits = totalCommits; }

    public Integer getTotalLinesAdded() { return totalLinesAdded; }
    public void setTotalLinesAdded(Integer totalLinesAdded) { this.totalLinesAdded = totalLinesAdded; }

    public Integer getTotalLinesDeleted() { return totalLinesDeleted; }
    public void setTotalLinesDeleted(Integer totalLinesDeleted) { this.totalLinesDeleted = totalLinesDeleted; }

    public Integer getActiveDevelopers() { return activeDevelopers; }
    public void setActiveDevelopers(Integer activeDevelopers) { this.activeDevelopers = activeDevelopers; }

    public Double getAverageCycleTime() { return averageCycleTime; }
    public void setAverageCycleTime(Double averageCycleTime) { this.averageCycleTime = averageCycleTime; }

    public Double getCodeCoverage() { return codeCoverage; }
    public void setCodeCoverage(Double codeCoverage) { this.codeCoverage = codeCoverage; }

    public Double getTeamProductivityScore() { return teamProductivityScore; }
    public void setTeamProductivityScore(Double teamProductivityScore) { this.teamProductivityScore = teamProductivityScore; }

    // Вспомогательные методы для работы с метриками
    public void addMetric(String name, String value) {
        this.metrics.put(name, value);
    }

    public String getMetric(String name) {
        return this.metrics.get(name);
    }
}
