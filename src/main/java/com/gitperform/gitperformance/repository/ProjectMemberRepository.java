package com.gitperform.gitperformance.repository;

import com.gitperform.gitperformance.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProjectId(Long projectId);
    List<ProjectMember> findByUserId(Long userId);
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);
    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.role = 'TEAM_LEAD'")
    List<ProjectMember> findTeamLeadsByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.role = 'DEVELOPER'")
    List<ProjectMember> findDevelopersByProjectId(@Param("projectId") Long projectId);

    void deleteByProjectIdAndUserId(Long projectId, Long userId);

    long countByProjectId(Long projectId);
}