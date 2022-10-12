package com.fc.project.core.repository;

import com.fc.project.core.domain.entity.Engagement;
import com.fc.project.core.repository.custom.EngagementRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EngagementRepository extends JpaRepository<Engagement, Long>, EngagementRepositoryCustom {
}
