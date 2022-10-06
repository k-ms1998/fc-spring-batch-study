package com.fc.project.core.repository;

import com.fc.project.core.domain.entity.Engagement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EngagementRepository extends JpaRepository<Engagement, Long> {
}
