package com.gitperform.gitperformance.service;

import com.gitperform.gitperformance.dto.metrics.*;
import com.gitperform.gitperformance.model.Project;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${github.token}")
    private String githubToken;

    public RepoMetricsDto analyzeUserCommits(Long projectId, String username,
                                             LocalDateTime from, LocalDateTime to) {

        try {
            Project project = projectService.getProject(projectId);
            String[] parts = extractOwnerAndRepo(project.getRepoUrl());

            List<CommitDto> allCommits = getCommits(parts[0], parts[1], from, to);
            List<CommitDto> userCommits = allCommits.stream()
                    .filter(commit -> commit.getAuthor().equalsIgnoreCase(username))
                    .collect(Collectors.toList());

            return buildMetricsFromCommits(userCommits, parts[1], parts[0]);
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze user commits: " + e.getMessage(), e);
        }
    }

    public RepoMetricsDto analyzeTeamCommits(Long projectId,
                                             LocalDateTime from, LocalDateTime to) {
        try {
            Project project = projectService.getProject(projectId);
            String[] parts = extractOwnerAndRepo(project.getRepoUrl());

            List<CommitDto> commits = getCommits(parts[0], parts[1], from, to);
            return buildMetricsFromCommits(commits, parts[1], parts[0]);
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze team commits: " + e.getMessage(), e);
        }
    }

    public AnonymousRepoMetricsDto analyzeAnonymousTeamCommits(Long projectId,
                                                               LocalDateTime from, LocalDateTime to) {
        try {
            Project project = projectService.getProject(projectId);
            String[] parts = extractOwnerAndRepo(project.getRepoUrl());

            List<CommitDto> commits = getCommits(parts[0], parts[1], from, to);

            Map<String, String> authorToAnonym = new HashMap<>();
            int anonymousId = 1;
            List<CommitDto> anonymousCommits = new ArrayList<>();

            for (CommitDto commit : commits) {
                CommitDto anonymized = new CommitDto();
                anonymized.setSha(commit.getSha());


                String author = commit.getAuthor();
                if (!authorToAnonym.containsKey(author)) {
                    authorToAnonym.put(author, "User_" + anonymousId++);
                }
                String anonymizedAuthor = authorToAnonym.get(author);
                anonymized.setAuthor(anonymizedAuthor != null ? anonymizedAuthor : "Unknown");
                anonymized.setAuthorEmail("anonymous@example.com");

                anonymized.setCommitDate(commit.getCommitDate());
                anonymized.setMessage(commit.getMessage());
                anonymized.setAdditions(commit.getAdditions());
                anonymized.setDeletions(commit.getDeletions());
                anonymized.setTotalChanges(commit.getTotalChanges());
                anonymized.setUrl(commit.getUrl());

                if (commit.getChangedFiles() != null) {
                    anonymized.setChangedFiles(commit.getChangedFiles());
                }
                anonymized.setFilesChanged(commit.getFilesChanged());


                anonymousCommits.add(anonymized);
            }

            RepoMetricsDto metrics = buildMetricsFromCommits(anonymousCommits, parts[1], parts[0]);

            AnonymousRepoMetricsDto anonymousMetrics = new AnonymousRepoMetricsDto();
            anonymousMetrics.setRepoName(metrics.getRepoName());
            anonymousMetrics.setOwner(metrics.getOwner());
            anonymousMetrics.setAnalysisDate(metrics.getAnalysisDate());
            anonymousMetrics.setTotalCommits(metrics.getTotalCommits());
            anonymousMetrics.setTotalContributors(metrics.getTotalContributors());
            anonymousMetrics.setLinesAdded(metrics.getLinesAdded());
            anonymousMetrics.setLinesDeleted(metrics.getLinesDeleted());
            anonymousMetrics.setAvgCommitSize(metrics.getAvgCommitSize());
            anonymousMetrics.setCommitFrequency(metrics.getCommitFrequency());

            Map<String, Integer> anonymousAuthorStats = new HashMap<>();
            Map<String, Integer> originalAuthorStats = metrics.getAuthorContributions();

            if (originalAuthorStats != null) {
                for (Map.Entry<String, Integer> entry : originalAuthorStats.entrySet()) {
                    String originalAuthor = entry.getKey();
                    String anonymizedAuthor = authorToAnonym.get(originalAuthor);

                    if (anonymizedAuthor != null) {
                        anonymousAuthorStats.put(anonymizedAuthor, entry.getValue());
                    } else {
                        anonymousAuthorStats.put("User_Unknown", entry.getValue());
                    }
                }
            }
            anonymousMetrics.setAuthorContributions(anonymousAuthorStats);

            List<CommitDto> recentCommits = anonymousCommits.stream()
                    .filter(Objects::nonNull)
                    .filter(c -> c.getCommitDate() != null)
                    .sorted((c1, c2) -> c2.getCommitDate().compareTo(c1.getCommitDate()))
                    .limit(10)
                    .collect(Collectors.toList());
            anonymousMetrics.setRecentCommits(recentCommits);

            return anonymousMetrics;
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze anonymous commits: " + e.getMessage(), e);
        }
    }

    public PeriodComparisonDto comparePeriods(Long projectId,
                                              PeriodComparisonRequest request) {
        try {
            Project project = projectService.getProject(projectId);
            String[] parts = extractOwnerAndRepo(project.getRepoUrl());

            RepoMetricsDto firstPeriodMetrics = analyzeTeamCommits(
                    projectId,
                    request.getFirstPeriodStart(),
                    request.getFirstPeriodEnd()
            );

            RepoMetricsDto secondPeriodMetrics = analyzeTeamCommits(
                    projectId,
                    request.getSecondPeriodStart(),
                    request.getSecondPeriodEnd()
            );

            return compareMetrics(firstPeriodMetrics, secondPeriodMetrics);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compare periods: " + e.getMessage(), e);
        }
    }

    private PeriodComparisonDto compareMetrics(RepoMetricsDto first, RepoMetricsDto second) {
        PeriodComparisonDto comparison = new PeriodComparisonDto();
        comparison.setFirstPeriod(first);
        comparison.setSecondPeriod(second);

        // Рассчитываем различия
        comparison.setTotalCommitsDiff(second.getTotalCommits() - first.getTotalCommits());
        comparison.setTotalContributorsDiff(second.getTotalContributors() - first.getTotalContributors());
        comparison.setLinesAddedDiff(second.getLinesAdded() - first.getLinesAdded());
        comparison.setLinesDeletedDiff(second.getLinesDeleted() - first.getLinesDeleted());
        comparison.setAvgCommitSizeDiff(second.getAvgCommitSize() - first.getAvgCommitSize());

        // Процентные изменения
        if (first.getTotalCommits() > 0) {
            comparison.setTotalCommitsChangePercent(
                    ((double) comparison.getTotalCommitsDiff() / first.getTotalCommits()) * 100
            );
        }

        if (first.getLinesAdded() > 0) {
            comparison.setLinesAddedChangePercent(
                    ((double) comparison.getLinesAddedDiff() / first.getLinesAdded()) * 100
            );
        }

        return comparison;
    }

    private List<CommitDto> getCommits(String owner, String repoName, LocalDateTime from, LocalDateTime to) throws IOException {
        GitHub github = new GitHubBuilder().withOAuthToken(githubToken).build();
        GHRepository repository = github.getRepository(owner + "/" + repoName);

        if (repository.isPrivate()) {
            throw new RuntimeException("Private repositories are not supported. Please use a public repository.");
        }

        List<CommitDto> commits = new ArrayList<>();
        PagedIterable<GHCommit> ghCommits = repository.listCommits();

        for (GHCommit ghCommit : ghCommits) {
            CommitDto commitDto = convertToCommitDto(ghCommit);

            if (from != null && commitDto.getCommitDate().isBefore(from)) {
                continue;
            }
            if (to != null && commitDto.getCommitDate().isAfter(to)) {
                continue;
            }

            commits.add(commitDto);
        }

        return commits;
    }

    private RepoMetricsDto buildMetricsFromCommits(List<CommitDto> commits, String repoName, String owner) {
        RepoMetricsDto metrics = new RepoMetricsDto();
        metrics.setRepoName(repoName);
        metrics.setOwner(owner);
        metrics.setAnalysisDate(LocalDateTime.now());

        if (commits.isEmpty()) {
            return metrics;
        }

        Map<String, Integer> authorStats = new HashMap<>();
        Map<String, Integer> dayStats = new HashMap<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");
        int totalAdditions = 0;
        int totalDeletions = 0;

        for (CommitDto commit : commits) {
            String author = commit.getAuthor();
            authorStats.put(author, authorStats.getOrDefault(author, 0) + 1);

            String dayOfWeek = commit.getCommitDate().format(dayFormatter);
            dayStats.put(dayOfWeek, dayStats.getOrDefault(dayOfWeek, 0) + 1);

            totalAdditions += commit.getAdditions();
            totalDeletions += commit.getDeletions();
        }

        metrics.setTotalCommits(commits.size());
        metrics.setTotalContributors(authorStats.size());
        metrics.setLinesAdded(totalAdditions);
        metrics.setLinesDeleted(totalDeletions);
        metrics.setAvgCommitSize((double) (totalAdditions + totalDeletions) / commits.size());
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
            String name = ghCommit.getAuthor().getLogin();
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
            List<String> changedFiles = new ArrayList<>();

            for (GHCommit.File file : files) {
                additions += file.getLinesAdded();
                deletions += file.getLinesDeleted();
                total += file.getLinesChanged();
                changedFiles.add(file.getFileName());
            }

            dto.setAdditions(additions);
            dto.setDeletions(deletions);
            dto.setTotalChanges(total);
            dto.setChangedFiles(changedFiles);
            dto.setFilesChanged(files.size());

        } catch (Exception e) {
            throw new RemoteException("Could not get detailed stats for commit " + ghCommit.getSHA1() + ", using fallback: " + e.getMessage());
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