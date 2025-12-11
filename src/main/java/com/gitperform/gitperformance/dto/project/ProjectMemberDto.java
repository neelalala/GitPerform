package com.gitperform.gitperformance.dto.project;

import com.gitperform.gitperformance.model.ProjectMember;
import lombok.Data;

@Data
public class ProjectMemberDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String displayName;
    private String role;

    public ProjectMemberDto(ProjectMember member) {
        this.id = member.getId();

        if (member.getUser() != null) {
            this.userId = member.getUser().getId();
            this.userName = member.getUser().getUsername();
            this.userEmail = member.getUser().getEmail();
            this.displayName = member.getUser().getDisplayName();
        }

        this.role = member.getRole() != null ? member.getRole().name() : null;
    }
}