package com.gitperform.gitperformance.controller;

import com.gitperform.gitperformance.dto.ApiResponse;
import com.gitperform.gitperformance.model.ProjectMember;
import com.gitperform.gitperformance.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {
    private final ProjectService projectService;

    public ProjectMemberController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ApiResponse<List<ProjectMember>> getProjectMembers(@PathVariable Long projectId) {
        List<ProjectMember> members = projectService.getProjectMembers(projectId);
        return new ApiResponse<>(true, "Project members retrieved", members);
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Boolean> removeOrLeave(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestParam(required = false) Long removerId) {

        boolean success;
        String message;

        if (removerId == null || removerId.equals(userId)) {
            success = projectService.deleteMember(projectId, userId);
            message = success ? "Left project successfully" : "Failed to leave project";
        } else {
            success = projectService.deleteMember(projectId, userId);
            message = success ? "Member removed successfully" : "Failed to remove member";
        }

        return new ApiResponse<>(success, message, success);
    }
}
