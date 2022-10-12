package com.fc.project.api.service.dev;

import com.fc.project.api.service.EmailService;
import com.fc.project.core.domain.entity.Engagement;
import org.springframework.stereotype.Service;

@Service
public class FakeEmailService implements EmailService {

    @Override
    public void sendEngagement(Engagement engagement) {
        System.out.println("Send Email: " + engagement.getAttendee().getEmail()
                + ", Schedule ID: " + engagement.getSchedule().getId());
    }
}
