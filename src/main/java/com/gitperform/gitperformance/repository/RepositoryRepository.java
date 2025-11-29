package com.gitperform.gitperformance.repository;

import com.gitperform.gitperformance.model.GitRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepositoryRepository extends JpaRepository<GitRepository, Long> {
    List<GitRepository> findByProjectId(Long projectId);

    boolean existsByProjectIdAndUrl(Long projectId, String url);
}
