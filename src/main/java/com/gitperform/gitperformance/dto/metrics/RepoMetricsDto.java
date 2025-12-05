package com.gitperform.gitperformance.dto.metrics;

import lombok.Data;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@Data
public class RepoMetricsDto {
    private String repoName;
    private String owner;
    private LocalDateTime analysisDate;
    private int totalCommits;
    private int totalContributors;
    private int linesAdded;
    private int linesDeleted;
    private double avgCommitSize;
    private Map<String, Integer> commitFrequency; // по дням недели
    private Map<String, Integer> authorContributions;
    private List<CommitDto> recentCommits;
    private String mostActiveAuthor;
    private int mostActiveDayCommits;
}