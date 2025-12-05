package com.gitperform.gitperformance.controller;

import com.gitperform.gitperformance.dto.ApiResponse;
import com.gitperform.gitperformance.dto.project.ProjectCreateDto;
import com.gitperform.gitperformance.dto.project.ProjectDto;
import com.gitperform.gitperformance.service.ProjectService;
import com.gitperform.gitperformance.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @PostMapping("/new")
    public ApiResponse<ProjectDto> createProject(@RequestBody ProjectCreateDto project,
                                                 @RequestParam Long userId) {
        var user = userService.findById(userId);
        if (user == null) {
            return new ApiResponse<>(false, "User not found", null);
        }

        var createdProject = projectService.createProject(project, user);
        return new ApiResponse<>(true, "Project created successfully", new ProjectDto(createdProject));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<ProjectDto>> getUserProjects(@PathVariable Long userId) {
        var projects = projectService.getUserProjects(userId).stream().map(ProjectDto::new).toList();
        return new ApiResponse<>(true, "Projects retrieved successfully", projects);
    }

    @PostMapping("/{projectId}/join")
    public ApiResponse<Boolean> joinProject(@PathVariable Long projectId,
                                            @RequestParam Long userId) {
        var user = userService.findById(userId);
        if (user == null) {
            return new ApiResponse<>(false, "User not found", false);
        }

        boolean success = projectService.joinProject(projectId, user);
        return new ApiResponse<>(success,
                success ? "Joined project successfully" : "Failed to join project",
                success);
    }
}
