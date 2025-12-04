package com.gitperform.gitperformance.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_members")
@Setter
@Getter
@NoArgsConstructor
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ProjectRole role;

    public enum ProjectRole {
        DEVELOPER, TEAM_LEAD
    }
}
