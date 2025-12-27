package com.gitperform.gitperformance.dto.metrics;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommitDto {
    private String sha;
    private String author;
    private String authorEmail;
    private LocalDateTime commitDate;
    private String message;
    private int additions;
    private int deletions;
    private int totalChanges;
    private String url;
    private List<String> changedFiles;
    private int filesChanged;
}