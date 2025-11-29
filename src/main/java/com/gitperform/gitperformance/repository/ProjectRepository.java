package com.gitperform.gitperformance.repository;

import com.gitperform.gitperformance.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT DISTINCT p FROM Project p JOIN p.members pm WHERE pm.user.id = :userId")
    List<Project> findByUserId(Long userId);
}