package com.fc.project.api.service;

import com.fc.project.api.controller.BatchController;
import com.fc.project.api.dto.EngagementEmail;
import com.fc.project.core.domain.entity.Engagement;
import com.fc.project.core.domain.entity.Share;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendEngagement(EngagementEmail engagementEmail) {
        final MimeMessagePreparator preparator = mimeMessage -> {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(engagementEmail.getRecipient());
            helper.setSubject(engagementEmail.getSubject());
            helper.setText(
                    templateEngine.process("engagement-email",
                            new Context(Locale.KOREAN, engagementEmail.getProperties())), true
            );
        };

        emailSender.send(preparator);
    }

    @Override
    public void sendNotification(BatchController.SendMailBatchRequest s) {
        System.out.println("[Send Notification] -> {" + s.toString() + "}");
        final MimeMessagePreparator preparator = mimeMessage -> {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(s.getEmail());
            helper.setSubject(s.getTitle());
            helper.setText(s.toString());
        };

        emailSender.send(preparator);
    }

    @Override
    public void sendShareRequestMail(String email, String name, Share.Direction direction) {
        log.info(new StringBuilder()
                .append("[Share Request] -> {fromEmail:").append(email).append(", toName:").append(name).append(", direction:").append(direction).append("}")
                .toString());
    }
}
