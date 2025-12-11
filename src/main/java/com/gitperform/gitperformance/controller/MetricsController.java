package com.gitperform.gitperformance.controller;

import com.gitperform.gitperformance.dto.ApiResponse;
import com.gitperform.gitperformance.dto.metrics.*;
import com.gitperform.gitperformance.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {
    private final MetricsService metricsService;

    @PostMapping("/analyze/{projectId}")
    public ApiResponse<RepoMetricsDto> analyzeProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) Integer daysBack) {
        
        try {
            RepoMetricsDto metrics = metricsService.analyzeRepository(projectId, daysBack);
            return new ApiResponse<>(true, "Analysis completed successfully", metrics);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Analysis failed: " + e.getMessage(), null);
        }
    }

    @PostMapping("/analyze/custom")
    public ApiResponse<RepoMetricsDto> analyzeCustomRepository(
            @RequestBody AnalysisRequestDto request) {

        try {
            RepoMetricsDto metrics = metricsService.analyzeGitHubRepository(
                    request.getOwner(),
                    request.getRepoName(),
                    request.getDaysBack()
            );
            return new ApiResponse<>(true, "Analysis completed successfully", metrics);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Analysis failed: " + e.getMessage(), null);
        }
    }

}