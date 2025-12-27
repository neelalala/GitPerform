package com.gitperform.gitperformance.controller;

import com.gitperform.gitperformance.dto.ApiResponse;
import com.gitperform.gitperformance.dto.metrics.*;
import com.gitperform.gitperformance.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/metrics/{projectId}")
@RequiredArgsConstructor
public class MetricsController {
    private final MetricsService metricsService;

    @GetMapping("/user-commits")
    public ApiResponse<RepoMetricsDto> analyzeUserCommits(
            @PathVariable Long projectId,
            @RequestParam String username,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        RepoMetricsDto metrics = metricsService.analyzeUserCommits(projectId, username, from, to);
        return new ApiResponse<>(true, "User commit analysis completed", metrics);
    }

    @GetMapping("/team-commits")
    public ApiResponse<RepoMetricsDto> analyzeTeamCommits(
            @PathVariable Long projectId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        RepoMetricsDto metrics = metricsService.analyzeTeamCommits(projectId, from, to);
        return new ApiResponse<>(true, "Team commit analysis completed", metrics);
    }

    @GetMapping("/anonymous-team-commits")
    public ApiResponse<AnonymousRepoMetricsDto> analyzeAnonymousTeamCommits(
            @PathVariable Long projectId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        AnonymousRepoMetricsDto metrics = metricsService.analyzeAnonymousTeamCommits(projectId, from, to);
        return new ApiResponse<>(true, "Anonymous team commit analysis completed", metrics);
    }

    @PostMapping("/compare-periods")
    public ApiResponse<PeriodComparisonDto> comparePeriods(
            @PathVariable Long projectId,
            @RequestBody PeriodComparisonRequest request) {

        PeriodComparisonDto comparison = metricsService.comparePeriods(projectId, request);
        return new ApiResponse<>(true, "Period comparison completed", comparison);
    }
}