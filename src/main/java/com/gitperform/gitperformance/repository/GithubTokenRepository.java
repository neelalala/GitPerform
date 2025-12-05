package com.gitperform.gitperformance.repository;

import com.gitperform.gitperformance.model.GithubToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GithubTokenRepository extends JpaRepository<GithubToken, Long> {
    Optional<GithubToken> findByUserId(Long userId);
}