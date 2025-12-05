package com.gitperform.gitperformance.dto.metrics;

import lombok.Data;

@Data
public class AnalysisRequestDto {
    private String repoUrl;
    private String owner;
    private String repoName;
    private Integer daysBack; // анализ за последние N дней
    private String branch; // ветка для анализа
}