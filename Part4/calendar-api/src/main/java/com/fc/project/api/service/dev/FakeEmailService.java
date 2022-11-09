package com.fc.project.api.service.dev;

import com.fc.project.api.controller.BatchController;
import com.fc.project.api.dto.EngagementEmail;
import com.fc.project.api.service.EmailService;
import com.fc.project.core.domain.entity.Engagement;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("test")
@Service
public class FakeEmailService implements EmailService {

    @Override
    public void sendEngagement(EngagementEmail engagementEmail) {
        System.out.println("Send Email: " + engagementEmail.getAttendeeEmails().get(0)
                + ", Schedule ID: " + engagementEmail.getEngagementId());
    }

    @Override
    public void sendNotification(BatchController.SendMailBatchRequest s) {
        System.out.println("Send Notification: " + s.getTitle()
                + " , Email: " + s.getEmail());
    }
}
