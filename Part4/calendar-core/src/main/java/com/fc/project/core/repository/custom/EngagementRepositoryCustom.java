package com.fc.project.core.repository.custom;

import com.fc.project.core.domain.entity.Engagement;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EngagementRepositoryCustom {
    List<Engagement> findByAttendeeId(Long attendeeId);
}
