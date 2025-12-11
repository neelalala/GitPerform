package com.gitperform.gitperformance.dto.project;

import com.gitperform.gitperformance.model.Project;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private String repoUrl;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private List<ProjectMemberDto> members;

    public ProjectDto(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.ownerId = project.getOwner() != null ? project.getOwner().getId() : null;
        this.ownerName = project.getOwner() != null ? project.getOwner().getDisplayName() : null;
        this.createdAt = project.getCreatedAt();
        this.repoUrl = project.getRepoUrl();

        if (project.getMembers() != null) {
            this.members = project.getMembers().stream()
                    .map(ProjectMemberDto::new)
                    .toList();
        }
    }
}