package com.gitperform.gitperformance.dto.metrics;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class AnonymousRepoMetricsDto {
    private String repoName;
    private String owner;
    private LocalDateTime analysisDate;
    private int totalCommits;
    private int totalContributors;
    private int linesAdded;
    private int linesDeleted;
    private double avgCommitSize;
    private Map<String, Integer> commitFrequency;
    private Map<String, Integer> authorContributions;
    private List<CommitDto> recentCommits;
}
