package com.gitperform.gitperformance.dto.metrics;

import lombok.Data;

@Data
public class PeriodComparisonDto {
    private RepoMetricsDto firstPeriod;
    private RepoMetricsDto secondPeriod;

    private int totalCommitsDiff;
    private int totalContributorsDiff;
    private int linesAddedDiff;
    private int linesDeletedDiff;
    private double avgCommitSizeDiff;

    private double totalCommitsChangePercent;
    private double linesAddedChangePercent;

    private String comparisonSummary;
}