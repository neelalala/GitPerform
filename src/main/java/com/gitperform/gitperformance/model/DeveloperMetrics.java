package com.gitperform.gitperformance.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "developer_metrics")
@Getter
@Setter
public class DeveloperMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "developer_id")
    private Long developerId;

    @Column(name = "project_id")
    private Long projectId;

    private Integer commitCount;
    private Integer linesAdded;
    private Integer linesDeleted;
    private Integer filesChanged;
    private Integer bugsFixed;
    private Integer pullRequests;
    private Integer codeReviews;
    private Double codeQualityScore;
    private Double productivityScore;

    @Column(name = "period_start")
    private LocalDateTime periodStart;

    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    private LocalDateTime calculatedAt;

    // Конструкторы
    public DeveloperMetrics() {
        this.calculatedAt = LocalDateTime.now();
    }

    public DeveloperMetrics(Long developerId, Long projectId, LocalDateTime periodStart, LocalDateTime periodEnd) {
        this();
        this.developerId = developerId;
        this.projectId = projectId;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }
}