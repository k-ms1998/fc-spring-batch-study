package com.fc.project.api.service;

import com.fc.project.api.dto.EngagementEmail;
import com.fc.project.core.domain.entity.Engagement;

public interface EmailService {
    void sendEngagement(EngagementEmail engagementEmail);
}
