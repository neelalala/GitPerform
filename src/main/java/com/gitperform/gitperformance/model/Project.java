package com.gitperform.gitperformance.model;

import com.gitperform.gitperformance.dto.project.ProjectCreateDto;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(name = "repo_url")
    private String repoUrl;

    @Column(name = "access_token")
    private String token;

    @Column(name = "is_public")
    private boolean isPublic = true;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectMember> members = new ArrayList<>();

    public Project(ProjectCreateDto dto) {
        name = dto.getName();
        description = dto.getDescription();
        repoUrl = dto.getRepoUrl();
        createdAt = LocalDateTime.now();
    }
}
