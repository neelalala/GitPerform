package com.gitperform.gitperformance.service;

import com.gitperform.gitperformance.dto.metrics.CommitDto;
import com.gitperform.gitperformance.dto.metrics.RepoMetricsDto;
import com.gitperform.gitperformance.model.GithubToken;
import com.gitperform.gitperformance.model.Project;
import com.gitperform.gitperformance.model.User;
import com.gitperform.gitperformance.repository.GithubTokenRepository;
import com.gitperform.gitperformance.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {
    
    private final GithubTokenRepository githubTokenRepository;
    private final ProjectRepository projectRepository;
    
    public RepoMetricsDto analyzeRepository(Long projectId, String githubToken, Integer daysBack) {
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            
            // Предполагаем, что в проекте хранится URL репозитория GitHub
            String repoUrl = project.getGithubUrl();
            if (repoUrl == null || repoUrl.isEmpty()) {
                throw new RuntimeException("GitHub URL not configured for this project");
            }
            
            // Извлекаем owner и repoName из URL
            String[] parts = extractOwnerAndRepo(repoUrl);
            String owner = parts[0];
            String repoName = parts[1];
            
            return analyzeGitHubRepository(owner, repoName, githubToken, daysBack);
            
        } catch (Exception e) {
            log.error("Error analyzing repository: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to analyze repository: " + e.getMessage());
        }
    }
    
    public RepoMetricsDto analyzeGitHubRepository(String owner, String repoName, String accessToken, Integer daysBack) {
        try {
            GitHub github = new GitHubBuilder().withOAuthToken(accessToken).build();
            GHRepository repository = github.getRepository(owner + "/" + repoName);
            
            LocalDateTime sinceDate = null;
            if (daysBack != null && daysBack > 0) {
                sinceDate = LocalDateTime.now().minusDays(daysBack);
            }
            
            return collectMetrics(repository, sinceDate);
            
        } catch (IOException e) {
            log.error("Error accessing GitHub API: {}", e.getMessage(), e);
            throw new RuntimeException("GitHub API error: " + e.getMessage());
        }
    }
    
    private RepoMetricsDto collectMetrics(GHRepository repository, LocalDateTime sinceDate) throws IOException {
        RepoMetricsDto metrics = new RepoMetricsDto();
        metrics.setRepoName(repository.getName());
        metrics.setOwner(repository.getOwnerName());
        metrics.setAnalysisDate(LocalDateTime.now());
        
        // Получаем коммиты
        List<CommitDto> commits = new ArrayList<>();
        Map<String, Integer> authorStats = new HashMap<>();
        Map<String, Integer> dayStats = new HashMap<>();
        
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");
        int totalAdditions = 0;
        int totalDeletions = 0;
        
        PagedIterable<GHCommit> ghCommits = repository.listCommits();
        for (GHCommit ghCommit : ghCommits) {
            CommitDto commitDto = convertToCommitDto(ghCommit);
            
            // Фильтрация по дате если нужно
            if (sinceDate != null && 
                commitDto.getCommitDate().isBefore(sinceDate)) {
                continue;
            }
            
            commits.add(commitDto);
            
            // Собираем статистику
            String author = commitDto.getAuthor();
            authorStats.put(author, authorStats.getOrDefault(author, 0) + 1);
            
            String dayOfWeek = commitDto.getCommitDate().format(dayFormatter);
            dayStats.put(dayOfWeek, dayStats.getOrDefault(dayOfWeek, 0) + 1);
            
            totalAdditions += commitDto.getAdditions();
            totalDeletions += commitDto.getDeletions();
        }
        
        // Рассчитываем метрики
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
        
        // Находим самого активного автора
        String mostActiveAuthor = authorStats.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("No authors");
        metrics.setMostActiveAuthor(mostActiveAuthor);
        
        // Находим самый активный день
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
            dto.setAuthor(ghCommit.getAuthor().getName());
            dto.setAuthorEmail(ghCommit.getAuthor().getEmail());
        } else {
            dto.setAuthor("Unknown");
            dto.setAuthorEmail("unknown@example.com");
        }
        
        dto.setCommitDate(ghCommit.getCommitDate().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime());
        dto.setMessage(ghCommit.getCommitShortInfo().getMessage());
        
        // Получаем статистику по файлам
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
            log.warn("Could not get detailed stats for commit {}, using fallback: {}", 
                     ghCommit.getSHA1(), e.getMessage());
        }
        
        dto.setUrl(ghCommit.getHtmlUrl().toString());
        return dto;
    }
    
    private String[] extractOwnerAndRepo(String url) {
        // Обрабатываем разные форматы URL
        url = url.replace("https://github.com/", "")
                .replace("git@github.com:", "")
                .replace(".git", "");
        
        String[] parts = url.split("/");
        if (parts.length >= 2) {
            return new String[]{parts[0], parts[1]};
        }
        throw new IllegalArgumentException("Invalid GitHub URL format");
    }
    
    public void saveGithubToken(Long userId, String accessToken, String githubUsername) {
        GithubToken token = githubTokenRepository.findByUserId(userId)
                .orElse(new GithubToken());
        
        token.setUserId(userId);
        token.setAccessToken(accessToken);
        token.setGithubUsername(githubUsername);
        token.setUpdatedAt(LocalDateTime.now());
        
        githubTokenRepository.save(token);
    }
    
    public String getGithubToken(Long userId) {
        return githubTokenRepository.findByUserId(userId)
                .map(GithubToken::getAccessToken)
                .orElse(null);
    }
    
    public boolean testGithubConnection(String accessToken) {
        try {
            GitHub github = new GitHubBuilder().withOAuthToken(accessToken).build();
            github.checkApiUrlValidity();
            return true;
        } catch (IOException e) {
            log.error("GitHub connection test failed: {}", e.getMessage());
            return false;
        }
    }
}