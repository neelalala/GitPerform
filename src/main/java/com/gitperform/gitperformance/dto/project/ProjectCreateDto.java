package com.gitperform.gitperformance.dto.project;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ProjectCreateDto {
    private String name;
    private String description;
    private String githubUrl;
}
