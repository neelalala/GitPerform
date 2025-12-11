package com.gitperform.gitperformance.service;

import com.gitperform.gitperformance.dto.metrics.CommitDto;
import com.gitperform.gitperformance.dto.metrics.RepoMetricsDto;
import com.gitperform.gitperformance.model.Project;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MetricsService {
    private final ProjectService projectService;

    public RepoMetricsDto analyzeRepository(Long projectId, Integer daysBack) {
        try {
            Project project = projectService.getProject(projectId);
            if (project == null) {
                throw new RuntimeException("Project not found");
            }

            String repoUrl = project.getRepoUrl();
            if (repoUrl == null || repoUrl.isEmpty()) {
                throw new RuntimeException("Repo URL not configured for this project");
            }

            String[] parts = extractOwnerAndRepo(repoUrl);
            return analyzeGitHubRepository(parts[0], parts[1], daysBack);

        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze repository: " + e.getMessage(), e);
        }
    }

    public RepoMetricsDto analyzeGitHubRepository(String owner, String repoName, Integer daysBack) {
        try {
            GitHub github = new GitHubBuilder().build();
            GHRepository repository = github.getRepository(owner + "/" + repoName);

            if (repository.isPrivate()) {
                throw new RuntimeException("Private repositories are not supported. Please use a public repository.");
            }

            LocalDateTime sinceDate = null;
            if (daysBack != null && daysBack > 0) {
                sinceDate = LocalDateTime.now().minusDays(daysBack);
            }
            return collectMetrics(repository, sinceDate);

        } catch (IOException e) {
            if (e.getMessage().contains("Not Found")) {
                throw new RuntimeException("Repository not found or is private");
            }
            throw new RuntimeException("GitHub API error: " + e.getMessage());
        }
    }
    
    private RepoMetricsDto collectMetrics(GHRepository repository, LocalDateTime sinceDate) throws IOException {
        RepoMetricsDto metrics = new RepoMetricsDto();
        metrics.setRepoName(repository.getName());
        metrics.setOwner(repository.getOwnerName());
        metrics.setAnalysisDate(LocalDateTime.now());

        List<CommitDto> commits = new ArrayList<>();
        Map<String, Integer> authorStats = new HashMap<>();
        Map<String, Integer> dayStats = new HashMap<>();
        
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");
        int totalAdditions = 0;
        int totalDeletions = 0;
        
        PagedIterable<GHCommit> ghCommits = repository.listCommits();
        for (GHCommit ghCommit : ghCommits) {
            CommitDto commitDto = convertToCommitDto(ghCommit);

            if (sinceDate != null && 
                commitDto.getCommitDate().isBefore(sinceDate)) {
                continue;
            }
            
            commits.add(commitDto);

            String author = commitDto.getAuthor();
            authorStats.put(author, authorStats.getOrDefault(author, 0) + 1);
            
            String dayOfWeek = commitDto.getCommitDate().format(dayFormatter);
            dayStats.put(dayOfWeek, dayStats.getOrDefault(dayOfWeek, 0) + 1);
            
            totalAdditions += commitDto.getAdditions();
            totalDeletions += commitDto.getDeletions();
        }

        metrics.setTotalCommits(commits.size());
        metrics.setTotalContributors(authorStats.size());
        metrics.setLinesAdded(totalAdditions);
        metrics.setLinesDeleted(totalDeletions);
        metrics.setAvgCommitSize(commits.isEmpty() ? 0 : 
            (double) (totalAdditions + totalDeletions) / commits.size());
        metrics.setCommitFrequency(dayStats);
        metrics.setAuthorContributions(authorStats);
        metrics.setRecentCommits(commits.stream()
            .sorted((c1, c2) -> c2.getCommitDate().compareTo(c1.getCommitDate()))
            .limit(10)
            .collect(Collectors.toList()));

        String mostActiveAuthor = authorStats.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("No authors");
        metrics.setMostActiveAuthor(mostActiveAuthor);

        int mostActiveDay = dayStats.values().stream()
            .max(Integer::compareTo)
            .orElse(0);
        metrics.setMostActiveDayCommits(mostActiveDay);
        
        return metrics;
    }
    
    private CommitDto convertToCommitDto(GHCommit ghCommit) throws IOException {
        CommitDto dto = new CommitDto();
        dto.setSha(ghCommit.getSHA1());
        
        if (ghCommit.getAuthor() != null) {
            String name = ghCommit.getAuthor().getName() != null ? ghCommit.getAuthor().getName() : ghCommit.getAuthor().getLogin();
            dto.setAuthor(name);
            dto.setAuthorEmail(ghCommit.getAuthor().getEmail());
        } else {
            dto.setAuthor("Unknown");
            dto.setAuthorEmail("unknown@example.com");
        }
        
        dto.setCommitDate(ghCommit.getCommitDate().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime());
        dto.setMessage(ghCommit.getCommitShortInfo().getMessage());

        try {

            List<GHCommit.File> files = ghCommit.listFiles().toList();
            int additions = 0;
            int deletions = 0;
            int total = 0;

            for (GHCommit.File file : files) {
                additions += file.getLinesAdded();
                deletions += file.getLinesDeleted();
                total += file.getLinesChanged();
            }

            dto.setAdditions(additions);
            dto.setDeletions(deletions);
            dto.setTotalChanges(total);

        } catch (Exception e) {
            throw new RemoteException("Could not get detailed stats for commit " + ghCommit.getSHA1()+ ", using fallback: " + e.getMessage());
        }

        dto.setUrl(ghCommit.getHtmlUrl().toString());
        return dto;
    }

    private String[] extractOwnerAndRepo(String url) {
        url = url.replace("https://github.com/", "")
                .replace("git@github.com:", "")
                .replace(".git", "");

        String[] parts = url.split("/");
        if (parts.length >= 2) {
            return new String[]{parts[0], parts[1]};
        }
        throw new IllegalArgumentException("Invalid GitHub URL format");
    }
}