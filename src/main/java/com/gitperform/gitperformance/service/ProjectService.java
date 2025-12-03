package com.gitperform.gitperformance.service;

import com.gitperform.gitperformance.model.Project;
import com.gitperform.gitperformance.model.ProjectMember;
import com.gitperform.gitperformance.model.User;
import com.gitperform.gitperformance.repository.ProjectRepository;
import com.gitperform.gitperformance.repository.ProjectMemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    public Project createProject(Project project, User owner) {
        project.setCreatedAt(LocalDateTime.now());
        Project savedProject = projectRepository.save(project);

        ProjectMember ownerMember = new ProjectMember();
        ownerMember.setProject(savedProject);
        ownerMember.setUser(owner);
        ownerMember.setRole(ProjectMember.ProjectRole.TEAM_LEAD);
        projectMemberRepository.save(ownerMember);

        return savedProject;
    }

    public List<Project> getUserProjects(Long userId) {
        return projectRepository.findByUserId(userId);
    }

    public boolean joinProject(Long projectId, User user) {
        // Implementation for joining project
        return true;
    }
}
