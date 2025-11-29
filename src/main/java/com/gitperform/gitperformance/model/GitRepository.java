package com.gitperform.gitperformance.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "git_repositories")
@Getter
@Setter
@NoArgsConstructor
public class GitRepository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String name;
    private String authToken; // В реальном приложении нужно шифровать

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private LocalDateTime connectedAt;
    private LocalDateTime lastSyncAt;

    public GitRepository(String url, String name, Project project) {
        this.url = url;
        this.name = name;
        this.project = project;
        this.connectedAt = LocalDateTime.now();
    }
}