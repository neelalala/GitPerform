package com.gitperform.gitperformance.dto.project;

import lombok.Data;

@Data
public class ProjectCreateDto {
    private String name;
    private String description;
    private String repoUrl;
}
