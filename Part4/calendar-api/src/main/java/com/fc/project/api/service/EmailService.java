package com.fc.project.api.service;

import com.fc.project.core.domain.entity.Engagement;

public interface EmailService {
    void sendEngagement(Engagement engagement);
}
