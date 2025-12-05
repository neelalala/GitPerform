package com.gitperform.gitperformance.dto.metrics;

import lombok.Data;

@Data
public class GithubAuthDto {
    private String accessToken;
    private String githubUsername;
}