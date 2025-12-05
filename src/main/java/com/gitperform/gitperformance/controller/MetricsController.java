package com.gitperform.gitperformance.controller;

import com.gitperform.gitperformance.dto.ApiResponse;
import com.gitperform.gitperformance.dto.metrics.*;
import com.gitperform.gitperformance.service.MetricsService;
import com.gitperform.gitperformance.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {
    
    private final MetricsService metricsService;
    private final ProjectService projectService;
    
    @PostMapping("/analyze/{projectId}")
    public ApiResponse<RepoMetricsDto> analyzeProject(
            @PathVariable Long projectId,
            @RequestParam Long userId,
            @RequestParam(required = false) Integer daysBack) {
        
        try {
            // Получаем GitHub токен пользователя
            String githubToken = metricsService.getGithubToken(userId);
            if (githubToken == null) {
                return new ApiResponse<>(false, "GitHub token not configured. Please connect your GitHub account.", null);
            }
            
            RepoMetricsDto metrics = metricsService.analyzeRepository(projectId, githubToken, daysBack);
            return new ApiResponse<>(true, "Analysis completed successfully", metrics);
            
        } catch (Exception e) {
            return new ApiResponse<>(false, "Analysis failed: " + e.getMessage(), null);
        }
    }
    
    @PostMapping("/analyze/custom")
    public ApiResponse<RepoMetricsDto> analyzeCustomRepository(
            @RequestBody AnalysisRequestDto request,
            @RequestParam Long userId) {
        
        try {
            String githubToken = metricsService.getGithubToken(userId);
            if (githubToken == null) {
                return new ApiResponse<>(false, "GitHub token not configured", null);
            }
            
            RepoMetricsDto metrics = metricsService.analyzeGitHubRepository(
                request.getOwner(),
                request.getRepoName(),
                githubToken,
                request.getDaysBack()
            );
            return new ApiResponse<>(true, "Analysis completed successfully", metrics);
            
        } catch (Exception e) {
            return new ApiResponse<>(false, "Analysis failed: " + e.getMessage(), null);
        }
    }
    
    @PostMapping("/github/connect")
    public ApiResponse<Boolean> connectGithubAccount(
            @RequestParam Long userId,
            @RequestBody GithubAuthDto authDto) {
        
        try {
            // Тестируем подключение
            boolean isValid = metricsService.testGithubConnection(authDto.getAccessToken());
            if (!isValid) {
                return new ApiResponse<>(false, "Invalid GitHub token", false);
            }
            
            metricsService.saveGithubToken(userId, authDto.getAccessToken(), authDto.getGithubUsername());
            return new ApiResponse<>(true, "GitHub account connected successfully", true);
            
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to connect GitHub account: " + e.getMessage(), false);
        }
    }
    
    @GetMapping("/github/status/{userId}")
    public ApiResponse<Boolean> checkGithubStatus(@PathVariable Long userId) {
        String token = metricsService.getGithubToken(userId);
        boolean isConnected = token != null && !token.isEmpty();
        
        String message = isConnected ? 
            "GitHub account is connected" : 
            "GitHub account is not connected";
        
        return new ApiResponse<>(true, message, isConnected);
    }
    
    @PostMapping("/github/test")
    public ApiResponse<Boolean> testGithubToken(@RequestBody GithubAuthDto authDto) {
        boolean isValid = metricsService.testGithubConnection(authDto.getAccessToken());
        String message = isValid ? 
            "GitHub token is valid" : 
            "Invalid GitHub token";
        
        return new ApiResponse<>(isValid, message, isValid);
    }
}