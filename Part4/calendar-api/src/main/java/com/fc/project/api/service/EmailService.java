package com.fc.project.api.service;

import com.fc.project.api.controller.BatchController;
import com.fc.project.api.dto.EngagementEmail;
import com.fc.project.core.domain.entity.Engagement;
import com.fc.project.core.domain.entity.Share;

public interface EmailService {
    void sendEngagement(EngagementEmail engagementEmail);

    void sendNotification(BatchController.SendMailBatchRequest s);

    void sendShareRequestMail(String email, String name, Share.Direction direction);
}
